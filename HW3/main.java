import java.io.*;
import java.util.*;
import tfidfReader.*;
import crossvalidation.*;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import KNN.KNN;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
public class main {
	
	public static Double error(DataSet dataset, ArrayList<String> prediction) {
		// dataset - could be trained or test data, ground truth
		// predicted label
		Double error = 0.0;
		ArrayList<Character> hit = new ArrayList<Character>();
		for (int i = 0; i < dataset.label.size(); i++) {
			if (dataset.label.get(i).equals(prediction.get(i))) {
				hit.add('.');
				error += 0;
			}
			else {
				hit.add('*');
				error += 1;
			}
		}
		error /= dataset.label.size();
		//System.out.println(error);
		return error;
	}
	
	public static void writeToExcel(Object[][] error_data) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Java Books");
        int rowCount = 0;
        
        for (Object[] aBook : error_data) {
            Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                	cell.setCellValue((Integer) field);
                }
            }
             
        }
         
        try (FileOutputStream outputStream = new FileOutputStream("JavaBooks.xlsx")) {
            workbook.write(outputStream);
        }
        
	}
	public static void main(String[] args) throws IOException {

		System.out.println("hello world");
		System.out.println("start javaml");
		final String dir = System.getProperty("user.dir");
		System.out.println("current dir = " + dir);

		ArrayList<ArrayList<Double>> TFIDF = new ArrayList<ArrayList<Double>>();
		// FileInputStream is = new FileInputStream(dir + "/TFIDF-TF-IDF-unlabeled.csv");
		FileInputStream is = new FileInputStream(dir + "/TFIDF-TF-IDF.csv");
		tfidfReader tfidfreader = new tfidfReader();
		TFIDF = tfidfreader.read(is);
		System.out.println(TFIDF.size());
		
		Map<Integer, FolderRange> mp = new HashMap<Integer, FolderRange>();
		int[] start = {1, 9, 17, 21, 29, 42, 47, 55, 65, 69, 87, 95, 105, 112, 117};
		int[] length = {8, 8, 4, 8, 13, 5, 8, 10, 4, 18, 8, 10, 7, 5, 6};
		String[] label = {"Airline Safety", "Amphertamine", "China and Spy Plan and Captives", "Hoof and Mouth Desease", "Iran Nuclear", "Korea and Nuclear Capability", "Mortrage Rates", "Ocean and Pollution", "Satanic Cult", "Store Irene", "Volcano", "Saddam Hussein", "Kim Jong-un", "Predictive Analytics", "Irma & Harvey"};
		for (int i = 0; i < 15; i++) 
			mp.put(i+1, new FolderRange(start[i], start[i] + length[i] - 1, label[i]));
		
		//TrainingTestData ttd = crossvalidation.cv(TFIDF, 0.85, mp);
		//DataSet training = ttd.training;
		//DataSet test = ttd.test;
		

		TrainingTestData ttd = crossvalidation.cv_clustering(TFIDF, mp);
		//TrainingTestData ttd = crossvalidation.cv(TFIDF, mp);
		DataSet training = ttd.training;
		DataSet test = ttd.test;
		
		System.out.println(training.feature.size());
		System.out.println(test.feature.size());
		
		ArrayList<Double> training_error_klist = new ArrayList<Double>();
		ArrayList<Double> test_error_klist = new ArrayList<Double>();
		ArrayList<Integer> klist = new ArrayList<Integer>();
		
		int kmin = 1;
		int kmax = 10;
		for (int k = kmin; k < kmax; k++) {
			ArrayList<String> predicted = KNN.KNN(k, training, training, "Cosine", label);
			training_error_klist.add(error(training, predicted));
			//klist.add(k);
		}
		//System.out.println(error_klist);
		//Object[][] error_data = new Object[3][training_error_klist.size()];
		//error_data[0] = klist.toArray();
		//error_data[1] = training_error_klist.toArray();
		
		for (int k = kmin; k < kmax; k++) {
			ArrayList<String> predicted = KNN.KNN(k, training, test, "Cosine", label);
			test_error_klist.add(error(test, predicted));
			
		}
/*		ArrayList<String> predicted = KNN.KNN(2, training, test, "Cosine", label);
		for (int i = 0; i < predicted.size(); i++) {
			System.out.println(predicted.get(i));
		}*/
		//System.out.println(test_error_klist);	
		//error_data[2] = test_error_klist.toArray();

		// this is for 7-3 validation
		//writeToExcel(error_data);
		
		
		
		// 10 fold cross validation
		
		double[][] klist_10f = new double[10][kmax-kmin+1];
		for (int i = 0; i < 10; i++) {
			int index = i+1;
			ttd = crossvalidation.cv10(TFIDF, index, mp);
			training = ttd.training;
			test = ttd.test;	
			System.out.println("fold " + (i+1));
			System.out.println(training.feature.size());
			System.out.println(test.feature.size());
			//get best k for each i-th cross validation
			int count = 0;
			for (int k = kmin; k <= kmax; k++) {
				ArrayList<String> predicted = KNN.KNN(k, training, test, "Cosine", label);
				//wrong:klist_10f[i] = new double[predicted.size()];
				//System.out.println(predicted.size());
				try {
					if (test.feature.size() != predicted.size())
						System.out.println("Wrong length");
					klist_10f[i][count++] = error(test, predicted).doubleValue();
					if (k == 2) {
						int[][] folder_count = new int[predicted.size()][predicted.size()];
						for (int ii = 0; ii < test.label.size(); ii++) {
							int x = 0;
							int y = 0;
							for (int kk = 0; kk < label.length; kk++) {
								if (test.label.get(ii).equals(label[kk]))
										x = kk;
								if (predicted.get(ii).equals(label[kk]))
										y = kk;
							}
							folder_count[x][y] += 1;
						}
						for (int ii = 0; ii < 15; ii++) {
							for (int jj = 0; jj < 15; jj++) {
								System.out.print(folder_count[ii][jj] + " ");
							}
							System.out.println();
						}
					}
					//System.out.println(count);
				}
				catch (IllegalArgumentException e) {
					System.out.println("IllegalArgumentException caught");
				}
			}	
		}
		double[] avg_klist_10f = new double[kmax-kmin+1];
		Double smallest = Double.MAX_VALUE;
		int best_k =  0;
		for (int j = kmin; j <= kmax; j++) {
			for (int i = 0; i < 10; i++) {
				avg_klist_10f[j-1] += klist_10f[i][j-1];
			}
			avg_klist_10f[j-1] /= 10;
			if (avg_klist_10f[j-1] < smallest) {
				smallest = avg_klist_10f[j-1];
				best_k = j;
			}
		}
		for (int j = kmin; j <= kmax; j++) 
			System.out.println(avg_klist_10f[j-1]);
		
		for (int j = kmin; j <= kmax; j++) {
			for (int i = 0; i < 10; i++) {
				System.out.print(klist_10f[i][j-1] + " ");
			}
			System.out.println();
		}
		
		System.out.println("Best k value is " + best_k + " with error " + smallest);
		
		
		// compared to JAVA ML 
		Dataset data = FileHandler.loadDataset(new File("TFIDF-TF-IDF-labeled.csv"), 7844, ",");
		
		Classifier knn = new KNearestNeighbors(5);
		knn.buildClassifier(data);


        Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(knn, data);
        for (Object o : pm.keySet())
            System.out.println(o + ": " + pm.get(o).getAccuracy());
        
		System.out.println("Job Finished Good");
		
	}
}
