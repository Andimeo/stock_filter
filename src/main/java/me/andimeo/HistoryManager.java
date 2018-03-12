package me.andimeo;

import java.util.HashMap;
import java.util.Map;

public class HistoryManager {
	private Map<Integer, HistoryItem> map;

	public HistoryManager() {
		map = new HashMap<Integer, HistoryItem>();
	}

	public Map<Integer, HistoryItem> getMap() {
		return map;
	}

	public void setMap(Map<Integer, HistoryItem> map) {
		this.map = map;
	}

}
