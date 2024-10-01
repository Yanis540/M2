package test;

import org.junit.Test;


import java.io.FileWriter;
import java.io.IOException;


public class TestFile {
    public static String BASE_FILE_PATH="./DAAR/Projects/egrep";

    
    @Test
    public void testGenerationSpeed(){
        String[] patterns = {
            "abaa", "abcdefk", "kdflks", "xzyqwe", "lmnopqr", "hijklmn", "ooppqqrr", "ppppqq", "uuuvvww", "xyzzzz",
            "aabbcc", "ddddeee", "effgghh", "qwertyu", "asdfghj", "zxcvbnm", "plmokni", "ujmnhyt", "rewqasd", "tyghbnm",
            "pplokij", "oiujklm", "nnbbvcc", "yuioplk", "hfdsoie", "woeiruf", "sdfghjk", "xcnvmdf", "ooplkji", "wertyuj",
            "bvcxzpl", "qazwsx", "edcrfv", "tgbvfr", "wsxcde", "gtyhnm", "poiuytr", "mnbvcxz", "lkjhgfd", "asdfqwe",
            "qazplm", "wsmjkd", "cbvlmki", "lpdsaqw", "ploikmn", "sdfrtre", "zxcvtyu", "bnmpokl", "cfgtymn", "wertyuio",
            "qasdfg", "lkjhgfds", "qwopplm", "plokmn", "trfjkil", "ghnbvc", "tryuiko", "awertyu", "ytgnhjm", "qwertyuio",
            "fghjklm", "poiulkm", "mnplkji", "rtyuiop", "fhgtyui", "olkjhgf", "xcvbnml", "uhyjnbv", "aaabbbccc", "ttyyzz",
            "ppqqrrss", "jjhghgg", "plkllp", "zxcvfgty", "mnbvcxza", "pkloijnm", "fjdksl", "zssxdd", "rtrfgyy", "fgtyhjk",
            "ewqasd", "jjjkkkll", "abcdmnop", "zzxxccvv", "bmnplokm", "llooookm", "iuytreww", "bvcfgtyu", "mmhhjjnm", "fghjkl",
            "nhygtfrd", "ytrewsdd", "gfdsasd", "klokijk", "oppijklm", "poiuhjkl", "gbnmnklp", "tyuiolkj", "qwetyyuui", "jkklnm",
            "azsxdcfv", "hgfvcdf", "mkoijuhb", "wsxedcrf", "dertgfvn"
        };
        try {
            // Initialisation du fichier CSV
            FileWriter csvWriter = new FileWriter(BASE_FILE_PATH+"/results/speed.csv");
            csvWriter.append("Method,AVG_KMP,AVG_DFA,MIN_KMP,MIN_DFA,MAX_KMP,MAX_DFA\n");

            // Mesure des performances pour KMP et DFA
            for (String pattern : patterns) {
                // Mesurer les performances du KMP
                long[] kmpTimes = StatMeasures.measureKMPConstructionTime(pattern);
                long kmpAvg = StatMeasures.average(kmpTimes);
                long kmpMin = StatMeasures.min(kmpTimes);
                long kmpMax = StatMeasures.max(kmpTimes);

                // Mesurer les performances du DFA
                long[] dfaTimes = StatMeasures.measureDFAConstructionTime(pattern);
                long dfaAvg = StatMeasures.average(dfaTimes);
                long dfaMin = StatMeasures.min(dfaTimes);
                long dfaMax = StatMeasures.max(dfaTimes);

                csvWriter.append(
                    pattern+
                    "," + kmpAvg+","+dfaAvg + 
                    "," + kmpMin+","+dfaMin + 
                    "," + kmpMax+","+dfaMax + "\n"
                );
            }

            // Fermeture du fichier
            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }

    @Test 
    public void testAccuracyAndTime(){
        // Liste des motifs simples (100 motifs)
        String[] patterns = {
            "mamamia", "helloworld", "abcdefg", "123abc", "javaisfun", "regex101", "pattern", "testcase", "debugging", "compilation",
            "trycatch", "exception", "overload", "function", "variable", "loop", "arraylist", "hashtable", "binarytree", "recursion",
            "sorting", "searching", "algorithm", "datastructure", "bitwise", "andoperator", "xoroperator", "shiftoperator", "modulus",
            "threading", "concurrency", "synchronization", "asynchronous", "runnable", "callable", "futuretask", "singleton", "factory",
            "observer", "decorator", "adapter", "bridge", "facade", "proxy", "builder", "prototype", "mediator", "strategy", "visitor",
            "composite", "iterator", "command", "interpreter", "memento", "state", "chainofresp", "templatemethod", "abstractfactory",
            "flyweight", "nullobject", "mvcpattern", "mvppattern", "mvvmpattern", "reactive", "observable", "publisher", "subscriber",
            "observerpattern", "mockito", "junit", "assertion", "integration", "unitesting", "regression", "loadtesting", "securitytest",
            "performance", "stress", "scalability", "concurrencytest", "paralleltest", "functionaltest", "blackboxtest", "whiteboxtest",
            "greyboxtest", "usabilitytest", "accessibility", "localization", "globalization", "benchmark", "profiling", "bottleneck",
            "heapdump", "threaddump", "gcroots", "leakdetection", "lockcontention", "deadlock", "threadpool", "forkjoin", 
        };
    
        // Liste des phrases de test (100 phrases de lineInput)
        String[] lineInput = {
            "mamamiaaaa yola", "hello to the world!", "abcdefg patterns are used", "sequence 123abc detected", "java is fun indeed", 
            "regex101 helps with patterns", "testing a simple pattern", "this is a testcase", "debugging is essential for code", 
            "compilation successful no errors", "handle the trycatch block properly", "this line throws an exception", 
            "function overloading is useful", "defining a function in Java", "variable names should be meaningful", "loops are fundamental",
            "ArrayList is a resizable array in Java", "hashtable stores key-value pairs", "binarytree traversal is important", 
            "recursion is a powerful technique", "sorting algorithms optimize searches", "searching efficiently in data", 
            "an algorithm solves a problem", "data structures organize information", "bitwise operations are fast", 
            "use the andoperator carefully", "xoroperator flips bits", "shiftoperator shifts bits to the left or right", 
            "modulus operation gives remainder", "multithreading improves performance", "concurrency issues need attention", 
            "synchronization avoids race conditions", "asynchronous operations increase efficiency", "runnable represents a task", 
            "callable returns a result", "futuretask represents asynchronous computation", "singleton ensures only one instance", 
            "factory design pattern simplifies creation", "observer design pattern helps in events", "decorator adds functionality", 
            "adapter pattern adapts interfaces", "bridge pattern separates abstraction from implementation", "facade simplifies complex systems", 
            "proxy controls access to objects", "builder simplifies object creation", "prototype pattern clones objects", 
            "mediator pattern coordinates communication", "strategy pattern defines algorithms", "visitor pattern separates logic", 
            "composite pattern composes objects", "iterator pattern traverses collections", "command pattern encapsulates requests", 
            "interpreter pattern defines grammar", "memento pattern saves object state", "state pattern represents states", 
            "chain of responsibility passes requests", "template method defines steps", "abstract factory creates families of objects", 
            "flyweight pattern minimizes memory usage", "null object avoids null checks", "MVC pattern separates concerns", 
            "MVP pattern separates logic", "MVVM pattern binds UI and logic", "reactive programming handles streams", 
            "observable is used in reactive programming", "publisher and subscriber communicate", "mockito for unit tests", 
            "JUnit framework runs tests", "assertion verifies conditions", "integration tests check system interactions", 
            "unit testing is essential", "regression tests catch bugs", "load testing ensures system performance", 
            "security testing finds vulnerabilities", "performance testing measures speed", "stress testing pushes system limits", 
            "scalability testing for growth", "concurrency tests check for issues", "parallel tests run simultaneously", 
            "functional testing ensures requirements", "blackbox testing ignores internals", "whitebox testing checks internal logic", 
            "greybox testing combines both", "usability testing for user experience", "accessibility testing for all users", 
            "localization adapts to languages", "globalization prepares for worldwide use", "benchmark testing measures performance", 
            "profiling finds performance issues", "bottleneck detected in the system", "heap dump shows memory usage", 
            "thread dump shows running threads", "GC roots point to live objects", "leak detection for memory issues", 
            "lock contention slows down processes", "deadlock detected between threads", "thread pool manages threads", 
            "fork join splits tasks", "parallel stream processes data in parallel", "mutex controls access to resources", 
        };
    
        // Tableaux des résultats attendus manuellement définis (100 booléens)
        boolean[] founds =  new boolean[lineInput.length];
        for(int i=0;i<founds.length;i++){
            founds[i] = lineInput[i].contains(patterns[i]);
        }
        try {
            // Initialisation du fichier CSV
            FileWriter csvWriter = new FileWriter(BASE_FILE_PATH+"/results/accuracy_and_time.csv");
            csvWriter.append("Pattern,lineInput,appears_expected,found_kmp,found_dfa,time_kmp,time_dfa\n");

            // Parcours de chaque motif
            for (int i = 0; i < patterns.length; i++) {
                String pattern = patterns[i];
                // Pour chaque phrase dans lineInput
                String line = lineInput[i];
                    // Résultat attendu (founds[j])
                boolean appearsExpected = founds[i];
                // Résultats de la méthode KMP
                StatMeasures.Result resKmp = StatMeasures.measureKMPTotalSearchTime(pattern, line);
                StatMeasures.Result resDFA = StatMeasures.measureDFATotalSearchTime(pattern, line);
                boolean foundKMP = resKmp.found,foundDFA = resDFA.found;
                long kmpTime = resKmp.avg_time,dfaTime = resDFA.avg_time;
                // Sauvegarder les résultats dans le fichier CSV
                csvWriter.append(pattern + "," + line + "," + appearsExpected + "," + foundKMP + "," + foundDFA +","+ kmpTime+"," + dfaTime+ "\n");
                    
            }
             // Fermeture du fichier CSV
            csvWriter.flush();
            csvWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
