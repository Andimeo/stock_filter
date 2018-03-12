package me.andimeo;

import java.util.List;

public class HistoryItem {
	private int index;
	private FilterCondition condition;
	private List<Stock> stocks;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FilterCondition getCondition() {
		return condition;
	}

	public void setCondition(FilterCondition condition) {
		this.condition = condition;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}
}
