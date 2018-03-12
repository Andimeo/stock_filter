package me.andimeo;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterConditionTest {

	@Test
	public void testFilterPosition() {
		Double[] vs = new Double[100];
		for (int i = 0; i < 100; i++) {
			vs[i] = i * 1.0;
		}
		List<Double> closePrice = Arrays.asList(vs);
		List<Integer> positions = Arrays.asList(5, 10, 20);
		FilterCondition condition = new FilterCondition();
		condition.setPositions(positions);
		assertTrue(condition.filterPosition(99, closePrice, (a, b) -> (a > b)));

		Collections.reverse(closePrice);
		assertTrue(condition.filterPosition(99, closePrice, (a, b) -> (a < b)));
	}
}
