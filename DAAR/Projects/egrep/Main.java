import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Main {
    // Méthode utilitaire pour lire un fichier
    private static String readFile(String filename) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        return content.toString();
    }
    public static void main(String[] args) {
        // Lecture de la regex
        String regEx="a|bc*";
        // if (args.length > 0) {
        //     // Lecture à partir d'un fichier
        //     try {
        //         regEx = readFile(args[0]);
        //     } catch (FileNotFoundException e) {
        //         System.err.println("Fichier non trouvé: " + args[0]);
        //         return;
        //     }
        // } else {
        //     // Entrée manuelle
        //     Scanner scanner = new Scanner(System.in);
        //     System.out.print("Entrez une expression régulière: ");
        //     regEx = scanner.next();
        //     scanner.close();
        // }

        // Affichage de la regex
        System.out.println("Expression régulière : " + regEx);

        // Construction de l'arbre syntaxique
        RegExTree syntaxTree;
        try {
            syntaxTree = RegEx.parse(regEx); // Parsing de l'arbre syntaxique
            System.out.println("Arbre syntaxique : " + syntaxTree);
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de l'expression régulière.");
            e.printStackTrace();
            return;
        }

        // Conversion en NDFA
        NDFA ndfa = new NDFA();
        ndfa.fromRegexTree(syntaxTree);
        System.out.println("NDFA généré :");
        ndfa.print();

        // Générer le fichier .dot pour le NDFA
        String ndfaDotFile = "ndfa.dot";
        ndfa.toDotFile(ndfaDotFile);
        System.out.println("Fichier .dot pour le NDFA créé : " + ndfaDotFile);

        // Conversion du NDFA en DFA
        DFA dfa = DFA.fromNDFA(ndfa);
        System.out.println("DFA généré :");
        dfa.print();

        // Générer le fichier .dot pour le DFA
        String dfaDotFile = "dfa.dot";
        dfa.toDotFile(dfaDotFile);
        System.out.println("Fichier .dot pour le DFA créé : " + dfaDotFile);

        // Exemple de lecture d'un fichier pour le parsing d'une chaîne (à adapter)
        // Ici on peut imaginer lire un fichier avec une chaîne à analyser par l'automate
        if (args.length > 1) {
            String inputString;
            try {
                inputString = readFile(args[1]);
                System.out.println("Fichier de chaîne lu : " + inputString);
                // Ici, vous pouvez ensuite simuler le parsing avec le DFA ou NDFA selon votre besoin.
            } catch (FileNotFoundException e) {
                System.err.println("Fichier non trouvé: " + args[1]);
            }
        }
    }
}
