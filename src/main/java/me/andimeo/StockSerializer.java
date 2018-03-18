package me.andimeo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class StockSerializer {
	public static void deserialize(File file, Set<String> codes) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String code = line.trim();
				if (code.isEmpty()) {
					continue;
				}
				codes.add(code);
			}
		} finally {
			reader.close();
		}
	}

	public static void serialize(File file, Set<String> codes) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			for (String code : codes) {
				writer.write(code + "\n");
			}
		}
	}
}
