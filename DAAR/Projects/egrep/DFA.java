import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DFA {
    private State startState;
    private Set<State> states;
    
    // Constructeur
    public DFA(State startState, Set<State> states) {
        this.startState = startState;
        this.states = states;
    }

    // Méthode pour convertir un NDFA en DFA
    public static DFA fromNDFA(NDFA ndfa) {
        Map<Set<State>, State> dfaStateMapping = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();
        Set<State> dfaStates = new HashSet<>();

        // Fermeture epsilon de l'état initial du NDFA
        Set<State> startNDFASet = epsilonClosure(ndfa.getStartState());
        State dfaStartState = new State(generateStateId());
        dfaStateMapping.put(startNDFASet, dfaStartState);
        queue.add(startNDFASet);
        dfaStates.add(dfaStartState);

        while (!queue.isEmpty()) {
            Set<State> currentNDFAStates = queue.poll();
            State currentDFAState = dfaStateMapping.get(currentNDFAStates);

            // Vérifier si cet ensemble contient un état final
            for (State ndfaState : currentNDFAStates) {
                if (ndfaState.isFinal()) {
                    currentDFAState.setFinal(true);
                    break;
                }
            }

            // Transition pour chaque symbole
            Map<Character, Set<State>> transitions = new HashMap<>();

            for (State ndfaState : currentNDFAStates) {
                for (Map.Entry<Character, Set<State>> transition : ndfaState.getAllTransitions().entrySet()) {
                    char symbol = transition.getKey();
                    Set<State> targetStates = transition.getValue();

                    Set<State> epsilonClosure = epsilonClosure(targetStates);
                    transitions.putIfAbsent(symbol, new HashSet<>());
                    transitions.get(symbol).addAll(epsilonClosure);
                }
            }

            // Ajouter les transitions au DFA
            for (Map.Entry<Character, Set<State>> transition : transitions.entrySet()) {
                char symbol = transition.getKey();
                Set<State> targetNDFAStates = transition.getValue();

                if (!dfaStateMapping.containsKey(targetNDFAStates)) {
                    State newDFAState = new State(generateStateId());
                    dfaStateMapping.put(targetNDFAStates, newDFAState);
                    queue.add(targetNDFAStates);
                    dfaStates.add(newDFAState);
                }

                currentDFAState.addTransition(symbol, dfaStateMapping.get(targetNDFAStates));
            }
        }

        return new DFA(dfaStartState, dfaStates);
    }

    // Fermeture epsilon pour un état
    private static Set<State> epsilonClosure(State state) {
        Set<State> closure = new HashSet<>();
        Stack<State> stack = new Stack<>();
        stack.push(state);
        closure.add(state);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            Set<State> epsilonTransitions = current.getTransitions(NDFA.EPSILON); // Transition epsilon
            if (epsilonTransitions != null) {
                for (State epsilonState : epsilonTransitions) {
                    if (!closure.contains(epsilonState)) {
                        closure.add(epsilonState);
                        stack.push(epsilonState);
                    }
                }
            }
        }

        return closure;
    }

    // Fermeture epsilon pour un ensemble d'états
    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>();
        for (State state : states) {
            closure.addAll(epsilonClosure(state));
        }
        return closure;
    }

    // Générer un nouvel ID pour les états du DFA
    private static int stateCounter = 0;
    private static int generateStateId() {
        return stateCounter++;
    }

    // Méthode print pour afficher les états et transitions du DFA
    public void print() {
        System.out.println("DFA:");
        for (State state : states) {
            System.out.println(state);
            for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                for (State target : entry.getValue()) {
                    System.out.println("  Transition: " + state.getId() + " -- " + entry.getKey() + " --> " + target.getId());
                }
            }
        }
    }

    // Méthode pour générer un fichier .dot pour le DFA
    public void toDotFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph DFA {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape=circle];\n");

            // États initiaux
            writer.write("  start [shape=none, label=\"\"];\n");
            writer.write("  start -> " + startState.getId() + ";\n");

            // États finaux
            for (State state : states) {
                if (state.isFinal()) {
                    writer.write("  " + state.getId() + " [shape=doublecircle];\n");
                }
            }

            // Transitions
            for (State state : states) {
                for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                    for (State dest : entry.getValue()) {
                        writer.write("  " + state.getId() + " -> " + dest.getId() + " [label=\"" + entry.getKey() + "\"];\n");
                    }
                }
            }

            writer.write("}\n");
            System.out.println("Dot file '" + filename + "' created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
