package me.andimeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CodeStockMap {
	private static String PATH = "src/main/resources/code-stock.txt";
	private static CodeStockMap instance = null;
	private Map<String, String> map = null;

	private CodeStockMap() {
		map = new HashMap<String, String>();
	}

	private void load() throws IOException {
		Files.lines(Paths.get(PATH)).forEach(s -> genPair(s));
	}

	private void genPair(String s) {
		String[] parts = s.split(" ");
		map.put(parts[0], parts[1]);
	}

	public static CodeStockMap instance() throws IOException {
		if (instance == null) {
			instance = new CodeStockMap();
			instance.load();
		}
		return instance;
	}

	public String getStockName(String code) {
		String name = map.get(code);
		if (name == null) {
			return "";
		}
		return name;
	}
}
