package me.andimeo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import me.andimeo.FilterCondition.LineType;

public class UtilsTest {
	@Test
	public void testIsSameWeek() {
		TradingDate d1 = new TradingDate(2014, 12, 31);
		TradingDate d2 = new TradingDate(2015, 1, 1);
		TradingDate d3 = new TradingDate(2015, 1, 2);
		TradingDate d4 = new TradingDate(2015, 1, 8);

		assertTrue(Utils.isSameXXX(d1, d2, LineType.WEEK));
		assertTrue(Utils.isSameXXX(d2, d1, LineType.WEEK));

		assertTrue(Utils.isSameXXX(d2, d3, LineType.WEEK));
		assertTrue(Utils.isSameXXX(d3, d2, LineType.WEEK));

		assertFalse(Utils.isSameXXX(d2, d4, LineType.WEEK));
		assertFalse(Utils.isSameXXX(d4, d2, LineType.WEEK));

		assertFalse(Utils.isSameXXX(d1, d4, LineType.WEEK));
		assertFalse(Utils.isSameXXX(d4, d1, LineType.WEEK));
	}
}
