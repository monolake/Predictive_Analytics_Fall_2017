package KNN;
import crossvalidation.DataSet;
import java.io.*;
import java.util.*;

public class KNN {

	public static Double Euclidean(ArrayList<Double> doc1, ArrayList<Double> doc2) {
		
		double dist =  0.0;
		for (int i = 0; i < doc1.size(); i++) {
			dist += Math.pow(doc1.get(i).doubleValue() - doc2.get(i).doubleValue(), 2);
		}
		dist = Math.sqrt(dist);
		Double res = new Double(dist);
		return res;
		
	}
	
	public static Double Cosine(ArrayList<Double> doc1, ArrayList<Double> doc2) {
		
		double cross_prod = 0.0;
		for (int i = 0; i < doc1.size(); i++) {
			cross_prod += doc1.get(i) * doc2.get(i);
		}
		
		double doc1_norm = 0.0;
		double doc2_norm = 0.0;
		for (int i = 0; i < doc1.size(); i++) {
			doc1_norm += Math.pow(doc1.get(i), 2);
			doc2_norm += Math.pow(doc2.get(i), 2);
		}
		doc1_norm = Math.sqrt(doc1_norm);
		doc2_norm = Math.sqrt(doc2_norm);
		Double res = new Double(cross_prod / doc1_norm / doc2_norm);
		
		return res;
	}
	
	public static Map<Double, String> KMostSimilar(
			DataSet training, ArrayList<Double> doc, String measure, int K) {
		
		Map<Double, String> res = new TreeMap<Double, String>();
		ArrayList<ArrayList<Double>> docs = training.feature;
		if (measure.equals("Euclidean")) {
			Map<Double, String> map = new TreeMap<Double, String>();
			for (int i = 0; i < docs.size(); i++) {
				map.put(Euclidean(docs.get(i), doc), training.label.get(i));
			}
			Set set = map.entrySet();
			Iterator iterator = set.iterator();
			int i = 0;
			while (iterator.hasNext() && i < K) {
				
				Map.Entry me = (Map.Entry)iterator.next();
				res.put((Double) me.getKey(), (String)me.getValue());
				i++;
			}
		}
		else if (measure.equals("Cosine")) {
			Map<Double, String> map = new TreeMap<Double, String>(Collections.reverseOrder());
			for (int i = 0; i < docs.size(); i++) {
				map.put(Cosine(docs.get(i), doc), training.label.get(i));
			}
			
			Set set = map.entrySet();
			Iterator iterator = set.iterator();
			int i = 0;
			while (iterator.hasNext() && i < K) {
				
				Map.Entry me = (Map.Entry)iterator.next();
				res.put((Double) me.getKey(), (String)me.getValue());
				i++;
			}
		}
		return res;
	}
	
	public static ArrayList<String> KNN(int k, DataSet training, DataSet test, String similarity, String[] doc_label) {
		// for each point of test sample, generate k nearest neighbors, and return the predicted label
		// measure accuracy outside of this function
		ArrayList<String> predicted_label = new ArrayList<String>();
		for (int i = 0; i < test.size(); i++) {
			Map<Double, String> labels = KMostSimilar(training, test.feature.get(i), similarity, k);
			int[] freq = new int[15];
			for (Map.Entry<Double, String> entry : labels.entrySet()) {
				String value = entry.getValue();
				for (int l = 0; l < doc_label.length; l++) {
					if (value == doc_label[l])
						freq[l]++;
				}				
			}
			String final_label = new String();
			int max = Integer.MIN_VALUE;
			for (int l = 0; l < 15; l++) {
				if (freq[l] > max) {
					max = freq[l];
					final_label = doc_label[l];
				}
			}
			predicted_label.add(final_label);
		}
		return predicted_label;
	}
}
