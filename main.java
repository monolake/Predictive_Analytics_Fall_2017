package PAHW1;
import java.io.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.*;
import java.util.regex.*;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.util.StringUtils;

public class main {
	public static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
	public static Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
	
	public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
		InputStream is = new FileInputStream(filePath);
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine();
		while (line != null) {
			buffer.append(line);
			buffer.append(" ");
			line = reader.readLine();
		}
		reader.close();
		is.close();
	}
	
	public static String readFile(String filePath) throws IOException {
		StringBuffer sb = new StringBuffer();
		readToBuffer(sb, filePath);
		return sb.toString();
	}
	
	public static boolean isStopword(String word) {
		if (stopWordSet.contains(word))
			return true;
		return false;
	}
	public static String removeStopWords(String input) {
		String result = "";
		String[] words = input.split("\\s+");
		for (String word: words) {
			if (word.isEmpty())
				continue;
			if (isStopword(word))
				continue;
			result += (word + " ");
		}
		return result;
	}
	public static List<String> lemmatize(String input, LinkedHashMap<String, String> ner_tag) {
		
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(input);

        // run all Annotators on this text
        pipeline.annotate(document);

        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<String> lemmas = new LinkedList<String>();
        List<CoreMap> sentences = document.get(SentencesAnnotation.class); 
	     for(CoreMap sentence: sentences) {
	       // traversing the words in the current sentence
	       // a CoreLabel is a CoreMap with additional token-specific methods
	       for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	         lemmas.add(token.get(LemmaAnnotation.class));
	         ner_tag.put(token.get(TextAnnotation.class), token.get(NamedEntityTagAnnotation.class));
	       }
	     }		
	     return lemmas;
	}
	
	
	public static void main(String []args) {
		System.out.println("Hello World");
		final String dir = System.getProperty("user.dir");
		//System.out.println("current dir = " + dir);
		String file = dir + "/C1/article01.txt";
		//System.out.println(file);
		
		try {
			String input = readFile(file);
			System.out.println(input);
			input = input.toLowerCase();
			Pattern pt = Pattern.compile("[^a-zA-Z0-9 ]");
			Matcher match = pt.matcher(input);
			while (match.find()) {
				String s = match.group();
				input = input.replaceAll("\\" + s, "");
			}
			System.out.println(input);
			String output = removeStopWords(input);
			System.out.println(output); // remove stopwords
			

			
			
			LinkedHashMap<String, String> ner_tag = new LinkedHashMap<String, String> ();
			List<String> lemma = lemmatize(output, ner_tag);
			System.out.println(lemma); // lemmatizer 
			System.out.println(ner_tag);
			
			// create a list of tokens with uniqueness
			LinkedHashMap<String, Integer> token_mp = new LinkedHashMap<String, Integer>();
			int count = 0;
			for (int i = 0; i < lemma.size(); i++) {
				if (token_mp.containsKey(lemma.get(i)))
					continue;
				token_mp.put(lemma.get(i), count++);
			}
			
			int [][] sim_mat = new int[count][count];
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					sim_mat[i][j] = 0;
				}
			}
			List<String> ngrams = new LinkedList<String> (StringUtils.getNgrams(lemma, 2, 2));
			System.out.println(ngrams);
			System.out.println("The dimension is : " + count);
			for (String temp : ngrams) {
				String[] words = temp.split("\\s+");
				int idx = token_mp.get(words[0]);
				int idy = token_mp.get(words[1]);
				sim_mat[idx][idy]++;
				// System.out.print(words[0] + " " + words[1] + " " + idx + " " + idy);
				//return;
				
			}
			
			for (String str : token_mp.keySet()) {
				System.out.print(str + token_mp.get(str) + " ");
			}
			System.out.println();
			
			int max = Integer.MIN_VALUE;
			
			double sums = 0;
			List<String> res = new LinkedList<String>();
			
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					if (sim_mat[i][j] > 3) {
						String temp = new String();
						for (String o : token_mp.keySet()) {
							if (token_mp.get(o).equals(i)) {
								temp = temp + o;
								temp = temp + " ";
							}
						}
						for (String o : token_mp.keySet()) {
							if (token_mp.get(o).equals(j)) {
								temp = temp + o;
							}
						}		
						res.add(temp);
					}
					// System.out.print(sim_mat[i][j] + " ");
				}
				// System.out.println();
			}
			
			// System.out.println("max " + max);
			
			List<String> token = new LinkedList<String>();
			int i = 0;
			while (i < lemma.size() - 1) {
				String target = lemma.get(i) + " " + lemma.get(i+1);
				if (res.contains(target)) {
					if (!token.contains(target))
						token.add(target);
					i += 2;
				}
				else {
					if (!token.contains(lemma.get(i)))
						token.add(lemma.get(i));
					i += 1;
				}
			}
			
			System.out.println(token);
			
			System.out.println("finish all work");
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
}

