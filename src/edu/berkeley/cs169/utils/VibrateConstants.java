package edu.berkeley.cs169.utils;

/**
 * Refactored
 */
public class VibrateConstants {
	public static final long SPEED_BASE = 100;
	public static final long DOT = SPEED_BASE;
	public static final long DASH = SPEED_BASE * 3;
	public static final long GAP = SPEED_BASE;
	public static final long LETTER_GAP = SPEED_BASE * 3;
	public static final long WORD_GAP = SPEED_BASE * 7;

	/** The characters from 'A' to 'Z' */
	public static final long[][] LETTERS = new long[][] {
	/* A */new long[] { DOT, GAP, DASH },
	/* B */new long[] { DASH, GAP, DOT, GAP, DOT, GAP, DOT },
	/* C */new long[] { DASH, GAP, DOT, GAP, DASH, GAP, DOT },
	/* D */new long[] { DASH, GAP, DOT, GAP, DOT },
	/* E */new long[] { DOT },
	/* F */new long[] { DOT, GAP, DOT, GAP, DASH, GAP, DOT },
	/* G */new long[] { DASH, GAP, DASH, GAP, DOT },
	/* H */new long[] { DOT, GAP, DOT, GAP, DOT, GAP, DOT },
	/* I */new long[] { DOT, GAP, DOT },
	/* J */new long[] { DOT, GAP, DASH, GAP, DASH, GAP, DASH },
	/* K */new long[] { DASH, GAP, DOT, GAP, DASH },
	/* L */new long[] { DOT, GAP, DASH, GAP, DOT, GAP, DOT },
	/* M */new long[] { DASH, GAP, DASH },
	/* N */new long[] { DASH, GAP, DOT },
	/* O */new long[] { DASH, GAP, DASH, GAP, DASH },
	/* P */new long[] { DOT, GAP, DASH, GAP, DASH, GAP, DOT },
	/* Q */new long[] { DASH, GAP, DASH, GAP, DOT, GAP, DASH },
	/* R */new long[] { DOT, GAP, DASH, GAP, DOT },
	/* S */new long[] { DOT, GAP, DOT, GAP, DOT },
	/* T */new long[] { DASH },
	/* U */new long[] { DOT, GAP, DOT, GAP, DASH },
	/* V */new long[] { DOT, GAP, DOT, GAP, DASH },
	/* W */new long[] { DOT, GAP, DASH, GAP, DASH },
	/* X */new long[] { DASH, GAP, DOT, GAP, DOT, GAP, DASH },
	/* Y */new long[] { DASH, GAP, DOT, GAP, DASH, GAP, DASH },
	/* Z */new long[] { DASH, GAP, DASH, GAP, DOT, GAP, DOT }, };

	/** The characters from '0' to '9' */
	public static final long[][] NUMBERS = new long[][] {
	/* 0 */new long[] { DASH, GAP, DASH, GAP, DASH, GAP, DASH, GAP, DASH },
	/* 1 */new long[] { DOT, GAP, DASH, GAP, DASH, GAP, DASH, GAP, DASH },
	/* 2 */new long[] { DOT, GAP, DOT, GAP, DASH, GAP, DASH, GAP, DASH },
	/* 3 */new long[] { DOT, GAP, DOT, GAP, DOT, GAP, DASH, GAP, DASH },
	/* 4 */new long[] { DOT, GAP, DOT, GAP, DOT, GAP, DOT, GAP, DASH },
	/* 5 */new long[] { DOT, GAP, DOT, GAP, DOT, GAP, DOT, GAP, DOT },
	/* 6 */new long[] { DASH, GAP, DOT, GAP, DOT, GAP, DOT, GAP, DOT },
	/* 7 */new long[] { DASH, GAP, DASH, GAP, DOT, GAP, DOT, GAP, DOT },
	/* 8 */new long[] { DASH, GAP, DASH, GAP, DASH, GAP, DOT, GAP, DOT },
	/* 9 */new long[] { DASH, GAP, DASH, GAP, DASH, GAP, DASH, GAP, DOT }, };

	public static final long[] ERROR_GAP = new long[] { GAP };
}
