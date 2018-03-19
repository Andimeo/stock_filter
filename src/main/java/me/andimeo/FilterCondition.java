package me.andimeo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class FilterCondition {
	private LineType lineType;
	private TradingDate date;
	private PositionType positionType;
	private List<Integer> positions;
	private double lowerLimit;
	private double upperLimit;
	private int duration;

	enum LineType {
		DAY, WEEK, MONTH
	}

	enum PositionType {
		LONG, SHORT, NONE
	}

	public List<Stock> filter(List<Stock> stocks) {
		List<Stock> result = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			// date
			int index = -1;
			List<TradingDate> dates = null;
			switch (lineType) {
			case DAY:
				dates = stock.getDayDate();
				break;
			case WEEK:
				dates = stock.getWeekDate();
				break;
			case MONTH:
				dates = stock.getMonthDate();
				break;
			}
			for (int i = 0; i < dates.size(); i++) {
				if (Utils.isSameXXX(date, dates.get(i), lineType)) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				Utils.err("date not found! " + stock.getCode());
				continue;
			}

			// position
			List<Double> closePrice = null;
			switch (lineType) {
			case DAY:
				closePrice = stock.getDayClosePrice();
				break;
			case WEEK:
				closePrice = stock.getWeekClosePrice();
				break;
			case MONTH:
				closePrice = stock.getMonthClosePrice();
				break;
			}
			if (positionType != PositionType.NONE) {
				BiFunction<Double, Double, Boolean> op = null;
				if (positionType == PositionType.LONG) {
					op = (a, b) -> (a > b);
				} else {
					op = (a, b) -> (a < b);
				}
				boolean isPassed = true;
				for (int i = index; i > index - duration; i--) {
					if (!filterPosition(i, closePrice, op)) {
						isPassed = false;
						break;
					}
				}
				if (!isPassed) {
					continue;
				}
			}

			// amount
			List<Double> amount = null;
			switch (lineType) {
			case DAY:
				amount = stock.getDayAmount();
				break;
			case WEEK:
				amount = stock.getWeekAmount();
				break;
			case MONTH:
				amount = stock.getMonthAmount();
				break;
			}
			if (amount.get(index) < lowerLimit || amount.get(index) > upperLimit) {
				continue;
			}
			result.add(stock);
		}
		System.out.println("Totally " + result.size() + " stocks filtered out!");
		return result;
	}

	public boolean filterPosition(int index, List<Double> closePrice, BiFunction<Double, Double, Boolean> op) {
		List<Double> movingAverage = new ArrayList<Double>();
		movingAverage.add(closePrice.get(index));
		for (int pos : positions) {
			double v = 0;
			int count = 0;
			for (int i = Math.max(0, index - pos + 1); i <= index; i++) {
				v += closePrice.get(i);
				count++;
			}
			movingAverage.add(v / count);
		}
		for (int i = 1; i < movingAverage.size(); i++) {
			if (!op.apply(movingAverage.get(i - 1), movingAverage.get(i))) {
				return false;
			}
		}
		return true;
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public PositionType positionType() {
		return positionType;
	}

	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public TradingDate getDate() {
		return date;
	}

	public void setDate(TradingDate date) {
		this.date = date;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
