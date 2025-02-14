package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DefaultTeamMineWithout {
  private HashMap<Point, Integer> degrees = new HashMap<Point, Integer>();
  private Evaluation evaluation = new Evaluation();

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = initializeFVS(points, edgeThreshold);

    System.out.println("Initial FVS size: " + bestFvs.size());

    boolean improved = true;
    while (improved) {
      ArrayList<Point> mutatedFvs = mutateFVS(new ArrayList<>(bestFvs), points, edgeThreshold);
      if (mutatedFvs.size() < bestFvs.size()) {
        bestFvs = mutatedFvs;
        System.out.println("Improved FVS size: " + bestFvs.size());
      } else {
        improved = false;
      }
    }

    return bestFvs;
  }

  // Fonction d'initialisation du FVS en utilisant la HashMap pour stocker les
  // degrés
  public ArrayList<Point> initializeFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs = new ArrayList<>();
    setDegrees(points, edgeThreshold);

    HashMap<Point, Integer> currentDegrees = new HashMap<>(degrees);

    while (!currentDegrees.isEmpty()) {
      // Trouver le sommet avec le degré maximum
      Point maxDegreePoint = currentDegrees.entrySet().stream()
          .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
          .get().getKey();
      
      // Ajouter ce sommet à l'ensemble FVS
      fvs.add(maxDegreePoint);

      // Supprimer le sommet de la HashMap et mettre à jour les degrés des voisins
      currentDegrees.remove(maxDegreePoint);
      updateDegrees(currentDegrees, maxDegreePoint, points, edgeThreshold);

      // Vérifier si fvs est une solution valide
      if (evaluation.isValid(points, fvs, edgeThreshold)) {
        break;
      }
    }

    return fvs;
  }

  // Met à jour les degrés des voisins du sommet supprimé
  private void updateDegrees(HashMap<Point, Integer> currentDegrees, Point removedPoint, ArrayList<Point> points,
      int edgeThreshold) {
    for (Point neighbor : evaluation.neighbor(removedPoint, points, edgeThreshold)) {
      if (currentDegrees.containsKey(neighbor)) {
        currentDegrees.put(neighbor, currentDegrees.get(neighbor) - 1);
      }
    }
  }

  // Fonction de mutation pour tenter de réduire la taille du FVS
  public ArrayList<Point> mutateFVS(ArrayList<Point> fvs, ArrayList<Point> points, int edgeThreshold) {
    Random random = new Random();
    if (fvs.isEmpty())
      return fvs; // Rien à muter

    int indexToRemove = random.nextInt(fvs.size());
    Point removedPoint = fvs.remove(indexToRemove);

    if (evaluation.isValid(points, fvs, edgeThreshold)) {
      // Mutation réussie, on garde la nouvelle solution
      return fvs;
    } else {
      // Mutation échouée, on remet le point dans le FVS
      fvs.add(removedPoint);
      return fvs;
    }
  }

  // Initialise les degrés pour tous les sommets
  public void setDegrees(ArrayList<Point> points, int edgeThreshold) {
    degrees.clear();
    for (Point p : points) {
      degrees.put(p, evaluation.neighbor(p, points, edgeThreshold).size());
    }
  }

}
