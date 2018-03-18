package me.andimeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class CodeStockMap {
	private static String PATH = "/code-stock.txt";
	private static CodeStockMap instance = null;
	private Map<String, String> map = null;

	private CodeStockMap() {
		map = new HashMap<String, String>();
	}

	private void load() throws IOException, URISyntaxException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(PATH), "UTF-8"));
		reader.lines().forEach(s -> genPair(s));
	}

	private void genPair(String s) {
		String[] parts = s.split(" ");
		map.put(parts[0], parts[1]);
	}

	public static CodeStockMap instance() {
		if (instance == null) {
			instance = new CodeStockMap();
			try {
				instance.load();
			} catch (Exception e) {
				return null;
			}
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
