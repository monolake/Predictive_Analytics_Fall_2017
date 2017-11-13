import java.io.*;
import java.util.*;
import net.sf.javaml.core.*;
import net.sf.javaml.clustering.*;
import net.sf.javaml.clustering.evaluation.*;
import net.sf.javaml.tools.data.FileHandler;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import net.sf.javaml.distance.CosineSimilarity;

class ResultClass {
	ArrayList<Double> doc_vec;
	Integer doc_index;
	public ResultClass (ArrayList<Double> doc_vec, Integer doc_index) {
		this.doc_vec = doc_vec;
		this.doc_index = doc_index;
	}
};

class FolderRange {
	Integer start;
	Integer end;
	public FolderRange(Integer start, Integer end) {
		// 0 based
		this.start = start - 1;
		this.end = end - 1;
	}
}

class ClusterIndex {
	ArrayList<ArrayList<Double>> points;
	ArrayList<Integer> index;
	public ClusterIndex (ArrayList<ArrayList<Double>> points, ArrayList<Integer> index) {
		this.points = points;
		this.index = index;
	}
}

public class main {
	
	
	public static ArrayList<Double> listSum(ArrayList<Double> list_1, ArrayList<Double> list_2) {
		if (list_1.size() == 0)
			return list_2;
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i = 0; i < list_1.size(); i++) {
			res.add(0.0);
		}
		for (int i = 0; i < list_1.size(); i++) {
			res.set(i, list_1.get(i) + list_2.get(i));
		}
		return res;
	}
	
	public static ArrayList<Double> normList(ArrayList<Double> list, Integer N) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i) / N);
		}
		return list;
	}

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
	
	public static LinkedHashMap<Integer, ClusterIndex> KMeans(
			ArrayList<ArrayList<Double>> dataset, int K, String measure, int iter, 
			ArrayList<FolderRange> fr) {
		
		ArrayList<ArrayList<Double>> center = new ArrayList<ArrayList<Double>>();
		LinkedHashMap<Integer, ClusterIndex> clusters = new LinkedHashMap<Integer, ClusterIndex>(K);

		if (fr.size() != 0) {

			for (int i = 0; i < fr.size(); i++) {
				int target = fr.get(i).start.intValue();
//				System.out.println(target);
				center.add(dataset.get(target)); //doc index
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(target);
				ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
				points.add(dataset.get(target));
				clusters.put(i, new ClusterIndex(points, list));
				//clusters.put(i,new ArrayList<ArrayList<Double>>());
			}
			System.out.println(clusters.get(0).index);
			System.out.println(clusters.get(1).index);
			System.out.println(clusters.get(2).index);
		}
		else {
			int i = 0;
			for (; i < K; i++) {
				center.add(dataset.get(i));
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(i);
				ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
				points.add(dataset.get(i));
				clusters.put(i, new ClusterIndex(points, list));
				//clusters.put(i, new ClusterIndex(new ArrayList<ArrayList<Double>>(), list));
				//clusters.put(i, new ArrayList<ArrayList<Double>>());
			}
		}
		//System.out.println("Center: " + center);
		int count = 0;
		while (count < iter) {
			for (int i = 0; i < dataset.size(); i++) {
				for (int fri = 0; fri < fr.size(); fri++) {
					if (i >= fr.get(fri).start.intValue() && i <= fr.get(fri).end.intValue()) {
						System.out.println(i);
						ArrayList<Double> point = dataset.get(i); // point in consideration
						int OldCluster = -1;
						for (int ii = 0; ii < clusters.size(); ii++) {
							if (clusters.get(ii).points.contains(point)) {
								OldCluster = ii;
								//System.out.println("OldCluster: " + OldCluster);
							}
						}
	
						Integer cluster = -1;
						if (measure.equals("Euclidean")) {
							ArrayList<Double> dist_vec = new ArrayList<Double>();
							Double min_vec = Double.MAX_VALUE;
							for (int j = 0; j < K; j++) {
								Double distance = 0.0;
								ArrayList<Double> v = center.get(j);
								//System.out.println(v);
								distance = Euclidean(point, v);
								
								//System.out.println(SumSquare);
								if (distance < min_vec) {
									min_vec = distance;
									cluster = j; // identify which cluster this point should belong to
								}
							}
						}
						else if (measure.equals("Cosine")) {
							ArrayList<Double> dist_vec = new ArrayList<Double>();
							Double max_vec = Double.MIN_VALUE;
							for (int j = 0; j < K; j++) {
								Double distance = 0.0;
								ArrayList<Double> v = center.get(j);
								//System.out.println(v);
								distance = Cosine(point, v);
								
								//System.out.println(SumSquare);
								if (distance > max_vec) {
									max_vec = distance;
									cluster = j; // identify which cluster this point should belong to
								}
							}
						}
		
						//System.out.println("OldCLusters " + OldCluster + " Point " + point);
						//System.out.println("NewCLusters " + cluster + " Point " + point);
						if (!clusters.get(cluster).points.contains(point)) {
							clusters.get(cluster).points.add(point);
							clusters.get(cluster).index.add(new Integer(i));
							if (OldCluster != -1) {
								//System.out.println(OldCluster);
								clusters.get(OldCluster).points.remove(point);
								clusters.get(OldCluster).index.remove(new Integer(i));
							}
						}
						//System.out.println(clusters);
					}
				}
			}
			//System.out.println("passed");
			//System.out.println(clusters);
			//System.out.println("Iteration: " + count);
			// recompute centroid for each cluster, based on points in that cluster
			System.out.println("clustering done!");
			for (int i = 0; i < clusters.size(); i++) {
				ArrayList<Double> centroid = new ArrayList<Double>();
				// number of points in the cluster
				for (int j = 0; j < clusters.get(i).points.size(); j++) {
					centroid = listSum(centroid, clusters.get(i).points.get(j));
				}
				centroid = normList(centroid, clusters.get(i).points.size());
				center.set(i, centroid);
			}
			
			//System.out.println("Center: " + center);
			count += 1;
			
			
		}
		return clusters; 
	}
	
	public static Map<Double, ResultClass> KMostSimilar(
			ArrayList<ArrayList<Double>> docs, ArrayList<Double> doc, String measure, int K, FolderRange folder) {
		
		Map<Double, ResultClass> res = new TreeMap<Double, ResultClass>();
		if (measure.equals("Euclidean")) {
			Map<Double, ResultClass> map = new TreeMap<Double, ResultClass>();
			for (int i = 0; i < docs.size(); i++) {
				map.put(Euclidean(docs.get(i), doc), new ResultClass(docs.get(i), i));
			}
			Set set = map.entrySet();
			Iterator iterator = set.iterator();
			int i = 0;
			while (iterator.hasNext() && i < K) {
				
				Map.Entry me = (Map.Entry)iterator.next();
				res.put((Double) me.getKey(), (ResultClass)me.getValue());
				i++;
			}
		}
		else if (measure.equals("Cosine")) {
			Map<Double, ResultClass> map = new TreeMap<Double, ResultClass>(Collections.reverseOrder());
			for (int i = 0; i < docs.size(); i++) {
				map.put(Cosine(docs.get(i), doc), new ResultClass(docs.get(i), i));
			}
			
			Set set = map.entrySet();
			Iterator iterator = set.iterator();
			int i = 0;
			while (iterator.hasNext() && i < K) {
				
				Map.Entry me = (Map.Entry)iterator.next();
				res.put((Double) me.getKey(), (ResultClass)me.getValue());
				i++;
			}
		}
		return res;
	}
	
	public static double[][] list2double(ArrayList<ArrayList<Double>> cluster) {
		double[][] m = new double[cluster.size()][];
		for (int i = 0; i < cluster.size(); i++) {
			m[i] = new double[cluster.get(i).size()];
			for (int j = 0; j < cluster.get(i).size(); j++) {
				m[i][j] = cluster.get(i).get(j);
			}
		}
/*		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) 
				System.out.print(m[i][j] + " ");
			System.out.println();
		}*/
		return m;
	}
	public static void main(String []args) {
		
		try {	

			ArrayList<FolderRange> fr = new ArrayList<FolderRange>();
			// k = 3
			fr.add(new FolderRange(1, 8));
			fr.add(new FolderRange(47, 54));
			fr.add(new FolderRange(117, 122));
			
			// k = 15
/*			fr.add(new FolderRange(1, 8));
			fr.add(new FolderRange(9, 16));
			fr.add(new FolderRange(17, 20));
			fr.add(new FolderRange(21, 28));
			fr.add(new FolderRange(29, 41));
			fr.add(new FolderRange(42, 46));
			fr.add(new FolderRange(47, 54));
			fr.add(new FolderRange(55, 64));
			fr.add(new FolderRange(65, 68));
			fr.add(new FolderRange(69, 86));
			fr.add(new FolderRange(87, 94));
			fr.add(new FolderRange(95, 104));
			fr.add(new FolderRange(105, 111));
			fr.add(new FolderRange(112, 116));
			fr.add(new FolderRange(117, 122));*/
			Dataset data = new DefaultDataset();
			final String dir = System.getProperty("user.dir");
			System.out.println("current dir = " + dir);

			FileInputStream is = new FileInputStream(dir + "/DataSet-corpus/" + "TFIDF-TF-IDF.csv");
			// FileInputStream is = new FileInputStream(dir + "/DataSet-corpus/" + "binary.csv");
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			line = reader.readLine();
			//System.out.println(line);
			ArrayList<ArrayList<Double>> TFIDF = new ArrayList<ArrayList<Double>>();
			while (line != null) {
				//buffer.append(line);
				//buffer.append(" ");
				ArrayList<Double> temp = new ArrayList<Double>();
				
				String[] val_str = line.split("\t");
				//System.out.println(val_str.length);
				//double[] v = new double[val_str.length];
				for (int i = 0; i < val_str.length; i++) {
					double val = Double.parseDouble(val_str[i]);
					temp.add(val);
					//v[i] = val;
				}
				//System.out.println(Arrays.toString(v));
				TFIDF.add(temp);
				//Instance tmpInstance = new DenseInstance(v);
				//data.add(tmpInstance);
				line = reader.readLine();
			}
			reader.close();
			is.close();
			
			for (int i = 0; i < TFIDF.size(); i++) {
				for (int fri = 0; fri < fr.size(); fri++) {
					if (i >= fr.get(fri).start.intValue() && i <= fr.get(fri).end.intValue()) {
						double[] v = new double[TFIDF.get(i).size()];
						for (int j = 0; j < TFIDF.get(i).size(); j++) {
							double val = TFIDF.get(i).get(j).doubleValue();
							v[j] = val;
						}
						Instance tmpInstance = new DenseInstance(v);
						data.add(tmpInstance);
					}
				}
			}
			System.out.println("half finished");
			
			// 1. find K most similar docs
			
/*			for (int i = 0; i < 15; i += 5) {
				FolderRange folder = fr.get(i);
				ArrayList<Double> doc = TFIDF.get(folder.start);
				System.out.println("doc ID: " + folder.start);
				Map<Double, ResultClass> res = KMostSimilar(TFIDF, doc, "Euclidean", 3, folder);
				System.out.println("Euclidean");
				for (Double key : res.keySet()) {
					//System.out.println(res.get(i).size());
					System.out.println(key);
					//System.out.println(res.get(key).doc_vec);
					System.out.println(res.get(key).doc_index);
				}
				Map<Double, ResultClass> res2 = KMostSimilar(TFIDF, doc, "Cosine", 3, folder);
				System.out.println("Cosine");
				for (Double key : res2.keySet()) {
					System.out.println(key);
					System.out.println(res2.get(key).doc_index);
				}
			}*/
			
			// 2. self-implement K-means clustering algorithm
/*			for (int i = 0; i < TFIDF.size(); i++) {
				for (int j = 0; j < TFIDF.get(i).size(); j++)
					System.out.print(TFIDF.get(i).get(j) + " ");
				System.out.println();
			}*/


			LinkedHashMap<Integer, ClusterIndex> clusters = KMeans(TFIDF, 3, "Cosine", 10, fr);
			System.out.println("kmeans finished");
			System.out.println(clusters.size());
			//System.out.println("clusters " + clusters);
			for (int i = 0; i < clusters.size(); i++) {
				FileWriter fos = new FileWriter(dir + "/DataSet-corpus/" + "output" + Integer.toString(i) + ".txt");
				PrintWriter dos = new PrintWriter(fos);
				FileWriter fos2 = new FileWriter(dir + "/DataSet-corpus/" + "distance-matrix" + Integer.toString(i) + ".txt");
				PrintWriter dos2 = new PrintWriter(fos2);			
				// for each point in the cluster
				for (int j = 0; j < clusters.get(i).index.size(); j++) {
					
					dos.println(clusters.get(i).index.get(new Integer(j)));
					// for each value of point
					//System.out.println(clusters.get(i).get(j).size());
					for (int k = 0; k < clusters.get(i).points.get(j).size(); k++) {
						dos2.print(clusters.get(i).points.get(j).get(k) + "\t");
						//doc.print(clusters.get(i).index.get(j))
						//System.out.print(clusters.get(i).get(j).get(k) + "\t");
					}
					dos2.println();
					//System.out.println();
				}
				fos.close();
				dos.close();
			}

			// dimension reduction by SVD
			
			for (int i = 0; i < clusters.size(); i++) {
				double[][] m = list2double(clusters.get(i).points);
				
				Matrix A = new Matrix(m);
			
				if (A.getRowDimension() < A.getColumnDimension())
					A = A.transpose();

				System.out.println("row dimension of A: " + A.getRowDimension());
				System.out.println("col dimension of A: " + A.getColumnDimension());
				SingularValueDecomposition s = A.svd();
				Matrix U = s.getU();
				// U.print(9, 6);
				Matrix S = s.getS(); 
				Matrix V = s.getV();
				//V.print(9, 6);
				Matrix svalues = new Matrix(s.getSingularValues(), 1);
				//svalues.print(9, 6);
				
				if (A.getRowDimension() > 1 && A.getColumnDimension() > 1) {
					int[] r = new int[2];
					r[0] = 0;
					r[1] = 1;	
					Matrix newS = S.getMatrix(r, r);
					System.out.println(newS.getRowDimension());
					System.out.println(newS.getColumnDimension());
					//newS.print(9,  6);
					Matrix VT = V.transpose();
					Matrix newV = VT.getMatrix(r, 0, VT.getColumnDimension()-1);
					Matrix B = newS.times(newV);
					B = B.transpose();
					B.print(9, 6);
				}
				else {
					System.out.println(S.getRowDimension());
					System.out.println(S.getColumnDimension());
					Matrix VT = V.transpose();
					Matrix B = S.times(VT);
					B = B.transpose();
					B.print(9, 6);
				}
			}
			
			System.out.println("start javaml");
			Clusterer km = new KMeans(3,10, new CosineSimilarity());
			Dataset[] clusters_data = km.cluster(data);
			//ClusterEvaluation sse = new SumOfSquaredErrors();
			//double score = sse.score(clusters_data);
			//System.out.println(score);
			
			for (int i = 0; i < clusters_data.length; i++) {
				FileHandler.exportDataset(clusters_data[i], new File(dir + "/DataSet-corpus/" + "javaml-output" + Integer.toString(i) + ".txt"));
			}
			//sc.close();
/*			XMeans xm = new XMeans();
			net.sf.javaml.clustering.Clusterer jmlxm = new WekaClusterer(xm);
			Dataset[] clusters = jmlxm.cluster(data);
			System.out.println(clusters.length);
			net.sf.javaml.clustering.evaluation.ClusterEvaluation sse = new SumOfSquaredErrors();
			double score = sse.score(clusters);
			System.out.println(score);	
			for (int i = 0; i < clusters.length; i++) {
				FileHandler.exportDataset(clusters[i], new File(dir + "/DataSet-corpus/" + "javaml-output" + Integer.toString(i) + ".txt"));
			}	*/		
			System.out.println("job finished");

		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
