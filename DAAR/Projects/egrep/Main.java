

import java.util.Arrays;

import Examples.RegexExample;

public class Main {
  
    public static void main(String[] args) {
        // RegexExample.exampleDFA(args);
        exampleKMP();
    }
    public static void exampleKMP(){
        String pattern = "abcabcd";
        KMP kmp = new KMP(pattern); 
        int[] carryOver = kmp.getCarryOver();
        
        System.out.println("Carry over for pattern ("+ pattern + ")  :"+Arrays.toString(carryOver));
        String example = "jshdifjsd idsfoidsj fiosdfs abcabcdef";
        System.out.println("Found " + kmp.find(example)+" match." );
    }
    public static boolean isSimpleConcatenation(String regex) {

        regex = regex.trim().replaceAll("^[^\\w]+|[^\\w]+$", "");
        String[] parts = regex.split("[^\\w]+");

        return parts.length == 1 && parts[0].equals(regex);
    }
    
}
