package crossvalidation;

import java.util.ArrayList;

public class DataSet {
	public ArrayList<ArrayList<Double>> feature;
	public ArrayList<String> label;
	DataSet(ArrayList<ArrayList<Double>> feature, ArrayList<String> label) {
		this.feature = feature;
		this.label = label;
	}
	
	public int size() {
		return feature.size();
	}
}
