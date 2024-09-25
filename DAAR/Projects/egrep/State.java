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
    public void setId(int id) {
        this.id = id; 
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
    public Set<State> getTransitions(char symbol) {
        return transitions.getOrDefault(symbol, new HashSet<>());
    }

    // Obtenir toutes les transitions (pour obtenir l'ensemble des symboles, etc.)
    public Map<Character, Set<State>> getAllTransitions() {
        return transitions;
    }

    public void replaceTransitions(Map<State, State> representative) {
        // Remplacer les transitions par leurs représentants
        Map<Character, Set<State>> newTransitions = new HashMap<>();

        for (Map.Entry<Character, Set<State>> entry : transitions.entrySet()) {
            Character symbol = entry.getKey();
            Set<State> targetStates = entry.getValue();
            Set<State> newTargetStates = new HashSet<>();

            // Remplacer chaque état cible par son représentant
            for (State targetState : targetStates) {
                State representativeState = representative.get(targetState);
                if (representativeState != null) { // Vérifier que le représentant n'est pas null
                    newTargetStates.add(representativeState);
                } else {
                    System.err.println("Aucun représentant trouvé pour l'état " + targetState);
                }
            }

            newTransitions.put(symbol, newTargetStates);
        }

        // Mettre à jour les transitions avec les nouveaux états cibles
        this.transitions = newTransitions;

        // Traiter les transitions epsilon
        Set<State> newEpsilonTransitions = new HashSet<>();
        for (State epsilonState : epsilonTransitions) {
            State representativeEpsilonState = representative.get(epsilonState);
            if (representativeEpsilonState != null) {
                newEpsilonTransitions.add(representativeEpsilonState);
            } else {
                System.err.println("Aucun représentant trouvé pour l'état epsilon " + epsilonState);
            }
        }

        this.epsilonTransitions = newEpsilonTransitions;
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