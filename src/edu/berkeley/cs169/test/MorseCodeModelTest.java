package edu.berkeley.cs169.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.Utils;

public class MorseCodeModelTest {

	@Test
	public void testGetLastChar() {
		String text = "HELLO";
		MorseCodeModel morse = Utils.textToMorse(text);
		assertEquals(morse.getLastChar(), 'O');
	}

	@Test
	public void testGetLastChar2() {
		String text = "A ";
		MorseCodeModel morse = Utils.textToMorse(text);
		assertEquals(morse.getLastChar(), ' ');
	}
}
