package edu.berkeley.cs169.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.utils.BMUtils;

public class BMUtilsTest {

	@Test
	public void testMorseToText1() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 1, 1, 1, 0,
				1, 0, 1, 2, 1, 1, 0, 1, 2, 1, 1, 0, 2, 2, 2 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "HELLO");
	}

	@Test
	public void testMorseToText2() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 2, 0, 2, 1,
				1, 1, 0, 0, 2, 1, 2, 1 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "AB C");
	}

	@Test
	public void testMorseToText3() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 1, 1, 1, 0,
				1, 1, 0, 0, 2, 0, 1, 1, 1, 1, 0, 1, 0, 1, 2, 1, 0, 1 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "HI THERE");
	}

	@Test
	public void testMorseToText4() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 1, 1, 1, 0,
				1, 1, 0, 0, 2, 0, 1, 1, 1, 1, 0, 1, 0, 1, 2, 1, 0, 1 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "HI THERE");
	}

	@Test
	public void testMorseToText5() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 2 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "A");
	}

	@Test
	public void testMorseToText6() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 2, 2, 2, 2, 2 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "0");
	}

	@Test
	public void testMorseToText7() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 2, 2, 0, 2,
				2, 2, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 2, 0, 2, 1, 2, 0, 1, 1,
				1, 1, 1, 0, 1, 1, 1, 1, 1 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "WOOSUK55");
	}

	@Test
	public void testMorseToText8() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 1, 2, 2, 2, 2,
				0, 0, 1, 1, 2, 2, 2, 0, 0, 1, 1, 1, 2, 2 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "1 2 3");
	}

	@Test
	public void testMorseToText9() {
		MorseCodeModel morse = new MorseCodeModel(new long[] { 2, 1, 2, 1, 0,
				1, 1, 1, 0, 1, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 0, 2, 2, 2, 2, 1 });
		String s = BMUtils.morseToText(morse);
		assertEquals(s, "CS169");
	}

	@Test
	public void testTextToMorse1() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 1, 1, 1, 1,
				0, 1, 0, 1, 2, 1, 1, 0, 1, 2, 1, 1, 0, 2, 2, 2 });
		MorseCodeModel m = BMUtils.textToMorse("HELLO");

		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse2() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 1, 2, 0, 2,
				1, 1, 1, 0, 0, 2, 1, 2, 1 });
		MorseCodeModel m = BMUtils.textToMorse("AB C");

		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse3() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 1, 1, 1, 1,
				0, 1, 1, 0, 0, 2, 0, 1, 1, 1, 1, 0, 1, 0, 1, 2, 1, 0, 1 });
		MorseCodeModel m = BMUtils.textToMorse("HI THERE");

		assertEquals(expected, m);
	}
	
	@Test
	public void testTextToMorse4() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 2, 2, 2, 2, 2 });
		MorseCodeModel m = BMUtils.textToMorse("0");

		assertEquals(expected, m);
	}
	
	@Test
	public void testTextToMorse5() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 2, 1, 2, 1, 0,
				1, 1, 1, 0, 1, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 0, 2, 2, 2, 2, 1 });
		MorseCodeModel m = BMUtils.textToMorse("CS169");

		assertEquals(expected, m);
	}
	
	@Test
	public void testTextToMorse6() {
		MorseCodeModel expected = new MorseCodeModel(new long[] { 1, 2, 2, 2, 2,
				0, 0, 1, 1, 2, 2, 2, 0, 0, 1, 1, 1, 2, 2});
		MorseCodeModel m = BMUtils.textToMorse("1 2 3");

		assertEquals(expected, m);
	}
	

	@Test
	public void testInverse() {
		assertEquals(BMUtils.morseToText(BMUtils.textToMorse("HELLO")), "HELLO");
	}
}