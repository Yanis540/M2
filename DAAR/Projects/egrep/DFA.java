import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DFA {
    private Set<State> states;
    private State startState;
    private Set<State> finalStates;
    private Map<Set<State>, State> dfaStateMapping;
    private int stateIdCounter; // Compteur pour générer des IDs d'état uniques
    // Méthode pour générer un ID d'état unique
    private int generateStateId() {
        return stateIdCounter++; // Retourner l'ID actuel et l'incrémenter
    }
    // Constructeur DFA vide
    public DFA() {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.dfaStateMapping = new HashMap<>();
        this.stateIdCounter = 0; // Initialiser le compteur
    }
    public DFA(NDFA ndfa) {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.dfaStateMapping = new HashMap<>();
        this.stateIdCounter = 0; // Initialiser le compteur
        fromNDFA(ndfa);
    }

    // Méthode pour construire une DFA à partir d'un NDFA
    public void fromNDFA(NDFA ndfa) {
        
        Queue<Set<State>> worklist = new LinkedList<>();
        this.dfaStateMapping = new HashMap<>();
        
        // Déterminiser le NDFA en DFA
        Set<State> startSet = epsilonClosure(Collections.singleton(ndfa.getStartState()));
        worklist.add(startSet);
        
        // Créer l'état DFA initial
        State startDFAState = new State(this.generateStateId());
        states.add(startDFAState);
        this.dfaStateMapping.put(startSet, startDFAState);
        this.startState = startDFAState;

        // Marquer l'état final du DFA si nécessaire
        if (startSet.stream().anyMatch(State::isFinal)) {
            startDFAState.setFinal(true);
        }

        while (!worklist.isEmpty()) {
            
            Set<State> currentSet = worklist.poll();
            State currentDFAState = this.dfaStateMapping.get(currentSet);

            // Gérer les transitions pour chaque symbole
            Map<Character, Set<State>> newTransitions = new HashMap<>();
            System.out.println("Current DFA State: " + currentDFAState.getId());
            for (State ndfaState : currentSet) {
                System.out.println("\tProcessing NDFA State : " + ndfaState.getId());
                for (Map.Entry<Character, Set<State>> entry : ndfaState.getAllTransitions().entrySet()) {
                    System.out.println("\t\tProcessing input: " + entry.getKey());
                    System.out.println("\t\tTarget state set: " + entry.getValue());
                    Character symbol = entry.getKey();
                    Set<State> targetStates = entry.getValue();
                    Set<State> targetClosure = epsilonClosure(targetStates);
                    // Créer la transition dans le DFA
                   
                    newTransitions.putIfAbsent(symbol, new HashSet<>());
                    newTransitions.get(symbol).addAll(targetClosure);
                    System.out.println("\t\tNew DFA Transitions: " + newTransitions);
                    System.out.println("\t\tTarget Closure: " + targetClosure);
                    // Si l'ensemble cible n'existe pas encore, le créer et l'ajouter à la worklist
                    if (!this.dfaStateMapping.containsKey(targetClosure)) {
                        State newDFAState = new State(this.generateStateId());
                        this.dfaStateMapping.put(targetClosure, newDFAState);
                        states.add(newDFAState);
                        worklist.add(targetClosure);

                        // Vérifier si cet état doit être marqué comme final
                        if (targetClosure.stream().anyMatch(State::isFinal)) {
                            newDFAState.setFinal(true);
                        }
                        System.out.println("\t\tCreated new DFA state: " + newDFAState.getId());
                        System.out.println("\t\tUpdated Mapping:  " + this.dfaStateMapping);
                    }else {
                    }
                }
            }
            
            // Appliquer les transitions DFA
            for (Map.Entry<Character, Set<State>> entry : newTransitions.entrySet()) {
                Character inputSymbol = entry.getKey();
                Set<State> targetSet = entry.getValue();
                State targetDFAState;

                // Vérifier si l'état cible a déjà été créé
                if (!this.dfaStateMapping.containsKey(targetSet)) {
                    targetDFAState = new State(this.generateStateId());
                    this.dfaStateMapping.put(targetSet, targetDFAState);
                    states.add(targetDFAState);
                    worklist.add(targetSet);

                    // Vérifier si cet état doit être marqué comme final
                    if (targetSet.stream().anyMatch(State::isFinal)) {
                        targetDFAState.setFinal(true);
                    }
                    System.out.println("\t\tCreated new DFA state: " + targetDFAState.getId());
                } else {
                    targetDFAState = this.dfaStateMapping.get(targetSet);
                }

                // Ajouter la transition
                currentDFAState.addTransition(inputSymbol, targetDFAState);
                System.out.println("\t\tTransition added from " + currentDFAState.getId() + " to " + targetDFAState.getId());
            }
        }
        // Appliquer la minimisation du DFA
        this.minimize();
        this.renameStates();
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
    public void renameStates() {
        int counter = 0;
    
        
        // Parcourir tous les états du DFA et les renommer
        for (State state : states) {
            state.setId(counter++); // Remettre l'identifiant à zéro et l'incrémenter
        }
        // Si vous avez un état de départ spécifique, vous pouvez le gérer ici
        if (this.startState != null) {
            this.startState.setId(0); // Assurer que l'état de départ a l'identifiant 0
        }
    
    
        System.out.println("States have been renamed from 0 to " + (counter - 1));
    }
    
    // Méthode pour vérifier si un string est accepté par la DFA (correspond à un état final)
    public boolean find(String input) {
        State currentState = this.startState;
        for (char c : input.toCharArray()) {
            Set<State> nextStates = currentState.getTransitions(c);
            System.out.println(currentState+"->Next state"+nextStates);
            if(currentState.isFinal())
                return true; 
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