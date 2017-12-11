package tfidfReader;

public class FolderRange {
	public Integer start;
	public Integer end;
	public String label;
	public FolderRange(Integer start, Integer end, String label) {
		// 1 based
		this.start = start;
		this.end = end;
		this.label = label;
	}
}
