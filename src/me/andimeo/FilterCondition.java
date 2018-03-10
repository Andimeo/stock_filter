package me.andimeo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FilterCondition {
	private LineType lineType;
	private int year;
	private int month;
	private int day;
	private PositionType positionType;
	private List<Integer> positions;
	private double lowerLimit;
	private double upperLimit;

	enum LineType {
		DAY, WEEK, MONTH
	}

	enum PositionType {
		LONG, SHORT, NONE
	}

	public List<Stock> filter(Enumeration<Stock> stocks) {
		return new ArrayList<>();
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
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
}
