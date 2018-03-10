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

public class DataParser {
	private List<Stock> stocks = new ArrayList<>();

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

}
