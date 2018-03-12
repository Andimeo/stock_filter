package me.andimeo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class RootDirFilenameFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		Set<String> set = new HashSet<>(Arrays.asList("cw", "ds", "ot", "sh", "sz"));
		if (set.contains(name)) {
			return true;
		}
		return false;
	}
}

class DataDirFilenameFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.matches(dir.getParentFile().getName() + "[0-9]{6}\\.day");
	}
}

class TradingDate {
	public TradingDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public int year;
	public int month;
	public int day;

	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + year;
		hash = hash * 31 + month;
		hash = hash * 31 + day;
		return hash;
	}

	public boolean equals(Object arg0) {
		TradingDate date = (TradingDate) arg0;
		return year == date.year && month == date.month && day == date.day;
	}

	public String toString() {
		return String.format("%4d%02d%02d", year, month, day);
	}
}

public class DataParser {
	private List<Stock> stocks = new ArrayList<>();
	private Set<TradingDate> tradingDates = new HashSet<TradingDate>();
	private TradingDate lastDate;

	public TradingDate getLastDate() {
		return lastDate;
	}

	public boolean isLegalTradingDate(int year, int month, int day) {
		return tradingDates.contains(new TradingDate(year, month, day));
	}

	public Set<Integer> yearSets() {
		Set<Integer> set = new TreeSet<Integer>();
		for (TradingDate date : tradingDates) {
			set.add(date.year);
		}
		return set;
	}

	public int parse(File rootDir) throws IOException {
		if (!rootDir.exists() || !rootDir.isDirectory()) {
			return -1;
		}
		File[] files = rootDir.listFiles(new RootDirFilenameFilter());
		if (files.length != 5) {
			return -1;
		}

		int num = 0;
		for (File file : files) {
			File dataDir = new File(file, "lday");
			num += parseLday(dataDir);
		}
		calculate();
		return num;
	}

	public List<Stock> getStocksFromSpecificMarket(String market) {
		return stocks.stream().filter(stock -> market.equals(stock.getMarket())).collect(Collectors.toList());
	}

	public List<Stock> getAllStocks() {
		return stocks;
	}

	private int parseLday(File dataDir) throws IOException {
		if (!dataDir.exists() || !dataDir.isDirectory()) {
			return 0;
		}
		String market = dataDir.getParentFile().getName();
		System.out.println(market);
		File[] files = dataDir.listFiles(new DataDirFilenameFilter());
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			String code = file.getName().substring(2, 8);
			List<DataRecord> list = DataRecord.from(content);
			Stock stock = new Stock(market, code, list);
			stocks.add(stock);
		}
		return files.length;
	}

	private void calculate() {
		int maxLength = -1;
		Stock stockWithMaxLength = null;
		for (Stock stock : stocks) {
			List<DataRecord> records = stock.getRecords();
			if (records.size() > maxLength) {
				maxLength = records.size();
				stockWithMaxLength = stock;
			}
		}
		if (stockWithMaxLength == null) {
			return;
		}
		for (DataRecord record : stockWithMaxLength.getRecords()) {
			TradingDate date = record.getDate();
			tradingDates.add(date);
			lastDate = date;
		}
	}
}
