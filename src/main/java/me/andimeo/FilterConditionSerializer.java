package me.andimeo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.andimeo.FilterCondition.LineType;
import me.andimeo.FilterCondition.PositionType;

public class FilterConditionSerializer {
	public static void serialize(File file, FilterCondition condition) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(String.format("%d\n", condition.getLineType().ordinal()));
			TradingDate date = condition.getDate();
			writer.write(String.format("%d %d %d\n", date.year, date.month, date.day));
			if (condition.positionType() == PositionType.NONE) {
				writer.write("0");
			} else if (condition.positionType() == PositionType.LONG) {
				writer.write("1");
				for (int position : condition.getPositions()) {
					writer.write(" " + position);
				}
			} else {
				writer.write("2");
				for (int position : condition.getPositions()) {
					writer.write(" " + position);
				}
			}
			writer.write("\n");

			if (condition.getLowerLimit() != Double.NEGATIVE_INFINITY) {
				writer.write("" + condition.getLowerLimit());
			} else {
				writer.write("-1");
			}
			writer.write(" ");
			if (condition.getUpperLimit() != Double.POSITIVE_INFINITY) {
				writer.write("" + condition.getUpperLimit());
			} else {
				writer.write("-1");
			}
			writer.write("\n");
		}
	}

	public static void deserialize(File file, FilterCondition condition) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			if (line == null) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			int v = Integer.parseInt(line);
			if (v == 0) {
				condition.setLineType(LineType.DAY);
			} else if (v == 1) {
				condition.setLineType(LineType.WEEK);
			} else if (v == 2) {
				condition.setLineType(LineType.MONTH);
			}

			line = reader.readLine();
			if (line == null) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			String[] parts = line.split(" ");
			if (parts.length != 3) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			condition.setDate(new TradingDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2])));

			line = reader.readLine();
			if (line == null) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			parts = line.split(" ");
			if (parts.length < 1) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			int type = Integer.parseInt(parts[0]);
			List<Integer> positions = new ArrayList<>();
			if (type == 0) {
				condition.setPositionType(PositionType.NONE);
			} else {
				if (type == 1) {
					condition.setPositionType(PositionType.LONG);
				} else if (type == 2) {
					condition.setPositionType(PositionType.SHORT);
				} else {
					Utils.err("FilterCondition deserialization failed");
					return;
				}
				for (int i = 1; i < parts.length; i++) {
					positions.add(Integer.parseInt(parts[i]));
				}
			}
			condition.setPositions(positions);

			line = reader.readLine();
			if (line == null) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			parts = line.split(" ");
			if (parts.length != 2) {
				Utils.err("FilterCondition deserialization failed");
				return;
			}
			
			double lower = Double.NEGATIVE_INFINITY;
			if (!"-1".equals(parts[0])) {
				lower = Double.parseDouble(parts[0]);
			}
			condition.setLowerLimit(lower);
			
			double upper = Double.POSITIVE_INFINITY;
			if (!"-1".equals(parts[1])) {
				upper = Double.parseDouble(parts[1]);
			}
			condition.setUpperLimit(upper);
		}
	}
}
