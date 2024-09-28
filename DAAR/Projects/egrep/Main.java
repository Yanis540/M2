import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;

public class Main {
  
    public static void main(String[] args) {
        String regEx, fileName;
        System.out.println(args.length);
        regEx = (args.length==2 && args[0]==null)?args[0]:"S((a|g|r)*)on";
        fileName = (args.length==2 && args[1]==null)?args[1]:"./DAAR/Projects/egrep/2.txt";
        System.out.println("  >> regEx : " + regEx);
        System.out.println("  >> file name : " + fileName);
        long startTime = System.currentTimeMillis();
        if (regEx.length() < 1) {
            System.err.println("  >> ERROR: empty regEx.");
            return;
        } 
        int resultNumber = findFromFile(regEx,fileName); 
        long searchEndTime = System.currentTimeMillis();
        System.out.println(resultNumber + " lines matched found");
        System.out.println("Temps Total : " + (searchEndTime - startTime) + "ms");
    }
    public static int findFromFile(String regEx,String fileName){
        Automate automate = new Automate(regEx); 
        int lineNumber = 1;
        int resultNumber = 0;
        String text;
        try {
            text = readFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        String[] lines = text.split("\\n");
        
        for (String line: lines) {
            if (automate.find(line)) {
                System.out.println(lineNumber+" - " +line);
                resultNumber++;
            }
            lineNumber++;
        }
        return resultNumber; 
    }
      // Méthode utilitaire pour lire un fichier
      private static String readFile(String filename) throws FileNotFoundException  {
        File file = new File(filename);
        long filelength = file.length();
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(filecontent);
    }
    public static void starterExample(){
        String regEx="S((a|g|r)*)on";
        constructExampleDFA(regEx);
    }

    public static void constructExampleDFA(String regEx){
        // Affichage de la regex
        System.out.println("Expression régulière : " + regEx);
        // Conversion en NDFA
        Automate automate = new Automate(regEx);
        NDFA ndfa = automate.getNDfa();
        // ndfa.print();
        // Générer le fichier .dot pour le NDFA
        String ndfaDotFile = "ndfa.dot";
        ndfa.toDotFile(ndfaDotFile);
        System.out.println("Fichier .dot pour le NDFA créé : " + ndfaDotFile);
        DFA dfa = automate.getDfa();
        System.out.println("DFA généré :");
        // dfa.print();
        String dfaDotFile = "dfa.dot";
        dfa.toDotFile(dfaDotFile);
        System.out.println("Fichier .dot pour le DFA créé : " + dfaDotFile);
        
        String lookupString = "darbb";
        System.out.println("String is found  :  "+automate.find(lookupString));
    } 
}
