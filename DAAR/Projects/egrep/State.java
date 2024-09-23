import java.util.*;

class State {
    private int id;
    private boolean isFinal;
    private Map<Character, Set<State>> transitions;
    private Set<State> epsilonTransitions;

    // Constructeur
    public State(int id) {
        this.id = id;
        this.isFinal = false;
        this.transitions = new HashMap<>();
        this.epsilonTransitions = new HashSet<>();
    }

    // Accesseurs et mutateurs
    public int getId() {
        return id;
    }
    // Accesseurs et mutateurs
    public Set<State> getEpsilonTransitions() {
        return this.epsilonTransitions;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    // Ajouter une transition
    public void addTransition(char symbol, State toState) {
        transitions.putIfAbsent(symbol, new HashSet<>());
        transitions.get(symbol).add(toState);
    }
    public void addEpsilontransition(State state) {
        epsilonTransitions.add(state);
    }
    // Récupérer les transitions pour un symbole donné
    public Set<State> getTransitions(int symbol) {
        return transitions.getOrDefault(symbol, new HashSet<>());
    }
   

    // Obtenir toutes les transitions (pour obtenir l'ensemble des symboles, etc.)
    public Map<Character, Set<State>> getAllTransitions() {
        return transitions;
    }

    // Surcharger toString pour un affichage simple
    public String toString() {
        return "State " + id + (isFinal ? " [final]" : "");
    }
}
// Classe auxiliaire pour représenter une paire d'états (début, fin)
class StatePair {
    public State start;
    public State end;

    public StatePair(State start, State end) {
        this.start = start;
        this.end = end;
    }
}