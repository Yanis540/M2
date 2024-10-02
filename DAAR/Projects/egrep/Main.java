

import java.util.Arrays;

import Examples.KMPExample;
import Examples.RegexExample;

public class Main {
  
    public static void main(String[] args) {
        // RegexExample.exampleDFA(args);
        // RegexExample.starterDFAExample();
        // KMPExample.starterKMP();
        KMPExample.exampleKMP(args);
    }
   
    public static boolean isSimpleConcatenation(String regex) {

        regex = regex.trim().replaceAll("^[^\\w]+|[^\\w]+$", "");
        String[] parts = regex.split("[^\\w]+");

        return parts.length == 1 && parts[0].equals(regex);
    }
    
}
