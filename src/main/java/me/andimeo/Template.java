package me.andimeo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import me.andimeo.FilterCondition.LineType;
import me.andimeo.FilterCondition.PositionType;

public class Template {
	// 日多，日空，周多，周空，月多，月空
	private Integer positions[] = new Integer[24];

	public Template() {
		for (int i = 0; i < 24; i++) {
			positions[i] = null;
		}
	}

	public void set(LineType lineType, PositionType positionType, int pos, Integer value) {
		if (positionType == PositionType.NONE || pos > 3 || pos < 0) {
			return;
		}

		int index = (lineType.ordinal() * 2 + positionType.ordinal()) * 4 + pos;
		positions[index] = value;
	}

	public Integer get(LineType lineType, PositionType positionType, int pos) {
		if (positionType == PositionType.NONE || pos > 3 || pos < 0) {
			return null;
		}
		int index = (lineType.ordinal() * 2 + positionType.ordinal()) * 4 + pos;
		return positions[index];
	}

	public void serialize(File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			for (int i = 0; i < 24; i++) {
				String text = "-1";
				if (positions[i] != null) {
					text = String.valueOf(positions[i]);
				}
				writer.write(text + "\n");
			}
		}
	}

	public void deserialize(File file) throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (int i = 0; i < 24; i++) {
				String line = reader.readLine();
				Integer value = null;
				try {
					value = Integer.parseInt(line);
				} catch (NumberFormatException e) {
				}
				if (value == -1) {
					positions[i] = null;
				} else {
					positions[i] = value;
				}
			}
		}
	}
}
