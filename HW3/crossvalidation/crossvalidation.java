package crossvalidation;
import java.io.*;
import java.util.*;
import tfidfReader.*;

public class crossvalidation {
	
	public static TrainingTestData cv10(ArrayList<ArrayList<Double>> sample, int index, Map<Integer,FolderRange> mp) {
		// index is from 0 to 9
		ArrayList<ArrayList<Double>> training = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> test = new ArrayList<ArrayList<Double>>();
		
		// get split by 9-1
		Map<Integer, FolderDiscrete> training_map = new HashMap<Integer, FolderDiscrete>();
		Map<Integer, FolderDiscrete> test_map = new HashMap<Integer, FolderDiscrete>();		
		for (int i = 0; i < mp.size(); i++) {
			FolderRange fr = mp.get(i+1);
			int length = fr.end - fr.start + 1;
			int start_test = (int) (fr.start + length * 0.1 * index);
			int end_test = (int) (fr.start + length * 0.1 * (index+1));
			String label = fr.label;
			ArrayList<Integer> training_list = new ArrayList<Integer>();
			for (int j = fr.start; j < start_test; j++) 
				training_list.add(j);
			for (int j = end_test + 1; j <= fr.end; j++)
				training_list.add(j);
			ArrayList<Integer> test_list = new ArrayList<Integer>();
			for (int j = start_test; j <= end_test; j++)
				test_list.add(j);
			training_map.put(i+1, new FolderDiscrete(training_list, label));
			test_map.put(i+1, new FolderDiscrete(test_list, label));
			// System.out.println(fr.start + " " + (fr.start + train_len - 1) + " " + (fr.start + train_len) + " " + fr.end);
		}
		
		ArrayList<String> training_label = new ArrayList<String>();
		ArrayList<String> test_label = new ArrayList<String>();

		//???????????????????do some work
		// each sample starts from 0
		// folder starts from 1 to 15
		for (int i = 0; i < sample.size(); i++) {
			for (int j = 0; j < mp.size(); j++) {
				// i + 1 because index starts from 1 in folder_range and folder_discrete
				if (training_map.get(j+1).folder_list.contains(i+1)) {
					training.add(sample.get(i));
					training_label.add(training_map.get(j+1).label);
				}
				else if (test_map.get(j+1).folder_list.contains(i+1)) {
					test.add(sample.get(i));
					test_label.add(test_map.get(j+1).label);
				}
			}
		}		
		DataSet training_data = new DataSet(training, training_label);
		DataSet test_data = new DataSet(test, test_label);
		return new TrainingTestData(training_data, test_data);
		
	}
	
	public static TrainingTestData cv(ArrayList<ArrayList<Double>> sample, double ratio, Map<Integer,FolderRange> mp) {
		ArrayList<ArrayList<Double>> training = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> test = new ArrayList<ArrayList<Double>>();
		
		Map<Integer, FolderRange> training_map = new HashMap<Integer, FolderRange>();
		Map<Integer, FolderRange> test_map = new HashMap<Integer, FolderRange>();
		for (int i = 0; i < mp.size(); i++) {
			FolderRange fr = mp.get(i+1);
			int length = fr.end - fr.start + 1;
			int train_len = (int) (length * ratio);
			String label = fr.label;
			training_map.put(i+1, new FolderRange(fr.start, fr.start + train_len - 1, label));
			test_map.put(i+1, new FolderRange(fr.start + train_len, fr.end, label));
			// System.out.println(fr.start + " " + (fr.start + train_len - 1) + " " + (fr.start + train_len) + " " + fr.end);
		}
		
		ArrayList<String> training_label = new ArrayList<String>();
		ArrayList<String> test_label = new ArrayList<String>();
		
		for (int i = 0; i < sample.size(); i++) {
			for (int j = 0; j < mp.size(); j++) {
				// i + 1 because index starts from 1
				if (i + 1 >= training_map.get(j+1).start && i + 1 <= training_map.get(j+1).end) {
					training.add(sample.get(i));
					training_label.add(training_map.get(j+1).label);
				}
				else if (i + 1 >= test_map.get(j+1).start && i + 1 <= test_map.get(j+1).end) {
					test.add(sample.get(i));
					test_label.add(training_map.get(j+1).label);
				}
			}
		}
		
		DataSet training_data = new DataSet(training, training_label);
		DataSet test_data = new DataSet(test, test_label);
		return new TrainingTestData(training_data, test_data);
	}

	public static TrainingTestData cv_clustering(ArrayList<ArrayList<Double>> sample, Map<Integer,FolderRange> mp) {
		ArrayList<ArrayList<Double>> training = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> test = new ArrayList<ArrayList<Double>>();
		
		Map<Integer, FolderRange> training_map = new HashMap<Integer, FolderRange>();
		Map<Integer, FolderRange> test_map = new HashMap<Integer, FolderRange>();
		// 15 maps
		for (int i = 0; i < mp.size(); i++) {
			FolderRange fr = mp.get(i+1);
			String label = fr.label;
			training_map.put(i+1, new FolderRange(fr.start, fr.end, label));
			// System.out.println(fr.start + " " + (fr.start + train_len - 1) + " " + (fr.start + train_len) + " " + fr.end);
		}
		
		ArrayList<String> training_label = new ArrayList<String>();
		ArrayList<String> test_label = new ArrayList<String>();
		
		for (int i = 0; i < sample.size(); i++) {
			for (int j = 0; j < mp.size(); j++) {
				// i + 1 because index starts from 1
				if (i + 1 >= training_map.get(j+1).start && i + 1 <= training_map.get(j+1).end) {
					training.add(sample.get(i));
					training_label.add(training_map.get(j+1).label);
				}
			}
		}
		for (int i = sample.size() - 20; i < sample.size(); i++) {
			test.add(sample.get(i));
			test_label.add("none");
		}
		
		DataSet training_data = new DataSet(training, training_label);
		DataSet test_data = new DataSet(test, test_label);
		return new TrainingTestData(training_data, test_data);
	}
}
