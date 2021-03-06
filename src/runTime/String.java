package runTime;

import java.io.IOException;

/**
 * The class the implements Kitten strings. When we refer to Java's java.lang.String,
 * we must use its fully qualified name.
 */

public class String {
	private java.lang.String value;

	/* creates the empty string */
	public String() {
		value = new java.lang.String();
	}

	/* clones a string */
	public String(java.lang.String other) {
		value = other;
	}

	// yields the length of a string */
	public int length() {
		return value.length();
	}

	/* converts a string into integer */
	public int toInt() {
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			System.out.println("illegal integer format");
			return 0;
		}
	}

	/* converts a string into float */
	public float toFloat() {
		try {
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e) {
			System.out.println("illegal float format");
			return 0;
		}
	}

	/* checks that two strings are equal */
	public boolean equals(String other) {
		return value.equals(other.value);
	}

	/* prints the string to the screen */
	public void output() {
		System.out.print(value);
	}

	/* reads from the keyboard a sequence of character until the first newline and
	   stores it into the string */
	public void input() {
		char c;

		value = new java.lang.String();

		try {
			while ((c = (char) System.in.read()) != '\n') value += c;
		}
		catch (IOException e) {
			System.out.println("I/O exception");
		}
	}

	/* yields the concatenation of "this" and then "s" */
	public String concat(String s) {
		return new String(value + s.value);
	}

	/* yields the concatenation of "this" and then "f" */
	public String concat(float f) {
		return new String(value + f);
	}

	/* yields the concatenation of "this" and then "i" */
	public String concat(int i) {
		return new String(value + i);
	}

	/* yields the concatenation of "this" and then "b" */
	public String concat(boolean b) {
		return new String(value + b);
	}
}