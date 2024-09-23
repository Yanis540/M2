import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class NDFA {
    private Set<State> states;
    private int stateCounter;
    public static final char EPSILON = 'ε'; // Transition epsilon
    private StatePair ndfaPair;
    // Constructeur NDFA
    public NDFA() {
        this.states = new HashSet<>();
        this.stateCounter = 0;
    }

    // Méthode pour créer un NDFA à partir d'un arbre RegexTree
    public void fromRegexTree(RegExTree tree) {
        // Réinitialisation de l'automate
        this.states.clear();
        this.stateCounter = 0;

        // Appel de la fonction récursive pour créer les états et transitions
        StatePair ndfaPair = buildNDFA(tree);

        // Définir l'état de départ et marquer l'état final
        ndfaPair.end.setFinal(true);
        this.ndfaPair = ndfaPair; 
    }
    public State getStartState(){
        return ndfaPair.start;
    }

    // Fonction récursive pour construire le NDFA à partir du RegexTree
    private StatePair buildNDFA(RegExTree tree) {
        switch (tree.root) {
            case RegEx.CONCAT:
                StatePair leftNDFA = buildNDFA(tree.subTrees.get(0));
                StatePair rightNDFA = buildNDFA(tree.subTrees.get(1));
                leftNDFA.end.addEpsilontransition(rightNDFA.start); // Transition epsilon
                return new StatePair(leftNDFA.start, rightNDFA.end);

            case RegEx.ALTERN:
                State start = createState();
                State end = createState();
                StatePair leftAlt = buildNDFA(tree.subTrees.get(0));
                StatePair rightAlt = buildNDFA(tree.subTrees.get(1));
                start.addEpsilontransition(leftAlt.start);
                start.addEpsilontransition(rightAlt.start);
                leftAlt.end.addEpsilontransition(end);
                rightAlt.end.addEpsilontransition(end);
                return new StatePair(start, end);

            case RegEx.ETOILE:
                start = createState();
                end = createState();
                StatePair starNDFA = buildNDFA(tree.subTrees.get(0));
                start.addEpsilontransition(starNDFA.start);
                start.addEpsilontransition(end); // Transition epsilon pour le saut
                starNDFA.end.addEpsilontransition(starNDFA.start);
                starNDFA.end.addEpsilontransition(end);
                return new StatePair(start, end);

            default: // Cas des symboles simples
                start = createState();
                end = createState();
                start.addTransition((char) tree.root, end);
                return new StatePair(start, end);
        }
    }

    // Méthode pour créer un nouvel état
    private State createState() {
        State state = new State(stateCounter++);
        this.states.add(state);
        return state;
    }

    // Affichage simple des états et transitions
    public void print() {
        System.out.println("NDFA:");
        for (State state : states) {
            System.out.println(state);
            for (Map.Entry<Character, Set<State>> entry : state.getAllTransitions().entrySet()) {
                for (State toState : entry.getValue()) {
                    System.out.println("  " + state.getId() + " -- " + entry.getKey() + " --> " + toState.getId());
                }
            }
            for (State epsilonState : state.getEpsilonTransitions()) {
                System.out.println("  " + state.getId() + " -- ε --> " + epsilonState.getId());
            }
        }
    }

    // Méthode pour générer un fichier Dot pour visualiser l'NDFA avec Graphviz
    public void toDotFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph NDFA {\n");
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

                for (State epsilonState : state.getEpsilonTransitions()) {
                    writer.write("  " + state.getId() + " -> " + epsilonState.getId() + " [label=\"ε\"];\n");
                }
            }

            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier Dot: " + e.getMessage());
        }
    }
}
