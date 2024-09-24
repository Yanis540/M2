public class Automate {
    private DFA dfa;
    private NDFA ndfa;
    public Automate(String regex){
        this.construct(regex);
    }
    public DFA getDfa(){
        return this.dfa; 
    }
    public NDFA getNDfa(){
        return this.ndfa; 
    }
    private void construct(String regEx){
         // Construction de l'arbre syntaxique
        RegExTree syntaxTree;
        try {
            syntaxTree = RegEx.parse(regEx); // Parsing de l'arbre syntaxique
            System.out.println("Arbre syntaxique : " + syntaxTree);
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de l'expression régulière.");
            e.printStackTrace();
            return ; 
        }
        // Conversion en NDFA
        this.ndfa= new NDFA();; 
        this.ndfa.fromRegexTree(syntaxTree);
        this.dfa =  new DFA(ndfa); 
    }
    public boolean find(String line){
        boolean res = dfa.find(line);
        if(res)
            return res; 
        if(line.length() == 0)
            return false; 
        return find(line.substring(1));
    }
}
