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
            csvWriter.append("Method, AVG_KMP, AVG_DFA, MIN_KMP, MIN_DFA, MAX_KMP, MAX_DFA\n");

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
}
