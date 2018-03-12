package me.andimeo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.andimeo.FilterCondition.LineType;

public class Stock {
	private String market;
	private String code;
	private List<DataRecord> records;
	private List<TradingDate> weekDate = new ArrayList<>();
	private List<TradingDate> monthDate = new ArrayList<>();

	public List<TradingDate> getWeekDate() {
		return weekDate;
	}

	public void setWeekDate(List<TradingDate> weekDate) {
		this.weekDate = weekDate;
	}

	public List<TradingDate> getMonthDate() {
		return monthDate;
	}

	public void setMonthDate(List<TradingDate> monthDate) {
		this.monthDate = monthDate;
	}

	public List<TradingDate> getDayDate() {
		return records.stream().map(DataRecord::getDate).collect(Collectors.toList());
	}

	public List<Double> getDayClosePrice() {
		return records.stream().map(DataRecord::getClosePrice).collect(Collectors.toList());
	}

	public List<Double> getDayAmount() {
		return records.stream().map(DataRecord::getAmount).collect(Collectors.toList());
	}

	private List<Double> weekClosePrice = new ArrayList<>();
	private List<Double> monthClosePrice = new ArrayList<>();
	private List<Double> weekAmount = new ArrayList<>();
	private List<Double> monthAmount = new ArrayList<>();

	public String getMarket() {
		return market;
	}

	public List<Double> getWeekClosePrice() {
		return weekClosePrice;
	}

	public List<Double> getMonthClosePrice() {
		return monthClosePrice;
	}

	public List<Double> getWeekAmount() {
		return weekAmount;
	}

	public List<Double> getMonthAmount() {
		return monthAmount;
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

		if (records.isEmpty()) {
			return;
		}
		DataRecord record = records.get(0);
		TradingDate lastDate = record.getDate();
		double wcp = record.getClosePrice();
		double wa = record.getAmount();
		double mcp = record.getClosePrice();
		double ma = record.getAmount();

		for (int i = 1; i < records.size(); i++) {
			record = records.get(i);
			TradingDate curDate = record.getDate();
			if (Utils.isSameXXX(lastDate, curDate, LineType.WEEK)) {
				wcp = record.getClosePrice();
				wa += record.getAmount();
			} else {
				weekClosePrice.add(wcp);
				weekAmount.add(wa);
				weekDate.add(lastDate);
				wcp = record.getClosePrice();
				wa = record.getAmount();
			}

			if (Utils.isSameXXX(lastDate, curDate, LineType.MONTH)) {
				mcp = record.getClosePrice();
				ma += record.getAmount();
			} else {
				monthClosePrice.add(mcp);
				monthAmount.add(ma);
				monthDate.add(lastDate);
				mcp = record.getClosePrice();
				ma = record.getAmount();
			}
			lastDate = curDate;
		}
		weekClosePrice.add(wcp);
		weekAmount.add(wa);
		weekDate.add(lastDate);
		monthClosePrice.add(mcp);
		monthAmount.add(ma);
		monthDate.add(lastDate);
	}
}
