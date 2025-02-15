package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DefaultTeam {
  private HashMap<Point, Integer> degrees = new HashMap<Point, Integer>();
  private Evaluation evaluation = new Evaluation();

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = initializeFVSParallel(points, edgeThreshold);
    System.out.println("Initial FVS size: " + bestFvs.size());

    for (int i = 0; i < 10; i++) { // On lance plusieurs améliorations
      System.out.println("Iter " + (i + 1) + ": STarting local search ");
      ArrayList<Point> newFvs = localSearch(bestFvs, points, edgeThreshold);
      System.out.println("Iter " + (i + 1) + ": Found FVS size " + newFvs.size() + ", best FVS size: " + bestFvs.size());
      if (newFvs.size() < bestFvs.size()) {
        bestFvs = newFvs;
      }
    }

    return bestFvs;
  }

  private ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
    ArrayList<Point> result = (ArrayList<Point>) points.clone();
    for (int i = 0; i < result.size(); i++) {
      for (int j = i + 1; j < result.size(); j++)
        if (result.get(i).equals(result.get(j))) {
          result.remove(j);
          j--;
        }
    }
    return result;
  }

  // Étape 2 : Amélioration locale (remove 2, add 1)
  private ArrayList<Point> localSearch(ArrayList<Point> currentFvs, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> current = removeDuplicates(currentFvs);
    ArrayList<Point> next = (ArrayList<Point>)current.clone();

    System.out.println("LS. First sol: " + current.size());

    do {
      current = next;
      next = remove2add1Parallel(current, points,edgeThreshold);
      if(current.size()>=next.size()) {
        next = removeUnecassaryPoint(next, points, edgeThreshold);
      }
      System.out.println("LS. Current sol: " + current.size() + ". Found next sol: "+next.size());
    } while (current.size()>next.size());
    
    System.out.println("LS. Last sol: " + current.size());
    return next;
  }
  private boolean isEdge(Point p, Point q, int edgeThreshold) {
    return p.distance(q)<edgeThreshold;
  }

  private boolean isSolution(ArrayList<Point> pointsIn,ArrayList<Point> candidateIn, int edgeThreshold) {
    ArrayList<Point> candidate = removeDuplicates(candidateIn);
    ArrayList<Point> rest = removeDuplicates(pointsIn);
    rest.removeAll(candidate);
    ArrayList<Point> visited = new ArrayList<Point>();

    while (!rest.isEmpty()) {
      visited.clear();
      visited.add(rest.remove(0));
      for (int i=0;i<visited.size();i++) {
        for (Point p: rest) if (isEdge(visited.get(i),p,edgeThreshold)) {
          for (Point q: visited) if (!q.equals(visited.get(i)) && isEdge(p,q,edgeThreshold)) return false;
          visited.add(p);
        }
        rest.removeAll(visited);
      }
    }

    return true;
  }

  private ArrayList<Point> removeUnecassaryPoint(ArrayList<Point> fvs, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = new ArrayList<>(fvs);
    for (Point p : fvs) {
      bestFvs.remove(p);
      if (!isSolution(points, bestFvs, edgeThreshold)) {
        bestFvs.add(p);
      }
    }
    return bestFvs;
  }
  // Stratégie de transition : remove 2 points, add 1
  public ArrayList<Point> remove2add1Parallel(ArrayList<Point> fvs, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> bestFvs = removeDuplicates(fvs);
    ArrayList<Point> rest = removeDuplicates(points);
    rest.removeAll(bestFvs);
    long seed = System.nanoTime();
    Collections.shuffle(bestFvs, new Random(seed));
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<ArrayList<Point>>> futures = new ArrayList<>();
    int maxAttempts = points.size() / 3; // Nombre total d'essais en parallèle

    for (Point p1 : fvs) {
      for (Point p2 : fvs) {
        if (p1.equals(p2))
          continue;
        bestFvs.remove(p1);
        bestFvs.remove(p2);

        for (int i = 0; i < maxAttempts; i++) {
          Callable<ArrayList<Point>> task = () -> {
            ArrayList<Point> localFvs = new ArrayList<>(bestFvs);
            Point randomPoint;
            do {
              randomPoint = points.get(new Random().nextInt(points.size()));
            } while (randomPoint.equals(p1) || randomPoint.equals(p2));

            localFvs.add(randomPoint);
            if (isSolution(points, localFvs, edgeThreshold)) {
              return localFvs; // Retourne dès qu'une solution est trouvée
            }
            localFvs.remove(randomPoint);
            return null; // Aucune solution trouvée
          };
          futures.add(executor.submit(task));
        }

        bestFvs.add(p1);
        bestFvs.add(p2);
      }
    }

    // Récupération de la première solution valide trouvée
    try {
      for (Future<ArrayList<Point>> future : futures) {
        ArrayList<Point> result = future.get();
        if (result != null) {
          executor.shutdownNow(); // Stoppe tous les threads dès qu'on trouve une solution
          return result;
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      // e.printStackTrace();
    } finally {
      executor.shutdown();
    }

    return bestFvs; // Retourne l'original si aucune meilleure solution n'est trouvée
  }

  private Point getMinimumDegree(ArrayList<Point> points, int edgeThreshold) {
    return points.stream()
        .min((p1, p2) -> evaluation.neighbor(p1, points, edgeThreshold).size()
            - evaluation.neighbor(p2, points, edgeThreshold).size())
        .get();
  }

  public ArrayList<Point> initializeFVSParallel(ArrayList<Point> points, int edgeThreshold) {
    int numThreads = 100;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<ArrayList<Point>>> futures = new ArrayList<>();

    for (int i = 0; i < numThreads; i++) {
      futures.add(executor.submit(() -> initializeFVS(points, edgeThreshold)));
    }

    ArrayList<Point> bestFvs = null;

    try {
      for (Future<ArrayList<Point>> future : futures) {
        ArrayList<Point> result = future.get();
        if (bestFvs == null || result.size() < bestFvs.size()) {
          bestFvs = result;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      executor.shutdown();
    }

    return bestFvs;
  }
  public ArrayList<Point> initializeFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs;
    ArrayList<Point> currentDegrees = new ArrayList<>(points);
    Collections.shuffle(currentDegrees, new Random(System.nanoTime()));

    ArrayList<Point> pointsToRemove = new ArrayList<>();
    
    while (!currentDegrees.isEmpty()) {
      Point point = getMinimumDegree(currentDegrees, edgeThreshold);
      ArrayList<Point> tmp = new ArrayList<>(points);
      pointsToRemove.add(point);
      currentDegrees.remove(point);
      tmp.removeAll(pointsToRemove);
      boolean isValidFvs = isSolution(points, tmp, edgeThreshold);
      if (!isValidFvs) {
        // en gros si ça casse on garde le point
        pointsToRemove.remove(point);
      }
    }

    fvs = new ArrayList<>(points);
    fvs.removeAll(pointsToRemove);
    return fvs;

  }

  

}
