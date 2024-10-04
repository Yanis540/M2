package KMP;
import java.util.Arrays;

public class KMP {

    private final String pattern;
    private final int[] carryOver;
    
    public KMP(String pattern) {
        this.pattern = pattern;
        this.carryOver = this.computeCarryOver();
    }
    public int[] getCarryOver(){
        return this.carryOver;
    }
    public String toString(){
        return Arrays.toString(carryOver);
    }
    public String getPattern(){
        return this.pattern;
    }
    private int[] computeCarryOver() {
        int[] lps = initializeLPS();
        this.optimizeLPS(lps);
        return lps;
    }
    private int lengthLargestPrefixSuffix(String str) 
    { 
        // https://www.geeksforgeeks.org/longest-prefix-also-suffix/
        int[] A = new int[str.length()]; 
        int j = 0, i = 1; 
        while(i < str.length()) 
        { 
            if(str.charAt(i) == str.charAt(j)) 
            { 
                A[i] = j+1; 
                j++; 
                i++; 
            } 
            else
            { 
                if(j==0) 
                    i++; 
                else
                    j = A[j-1]; 
                  
            } 
        } 
  
        return A[str.length()-1]; 
    } 
  
    private int[] initializeLPS() {
        int[] lps = new int[pattern.length() + 1];
        lps[0] = -1;
        for (int i = 0; i < pattern.length(); i++) {
            String subset = pattern.chars()
                .limit(i+1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
            ;
            lps[i + 1] = lengthLargestPrefixSuffix(subset) ;
        }

        return lps;
    }

    private void optimizeLPS(int[] lps) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < pattern.length(); i++) {
                if (lps[i] != -1 && pattern.charAt(i) == pattern.charAt(lps[i])) {
                    if (lps[lps[i]] != -1) {
                        lps[i] = lps[lps[i]];
                    } else {
                        lps[i] = -1;
                    }
                    changed = true;
                }
            }
        } while (changed);
    }
    public boolean find(String text) {
        
        int i = 0, j = 0;

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    return true;
                }
            } else if (j != 0) {
                j = carryOver[j];
                if (j == -1) {
                    j = 0;
                    i++;
                }
            } else {
                i++;
            }

        }
        return false;
    }
}