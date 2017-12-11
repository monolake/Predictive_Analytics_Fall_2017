package crossvalidation;

public class TrainingTestData {
	public DataSet training;
	public DataSet test;
	public TrainingTestData(DataSet training, DataSet test) {
		this.training = training;
		this.test = test;
	}
}
