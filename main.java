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
	public static List<String> lemmatize(String input, List<String> ner_tag) {
		
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
	         ner_tag.add(token.get(NamedEntityTagAnnotation.class));
	       }
	     }		
	     return lemmas;
	}
	
	public static LinkedHashMap<String, Integer> selfgetTF(List<String> lemma) {
		
		// token - number of times it appears
		LinkedHashMap<String, Integer> token_mp = new LinkedHashMap<String, Integer>();
		
		List<String> ngrams_level2 = new LinkedList<String> (StringUtils.getNgrams(lemma, 2, 2));
		for (String temp : ngrams_level2) {
			token_mp.put(temp,  Collections.frequency(ngrams_level2, temp));
		}

		List<String> ngrams_level3 = new LinkedList<String> (StringUtils.getNgrams(lemma, 3, 3));
		for (String temp : ngrams_level3) {
			token_mp.put(temp,  Collections.frequency(ngrams_level3, temp));
		}
		
/*		for (String key : token_mp.keySet()) {
			if (token_mp.get(key) > 3)
				System.out.println(key);
		}*/
		LinkedHashMap<String, Integer> token_mp_final = new LinkedHashMap<String, Integer>();
		
		List<String> ngrams_level1 = new LinkedList<String> (StringUtils.getNgrams(lemma, 1, 1));
		for (String temp : ngrams_level1) {
			int appearance = 0;
			for (String key : token_mp.keySet()) {
				if (token_mp.get(key) > 3 && key.contains(temp)) {
					appearance += token_mp.get(key);
				}
			}
			token_mp_final.put(temp, Collections.frequency(ngrams_level1, temp) - appearance);
		}		
		
		for (String key : token_mp.keySet()) {
			if (token_mp.get(key) > 3) {
				token_mp_final.put(key, token_mp.get(key));
				//System.out.println(key);
			}
		}
		
		double count = 0;
		for (String key : token_mp_final.keySet()) {
			count += token_mp_final.get(key);
		}
		
		for (String key : token_mp_final.keySet()) {
			System.out.print(key + " : " + token_mp_final.get(key) / count + "\n");
		}
		
		System.out.println();
		return token_mp_final;
			
	}
	
	public static int computeTF(String token, String file) {
		//String input = readFile(files.get(i));
		return 1;
	}
	public static void main(String []args) {
		System.out.println("Hello World");
		final String dir = System.getProperty("user.dir");
		//System.out.println("current dir = " + dir);
		LinkedList<String> queue = new LinkedList<String> ();
		
		File f = new File(dir + "/" + args[0]);
		System.out.println(f.listFiles().length);
		
		//System.out.println(file);
		
		try {
			// 1. generate terms of all documents
			List<String> tokens = new LinkedList<String>();
			List<String> files = new LinkedList<String>();
			LinkedList<LinkedHashMap<String, Integer>> TF_list = new LinkedList<LinkedHashMap<String, Integer>>();
			for (File path : f.listFiles()) {
				System.out.println(dir + "/" + args[0] + "/" + path.getName());
				String file = new String(path.getName());
				files.add(file);
			}
			for (int i = 0; i < f.listFiles().length; i++) {
				String input = readFile(dir + "/" + args[0] + "/" + files.get(i));
				System.out.println(input);
				input = input.toLowerCase();
				Pattern pt = Pattern.compile("[^a-zA-Z0-9 ]");
				Matcher match = pt.matcher(input);
				while (match.find()) {
					String s = match.group();
					input = input.replaceAll("\\" + s, "");
				}
				//System.out.println(input);
				String output = removeStopWords(input);
				//System.out.println(output); // remove stopwords
				List<String> ner_tag = new LinkedList<String> ();
				List<String> lemma = lemmatize(output, ner_tag);
				System.out.println(lemma); // lemmatizer 
				System.out.println(ner_tag);
				
				List<String> after_ner = new LinkedList<String>();
				
				int ii = 0;
				while (ii < lemma.size()) {
					if (ner_tag.get(ii).compareTo("O") == 0) {
						after_ner.add(lemma.get(ii));
						ii++;
					}
					else {
						String temp = new String();
						temp = lemma.get(ii);
						int j = ii+1;
						while (j < lemma.size() && ner_tag.get(j).compareTo(ner_tag.get(ii)) == 0) {
							temp += (" " + lemma.get(j));
							j += 1;
						}
						after_ner.add(temp);
						ii = j;
					}					
				}
				System.out.println(after_ner);
				LinkedHashMap<String, Integer> TF = selfgetTF(after_ner);
				TF_list.add(TF);
				for (String key : TF.keySet()) {
					if (tokens.contains(key))
						continue;
					tokens.add(key);
				}
			}
			
			// 2. count token appearance in documents
			LinkedList<Integer> num_appeared = new LinkedList<Integer>();
			
			int m = tokens.size();
			int n = f.listFiles().length;
			for (int i = 0; i < m; i++) {
				num_appeared.add(0);
			}
			for (int i = 0; i < m; i++) {
				int count = 0;
				for (int j = 0; j < n; j++) {
					LinkedHashMap<String, Integer> doc = TF_list.get(j);
					if (doc.containsKey(tokens.get(i))) {
						count++;
					}
				}
				num_appeared.set(i, count);
			}

			// 2. generate TFIDF matrix
			double [][] TFIDF = new double [m][n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					TFIDF[i][j] = 0;
					LinkedHashMap<String, Integer> doc = TF_list.get(j);
					if (doc.containsKey(tokens.get(i))) {
						TFIDF[i][j] = (double) doc.get(tokens.get(i)) * Math.log((double) n / (double) num_appeared.get(i));
					}
				}
			}
			
			FileWriter fos = new FileWriter(args[1]);
			PrintWriter dos = new PrintWriter(fos);
			String header = new String();
			header += "\t";
			for (int i = 0; i < n; i++) {
				header += files.get(i) + "\t";
			}
			dos.println(header);
			for (int i = 0; i < m; i++) {
				//System.out.print(tokens.get(i) + "\t");
				dos.print(tokens.get(i) + ",");
				for (int j = 0; j < n; j++) {
					//System.out.print(TFIDF[i][j] + "\t");
					dos.print(TFIDF[i][j] + "\t");
				}
				//System.out.println();
				dos.println();
			}
			System.out.println("finish all work");
			dos.close();
			fos.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
}

