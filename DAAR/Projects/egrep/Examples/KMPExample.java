package Examples;

import java.util.Arrays;

import KMP.KMP;
import Reader.Reader;
import test.TestFile;


public class KMPExample {
    public static void findPatternInFileWithKMP(String[]args){
        String pattern, fileName;
        System.out.println(args.length);
        pattern = (args.length==2 && args[0]==null)?args[0]:"Sargon";
        fileName = (args.length==2 && args[1]==null)?args[1]:TestFile.BASE_FILE_PATH+"/file.txt";
        System.out.println("  >> pattern : " + pattern);
        System.out.println("  >> file name : " + fileName);
        long startTime = System.currentTimeMillis();
        if (pattern.length() < 1) {
            System.err.println("  >> ERROR: empty pattern.");
            return;
        } 
        int resultNumber = findTotalNumberPatternFromFile(pattern,fileName); 
        long searchEndTime = System.currentTimeMillis();
        System.out.println(resultNumber + " lines matched found");
        System.out.println("Temps Total : " + (searchEndTime - startTime) + "ms");
    }
    public static int findTotalNumberPatternFromFile(String pattern,String fileName){
        KMP kmp = new KMP(pattern); 
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
            if (kmp.find(line)) {
                System.out.println(lineNumber+" - " +line);
                resultNumber++;
            }
            lineNumber++;
        }
        return resultNumber; 
    }
      // Méthode utilitaire pour lire un fichier
    
    public static void starterKMP(){
        String pattern = "abcabcd";
        KMP kmp = new KMP(pattern); 
        int[] carryOver = kmp.getCarryOver();
        
        System.out.println("Carry over for pattern ("+ pattern + ")  :"+Arrays.toString(carryOver));
        String example = "jshdifjsd idsfoidsj fiosdfs abcabcdef";
        System.out.println("Found " + kmp.find(example)+" match." );
    }
}
