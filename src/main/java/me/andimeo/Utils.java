package me.andimeo;

import org.joda.time.DateTime;

import me.andimeo.FilterCondition.LineType;

public class Utils {
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isSameWeek(TradingDate date1, TradingDate date2) {
		return isSameWeek(new DateTime(date1.year, date1.month, date1.day, 0, 0),
				new DateTime(date2.year, date2.month, date2.day, 0, 0));
	}

	private static boolean isSameWeek(DateTime d1, DateTime d2) {
		final int week1 = d1.getWeekOfWeekyear();
		final int week2 = d2.getWeekOfWeekyear();

		final int year1 = d1.getWeekyear();
		final int year2 = d2.getWeekyear();

		final int era1 = d1.getEra();
		final int era2 = d2.getEra();

		// Return true if week, year and era matches
		return (week1 == week2) && (year1 == year2) && (era1 == era2);
	}

	private static boolean isSameMonth(TradingDate date1, TradingDate date2) {
		return date1.year == date2.year && date1.month == date2.month;
	}

	public static void err(String s) {
		System.err.println(s);
	}

	public static boolean isSameXXX(TradingDate date1, TradingDate date2, LineType type) {
		switch (type) {
		case DAY:
			return date1.equals(date2);
		case WEEK:
			return isSameWeek(date1, date2);
		case MONTH:
			return isSameMonth(date1, date2);
		}
		return false;
	}
}
