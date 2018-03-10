package me.andimeo;

import java.util.List;

public class Stock {
	private String market;
	private String code;
	private List<DataRecord> records;
	
	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<DataRecord> getRecords() {
		return records;
	}

	public void setRecords(List<DataRecord> records) {
		this.records = records;
	}

	public Stock(String market, String code, List<DataRecord> records) {
		this.market = market;
		this.code = code;
		this.records = records;
	}
}
