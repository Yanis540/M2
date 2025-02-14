package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DefaultTeam {
  private HashMap<Point, Integer> degrees = new HashMap<Point, Integer>();
  private Evaluation evaluation = new Evaluation();

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = initializeFVS(points, edgeThreshold);

    System.out.println("Initial FVS size: " + bestFvs.size());

    for (int i = 0; i < 5; i++) { // On lance plusieurs améliorations
      System.out.println("Iter " + (i + 1) + ": STarting local search ");
      ArrayList<Point> newFvs = localSearch(bestFvs, points, edgeThreshold);
      System.out.println("Iter " + (i + 1) + ": Found FVS size " + newFvs.size());
      if (newFvs.size() < bestFvs.size()) {
        bestFvs = newFvs;
      }
    }

    return bestFvs;
  }

  // Étape 2 : Amélioration locale (remove 2, add 1)
  private ArrayList<Point> localSearch(ArrayList<Point> currentFvs, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = new ArrayList<>(currentFvs);
    boolean improved = true;

    while (improved) {
      improved = false;
      System.out.println("Trying to improve local search");
      ArrayList<Point> candidate = remove2add1(new ArrayList<>(bestFvs), points, edgeThreshold);

      if (candidate.size() < bestFvs.size()) {
        bestFvs = candidate;
        improved = true;
        System.out.println("Local search improvement: " + bestFvs.size());
      }
    }

    return bestFvs;
  }

  // Stratégie de transition : remove 2 points, add 1
  private ArrayList<Point> remove2add1(ArrayList<Point> fvs, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = new ArrayList<>(fvs);
    for (Point p1 : fvs) {
      for (Point p2 : fvs) {
        if (p1.equals(p2))
          continue;
        bestFvs.remove(p1);
        bestFvs.remove(p2);
        int tmp = bestFvs.size();
        int attempts = 20; 
        while(attempts-- > 0) {
          Point other_point = points.get(new Random().nextInt(points.size()));
          bestFvs.add(other_point);
          if (evaluation.isValid(points, bestFvs, edgeThreshold))
            return bestFvs;
          bestFvs.remove(tmp);
        }
        bestFvs.add(p1);
        bestFvs.add(p2);
      }
    }

    return bestFvs;
  }

  private Point getMinimumDegree(ArrayList<Point> points, int edgeThreshold) {
    return points.stream()
      .min((p1, p2) -> evaluation.neighbor(p1, points, edgeThreshold).size()- evaluation.neighbor(p2, points, edgeThreshold).size())
      .get()
    ;
  }

  // Fonction d'initialisation du FVS en utilisant la HashMap pour stocker les
  // degrés
  public ArrayList<Point> initializeFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs;
    ArrayList<Point> currentDegrees = new ArrayList<>(points);
    ArrayList<Point> pointsToRemove = new ArrayList<>();

    for (Point p : points) {
      boolean isOutlier = evaluation.neighbor(p, points, edgeThreshold).size() < 2;
      if (isOutlier) {
        pointsToRemove.add(p);
        currentDegrees.remove(p);
      }
    }

    while (!currentDegrees.isEmpty()) {
      Point point = getMinimumDegree(currentDegrees, edgeThreshold);
      ArrayList<Point> tmp = new ArrayList<>(points);
      pointsToRemove.add(point);
      currentDegrees.remove(point);
      tmp.removeAll(pointsToRemove);
      boolean isValidFvs = evaluation.isValid(points, tmp, edgeThreshold);
      if (!isValidFvs) {
        // en gros si ça casse on garde le point 
        pointsToRemove.remove(point);
      }
    }

    fvs = new ArrayList<>(points);
    fvs.removeAll(pointsToRemove);
    return fvs;

  }

  // cette fonction ne marche pas très bien car elle n'enlève pas les outliers 
  private ArrayList<Point> initMaxDegree(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs = new ArrayList<>();
    setDegrees(points, edgeThreshold);

    HashMap<Point, Integer> currentDegrees = new HashMap<>(degrees);

    while (!currentDegrees.isEmpty()) {
      Point maxDegreePoint = currentDegrees.entrySet().stream()
        .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
        .get().getKey();

      fvs.add(maxDegreePoint);

      currentDegrees.remove(maxDegreePoint);
      updateDegrees(currentDegrees, maxDegreePoint, points, edgeThreshold);

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

  // Initialise les degrés pour tous les sommets
  public void setDegrees(ArrayList<Point> points, int edgeThreshold) {
    degrees.clear();
    for (Point p : points) {
      degrees.put(p, evaluation.neighbor(p, points, edgeThreshold).size());
    }
  }

}
