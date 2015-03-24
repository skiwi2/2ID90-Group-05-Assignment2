import java.util.HashSet;

public class SpellCorrector {
    final private CorpusReader cr;
    final private ConfusionMatrixReader cmr;
    
    final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz'".toCharArray();
    
    
    public SpellCorrector(CorpusReader cr, ConfusionMatrixReader cmr) 
    {
        this.cr = cr;
        this.cmr = cmr;
    }
    
    public String correctPhrase(String phrase)
    {
        if(phrase == null || phrase.length() == 0)
        {
            throw new IllegalArgumentException("phrase must be non-empty.");
        }
            
        String[] words = phrase.split(" ");
        String finalSuggestion = "";
        
        /** CODE TO BE ADDED **/
        
        
        return finalSuggestion.trim();
    }
    
    public double calculateChannelModelProbability(String suggested, String incorrect) 
    {
         /** CODE TO BE ADDED **/
        
        return 0.0;
    }
    private String deleteCharAt(String strValue, int index) {
        return strValue.substring(0, index) + strValue.substring(index + 1);
 
    }
    private String insertCharAt(String strValue, int index, char c){
        return strValue.substring(0,index) + c + strValue.substring(index);
    }
    private String subCharAt(String strValue, int index, char c){
        return strValue.substring(0,index) + c + strValue.substring(index+1);
    }
      
    public HashSet<String> getCandidateWords(String word)
    {
        HashSet<String> ListOfWords = new HashSet<String>();
        String w;
        for (int i = 0; i < word.length()+1; i++) {
            w = deleteCharAt(word+" ", i);
           //System.out.println(w);
            ListOfWords.add(w.trim());
            for (int j = 0; j < 26; j++) {
                 w = insertCharAt(word, i, ALPHABET[j]);
                //System.out.println(w);
                ListOfWords.add(w.trim());
                w = subCharAt(word+" ", i, ALPHABET[j]);
                //System.out.println(w);
                ListOfWords.add(w.trim());
            }
            
            
        }
        
        return cr.inVocabulary(ListOfWords);
    }          
}