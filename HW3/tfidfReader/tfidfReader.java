package tfidfReader;
import java.io.*;
import java.util.*;
import net.sf.javaml.core.*;
import net.sf.javaml.tools.data.FileHandler;

public class tfidfReader {
	
	public static ArrayList<ArrayList<Double>> read(FileInputStream is) throws IOException {
		
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine();
		//System.out.println(line);
		ArrayList<ArrayList<Double>> TFIDF = new ArrayList<ArrayList<Double>>();
		Dataset data = new DefaultDataset();
		while (line != null) {
			ArrayList<Double> temp = new ArrayList<Double>();
			
			String[] val_str = line.split("\t");
			double[] v = new double[val_str.length];
			for (int i = 0; i < val_str.length; i++) {
				double val = Double.parseDouble(val_str[i]);
				temp.add(val);
				v[i] = val;
			}
			//System.out.println(Arrays.toString(v));
			TFIDF.add(temp);
			Instance tmpInstance = new DenseInstance(v);
			data.add(tmpInstance);
			line = reader.readLine();
		}
		reader.close();
		return TFIDF;

	}
}
