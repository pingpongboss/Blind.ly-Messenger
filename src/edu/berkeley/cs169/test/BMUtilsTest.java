package edu.berkeley.cs169.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.Utils;

public class BMUtilsTest {

	@Test
	public void testMorseToText1() {
		long[] array = new long[] { 1, 1, 1, 1, 0, 1, 0, 1, 2, 1, 1, 0, 1, 2,
				1, 1, 0, 2, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "HELLO");
	}

	@Test
	public void testMorseToText2() {
		long[] array = new long[] { 1, 2, 0, 2, 1, 1, 1, 0, 0, 2, 1, 2, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "AB C");
	}

	@Test
	public void testMorseToText3() {
		long[] array = new long[] { 1, 1, 1, 1, 0, 1, 1, 0, 0, 2, 0, 1, 1, 1,
				1, 0, 1, 0, 1, 2, 1, 0, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "HI THERE");
	}

	@Test
	public void testMorseToText4() {
		long[] array = new long[] { 1, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "A");
	}

	@Test
	public void testMorseToText5() {
		long[] array = new long[] { 2, 2, 2, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "0");
	}

	@Test
	public void testMorseToText6() {
		long[] array = new long[] { 1, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 1, 1,
				1, 0, 1, 1, 2, 0, 2, 1, 2, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "WOOSUK55");
	}

	@Test
	public void testMorseToText7() {
		long[] array = new long[] { 1, 2, 2, 2, 2, 0, 0, 1, 1, 2, 2, 2, 0, 0,
				1, 1, 1, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "1 2 3");
	}

	@Test
	public void testMorseToText8() {
		long[] array = new long[] { 2, 1, 2, 1, 0, 1, 1, 1, 0, 1, 2, 2, 2, 2,
				0, 2, 1, 1, 1, 1, 0, 2, 2, 2, 2, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel morse = new MorseCodeModel(list);
		String s = Utils.morseToText(morse);
		assertEquals(s, "CS169");
	}

	@Test
	public void testTextToMorse1() {
		long[] array = new long[] { 1, 1, 1, 1, 0, 1, 0, 1, 2, 1, 1, 0, 1, 2,
				1, 1, 0, 2, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);

		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("HELLO");

		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse2() {
		long[] array = new long[] { 1, 2, 0, 2, 1, 1, 1, 0, 0, 2, 1, 2, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("AB C");
		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse3() {
		long[] array = new long[] { 1, 1, 1, 1, 0, 1, 1, 0, 0, 2, 0, 1, 1, 1,
				1, 0, 1, 0, 1, 2, 1, 0, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("HI THERE");
		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse4() {
		long[] array = new long[] { 2, 2, 2, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("0");

		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse5() {
		long[] array = new long[] { 2, 1, 2, 1, 0, 1, 1, 1, 0, 1, 2, 2, 2, 2,
				0, 2, 1, 1, 1, 1, 0, 2, 2, 2, 2, 1 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("CS169");

		assertEquals(expected, m);
	}

	@Test
	public void testTextToMorse6() {
		long[] array = new long[] { 1, 2, 2, 2, 2, 0, 0, 1, 1, 2, 2, 2, 0, 0,
				1, 1, 1, 2, 2 };
		ArrayList<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		MorseCodeModel expected = new MorseCodeModel(list);
		MorseCodeModel m = Utils.textToMorse("1 2 3");

		assertEquals(expected, m);
	}

	@Test
	public void testInverse() {

		assertEquals(Utils.morseToText(Utils.textToMorse("HELLO")), "HELLO");
	}

	@Test
	public void testInverse1() {

		assertEquals(Utils.morseToText(Utils.textToMorse("1 2 3")), "1 2 3");
	}

	@Test
	public void testInverse2() {

		assertEquals(Utils.morseToText(Utils.textToMorse("1 23")), "1 23");
	}

	@Test
	public void testInverse3() {

		assertEquals(Utils.morseToText(Utils.textToMorse("AB C")), "AB C");
	}
}
