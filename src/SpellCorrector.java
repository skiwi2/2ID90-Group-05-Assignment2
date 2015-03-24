import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Map;
import java.util.List;
import java.util.Set;

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
        
        List<String> currentSentence = new ArrayList<>();
        Map<List<String>, Double> phraseStrengths = new HashMap<>();
        recursiveCorrectPhrase(words, 0, currentSentence, 1d, 0, phraseStrengths);
        
        List<String> bestSentence = phraseStrengths.entrySet().stream()
            .sorted(Comparator.<Map.Entry<List<String>, Double>>comparingDouble(entry -> entry.getValue()).reversed())
            .findFirst().get().getKey();
        return String.join(" ", bestSentence);
    }
    
    private void recursiveCorrectPhrase(final String[] words, final int index, final List<String> currentSentence, final double currentStrength, final int currentErrors, final Map<List<String>, Double> phraseStrengths) {
        List<String> newSentence = new ArrayList<>(currentSentence);
        int newErrors = currentErrors;
        
        if (index == words.length) {
            //leaf node
            phraseStrengths.put(newSentence, currentStrength);
            return;
        }
        
        //if max amount of errors has occured, finish the word
        if (currentErrors == 2) {
            for (int i = index; i < words.length; i++) {
                newSentence.add(words[i]);
            }
            phraseStrengths.put(newSentence, currentStrength);
            return;
        }
        
        //check if last word was incorrect, if so, skip expanding recursion
        if (index > 0 && !Objects.equals(words[index - 1], newSentence.get(index - 1))) {
            newErrors++;
            newSentence.add(words[index]);
            recursiveCorrectPhrase(words, index + 1, newSentence, currentStrength, newErrors, phraseStrengths);
            return;
        }
        
        Set<String> candidateWords = getCandidateWords(words[index]);
        for (String candidateWord : candidateWords) {
            newSentence.add(candidateWord);
            double wordStrength = calculateWordStrength(candidateWord, index, words);
            recursiveCorrectPhrase(words, index + 1, newSentence, currentStrength * wordStrength, newErrors, phraseStrengths);
            newSentence.remove(newSentence.size() - 1);
        }
    }
    
    private double calculateWordStrength(final String candidateWord, final int index, final String[] words) {
        String ngram = ((index == 0) ? "" : words[index - 1]) + " " + candidateWord;
        return cr.getSmoothedCount(ngram);  //TODO multiply by calculateChannelModelProbability
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
