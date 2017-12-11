package tfidfReader;

import java.util.ArrayList;

public class FolderDiscrete {

	public ArrayList<Integer> folder_list;
	public String label;
	public FolderDiscrete(ArrayList<Integer> folder_list, String label) {
		// 1 based
		this.folder_list = folder_list;
		this.label = label;
	}
}
