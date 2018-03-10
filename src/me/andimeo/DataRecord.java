package me.andimeo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class DataRecord {
	private int year;
	private int month;
	private int day;
	private double openPrice;
	private double maxPrice;
	private double minPrice;
	private double closePrice;
	private double ratio;
	private double amount; // 总额
	private int volume; // 总量

	public static double toDouble(byte[] bytes, int offset, int length) {
		return ByteBuffer.wrap(bytes, offset, length).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}

	public static int toInt(byte[] bytes, int offset, int length) {
		return ByteBuffer.wrap(bytes, offset, length).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	public static List<DataRecord> from(byte[] bytes) {
		int num = bytes.length / 32;
		List<DataRecord> list = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			int offset = i * 32;
			DataRecord record = new DataRecord();
			// date
			int value = toInt(bytes, offset, 4);
			record.setYear(value / 10000);
			record.setMonth(value % 10000 / 100);
			record.setDay(value % 100);

			// open price
			value = toInt(bytes, offset + 4, 4);
			record.setOpenPrice(value / 100.0);

			// max price
			value = toInt(bytes, offset + 8, 4);
			record.setMaxPrice(value / 100.0);

			// min price
			value = toInt(bytes, offset + 12, 4);
			record.setMinPrice(value / 100.0);

			// close price
			value = toInt(bytes, offset + 16, 4);
			record.setClosePrice(value / 100.0);

			// amount
			double v = toDouble(bytes, offset + 20, 4);
			record.setAmount(v / 10.0);

			// volume
			value = toInt(bytes, offset + 24, 4);
			record.setVolume(value);

			// unused

			if (i == 0) {
				record.setRatio(0);
			} else {
				int index = list.size() - 1;
				double preClosePrice = list.get(index).getClosePrice();
				record.setRatio((record.getClosePrice() - preClosePrice) / preClosePrice);
			}
			list.add(record);
		}
		return list;
	}

	private DataRecord() {
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

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String toString() {
		return "" + getYear() + "" + getMonth() + "" + getDay() + " " + getOpenPrice() + " " + getMaxPrice() + " "
				+ getMinPrice() + " " + getClosePrice() + " " + getRatio() + " " + getAmount() + " " + getVolume();
	}
}
