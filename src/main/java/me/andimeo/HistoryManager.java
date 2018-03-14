package me.andimeo;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
	private List<HistoryItem> history;

	public HistoryManager() {
		history = new ArrayList<>();
	}

	public void addHistory(HistoryItem item) {
		history.add(item);
	}

	public HistoryItem getHistory(int index) {
		if (index >= history.size()) {
			return null;
		}
		return history.get(index);
	}

	public int size() {
		return history.size();
	}
	
	public void truncate(int index) {
		history.subList(index, history.size()).clear();
	}
}
