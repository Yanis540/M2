import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DFA {
    private Set<State> states;
    private State startState;
    private Set<State> finalStates;
    private Map<Set<State>, State> dfaStateMapping;

    // Constructeur DFA vide
    public DFA() {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.dfaStateMapping = new HashMap<>();
    }
    public DFA(NDFA ndfa) {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.dfaStateMapping = new HashMap<>();
        fromNDFA(ndfa);
    }

    // Méthode pour construire une DFA à partir d'un NDFA
    public void fromNDFA(NDFA ndfa) {
        Queue<Set<State>> worklist = new LinkedList<>();
        Map<Set<State>, Map<Character, Set<State>>> transitions = new HashMap<>();
        Map<Set<State>, Boolean> marked = new HashMap<>();
        
        // Déterminiser le NDFA en DFA
        Set<State> startSet = epsilonClosure(Collections.singleton(ndfa.getStartState()));
        worklist.add(startSet);
        marked.put(startSet, false);
        
        State startDFAState = new State(0);
        states.add(startDFAState);
        dfaStateMapping.put(startSet, startDFAState);
        this.startState = startDFAState;

        while (!worklist.isEmpty()) {
            Set<State> currentSet = worklist.poll();
            State currentDFAState = dfaStateMapping.get(currentSet);
            marked.put(currentSet, true);

            // Si l'un des états de l'ensemble est un état final du NDFA, marquer l'état DFA comme final
            if (currentSet.stream().anyMatch(State::isFinal)) {
                currentDFAState.setFinal(true);
                finalStates.add(currentDFAState);
            }

            // Gérer les transitions pour chaque symbole
            Map<Character, Set<State>> newTransitions = new HashMap<>();
            for (State ndfaState : currentSet) {
                for (Map.Entry<Character, Set<State>> entry : ndfaState.getAllTransitions().entrySet()) {
                    Character symbol = entry.getKey();
                    Set<State> targetStates = entry.getValue();
                    Set<State> targetClosure = epsilonClosure(targetStates);

                    newTransitions.putIfAbsent(symbol, new HashSet<>());
                    newTransitions.get(symbol).addAll(targetClosure);

                    // Si le nouvel ensemble d'états n'est pas marqué, on l'ajoute à la worklist
                    if (!dfaStateMapping.containsKey(targetClosure)) {
                        State newDFAState = new State(states.size());
                        dfaStateMapping.put(targetClosure, newDFAState);
                        states.add(newDFAState);
                        marked.put(targetClosure, false);
                        worklist.add(targetClosure);
                    }
                }
            }

            // Appliquer les transitions DFA
            for (Map.Entry<Character, Set<State>> entry : newTransitions.entrySet()) {
                currentDFAState.addTransition(entry.getKey(), dfaStateMapping.get(entry.getValue()));
            }
        }
        // Appliquer la minimisation du DFA
        this.minimize();
    }

    // Fonction pour calculer la fermeture epsilon d'un ensemble d'états
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Queue<State> worklist = new LinkedList<>(states);

        while (!worklist.isEmpty()) {
            State current = worklist.poll();
            for (State epsilonState : current.getEpsilonTransitions()) {
                if (!closure.contains(epsilonState)) {
                    closure.add(epsilonState);
                    worklist.add(epsilonState);
                }
            }
        }
        return closure;
    }

    // Méthode pour vérifier si un string est accepté par la DFA (correspond à un état final)
    public boolean find(String input) {
        State currentState = startState;

        for (char c : input.toCharArray()) {
            Set<State> nextStates = currentState.getTransitions(c);
            if (nextStates.isEmpty()) {
                return false; // Aucune transition possible pour ce symbole
            }
            currentState = nextStates.iterator().next(); // Se déplacer vers le nouvel état
        }
        return currentState.isFinal();
    }
//! #######################################"##############"
    // Minimisation du DFA : algorithme de minimisation des états
    private void minimize() {
        Set<State> nonFinalStates = new HashSet<>(states);
        nonFinalStates.removeAll(finalStates);

        // Partitionner les états en deux groupes : finaux et non finaux
        Set<Set<State>> partitions = new HashSet<>();
        partitions.add(nonFinalStates);
        partitions.add(finalStates);

        boolean changed = true;
        while (changed) {
            changed = false;
            Set<Set<State>> newPartitions = new HashSet<>();

            for (Set<State> group : partitions) {
                Map<Map<Character, State>, Set<State>> transitionGroups = new HashMap<>();

                for (State state : group) {
                    Map<Character, State> transitions = new HashMap<>();
                    for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                        transitions.put(entry.getKey(), findPartition(entry.getValue().iterator().next(), partitions));
                    }
                    transitionGroups.putIfAbsent(transitions, new HashSet<>());
                    transitionGroups.get(transitions).add(state);
                }

                newPartitions.addAll(transitionGroups.values());
                if (transitionGroups.size() > 1) {
                    changed = true;
                }
            }

            partitions = newPartitions;
        }

        // Fusionner les états équivalents
        mergeEquivalentStates(partitions);
    }
    // Trouver à quel groupe (partition) appartient un état donné
    private State findPartition(State state, Set<Set<State>> partitions) {
        for (Set<State> partition : partitions) {
            if (partition.contains(state)) {
                return partition.iterator().next();
            }
        }
        return null;
    }

    // Fusionner les états équivalents après la minimisation
    private void mergeEquivalentStates(Set<Set<State>> partitions) {
        Map<State, State> representative = new HashMap<>();

        for (Set<State> partition : partitions) {
            State rep = partition.iterator().next();
            for (State state : partition) {
                representative.put(state, rep);
            }
        }

        // Réassigner les transitions vers les représentants
        for (State state : states) {
            state.replaceTransitions(representative);
        }

        // Supprimer les états non représentatifs
        states.removeIf(state -> !representative.containsKey(state) || representative.get(state) != state);
    }
//! #######################################"##############"

    // Affichage simple des états et transitions de la DFA
    public void print() {
        System.out.println("DFA:");
        for (State state : states) {
            System.out.println(state);
            for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                for (State toState : entry.getValue()) {
                    System.out.println("  " + state.getId() + " -- " + entry.getKey() + " --> " + toState.getId());
                }
            }
        }
    }

    // Méthode pour générer un fichier Dot pour visualiser la DFA avec Graphviz
    public void toDotFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph DFA {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape = circle];\n");

            for (State state : states) {
                if (state.isFinal()) {
                    writer.write("  " + state.getId() + " [shape = doublecircle];\n");
                }

                for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                    for (State toState : entry.getValue()) {
                        writer.write("  " + state.getId() + " -> " + toState.getId() + " [label=\"" + entry.getKey() + "\"];\n");
                    }
                }
            }

            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier Dot: " + e.getMessage());
        }
    }
}