package me.andimeo;

import java.util.List;

public class HistoryItem {
	private FilterCondition condition;
	private List<Stock> stocks;

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
