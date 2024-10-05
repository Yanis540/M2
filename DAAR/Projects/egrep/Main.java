

import java.util.Arrays;

import Examples.KMPExample;
import Examples.RegexExample;
import Regex.RegExTree;
import test.TestFile;

public class Main {
  
    public static void main(String[] args) {
        // RegexExample.exampleDFA(args);
        RegexExample.starterDFAExample();
        // KMPExample.starterKMP();
        // KMPExample.exampleKMP(args);
        try{
            runParsing(args);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void runParsing(String[]args) throws Exception{
        System.out.println(args.length);
        if(args.length!=2){
            throw new Exception("Expecting pattern : <Pattern> <filename_path>");
        }
        String pattern = args[0];
        RegExTree rgt = Regex.RegEx.parse(pattern);
        if(rgt.isTreeConcat()){
            KMPExample.findPatternInFileWithKMP(args);
        }else {
            RegexExample.findRegexInFileWithDFA(args);
        }
        
    } 
    public static boolean isSimpleConcatenation(String regex) {

        regex = regex.trim().replaceAll("^[^\\w]+|[^\\w]+$", "");
        String[] parts = regex.split("[^\\w]+");

        return parts.length == 1 && parts[0].equals(regex);
    }
    
}
