package Regex;
import java.util.ArrayList;

public class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;

    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    // FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty())
            return rootToString();
        String result = rootToString() + "(" + subTrees.get(0).toString();
        for (int i = 1; i < subTrees.size(); i++)
            result += "," + subTrees.get(i).toString();
        return result + ")";
    }
    public boolean isTreeConcat() {
        if (root != RegEx.CONCAT && !(0 < root && root < 128)) {
          return false;
        }
        for (RegExTree subTree : subTrees) {
          if (!subTree.isTreeConcat()) {
            return false;
          }
        }
        return true;
      }

    private String rootToString() {
        if (root == RegEx.CONCAT)
            return ".";
        if (root == RegEx.ETOILE)
            return "*";
        if (root == RegEx.ALTERN)
            return "|";
        if (root == RegEx.DOT)
            return ".";
        return Character.toString((char) root);
    }
}
