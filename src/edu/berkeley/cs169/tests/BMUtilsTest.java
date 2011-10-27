package edu.berkeley.cs169.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.utils.BMUtils;

public class BMUtilsTest {

	@Test
	public void testTextToMorse() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 1, 1, 1, 1,
				0, 1, 0, 1, 2, 1, 1, 0, 1, 2, 1, 1, 0, 2, 2, 2 });
		MorseCodeModel m = BMUtils.textToMorse("HELLO");
		assertEquals(m, expected);
	}

	@Test
	public void testMorseToText() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 1, 1, 1, 0,
				1, 0, 1, 2, 1, 1, 0, 1, 2, 1, 1, 0, 2, 2, 2 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "HELLO");
	}
}
