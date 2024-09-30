package Examples;



import Reader.Reader;
import Regex.Automate;
import Regex.DFA;
import Regex.NDFA;
import test.TestFile;

public class RegexExample {
    public static void exampleDFA(String[]args){
        String regEx, fileName;
        System.out.println(args.length);
        regEx = (args.length==2 && args[0]==null)?args[0]:"S((a|g|r)*)on";
        fileName = (args.length==2 && args[1]==null)?args[1]:"./DAAR/Projects/egrep/file.txt";
        System.out.println("  >> regEx : " + regEx);
        System.out.println("  >> file name : " + fileName);
        long startTime = System.currentTimeMillis();
        if (regEx.length() < 1) {
            System.err.println("  >> ERROR: empty regEx.");
            return;
        } 
        int resultNumber = findTotalNumberRegexFromFile(regEx,fileName); 
        long searchEndTime = System.currentTimeMillis();
        System.out.println(resultNumber + " lines matched found");
        System.out.println("Temps Total : " + (searchEndTime - startTime) + "ms");
    }
    public static int findTotalNumberRegexFromFile(String regEx,String fileName){
        Automate automate = new Automate(regEx); 
        int lineNumber = 1;
        int resultNumber = 0;
        String text;
        try {
            text = Reader.readFile(fileName);
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
    
    public static void starterDFAExample(){
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
        String ndfaDotFile = TestFile.BASE_FILE_PATH+"/ndfa.dot";
        ndfa.toDotFile(ndfaDotFile);
        System.out.println("Fichier .dot pour le NDFA créé : " + ndfaDotFile);
        DFA dfa = automate.getDfa();
        System.out.println("DFA généré :");
        // dfa.print();
        String dfaDotFile = TestFile.BASE_FILE_PATH+"dfa.dot";
        dfa.toDotFile(dfaDotFile);
        System.out.println("Fichier .dot pour le DFA créé : " + dfaDotFile);
        
        String lookupString = "darbb";
        System.out.println("String is found  :  "+automate.find(lookupString));
    } 
}
