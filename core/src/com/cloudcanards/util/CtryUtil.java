package com.cloudcanards.util;

/**
 * Description
 *
 * @author creativitRy
 */
public class CtryUtil
{
	static
	{
		System.out.println("[[WARNING: DEBUGGING]]\n");
	}
	
	/**
	 * Concats all with a space in between
	 *
	 * @param objects stuff to concat
	 * @return formatted string
	 */
	public static String toStringAll(Object... objects)
	{
		if (objects.length == 0)
		{
			return "";
		}
		StringBuilder s = new StringBuilder();
		for (Object object : objects)
		{
			if (object == null)
			{
				s.append(" null");
			}
			else
			{
				s.append(" ").append(object);
			}
		}
		
		return s.substring(1);
	}
	
	/**
	 * for printing 2D int arrays in a rectangle with a space in between all ints
	 *
	 * @param arr 2D int array
	 * @return formatted 2D int array
	 */
	public static String deepToString(int[][] arr)
	{
		StringBuilder s = new StringBuilder();
		for (int[] a : arr)
		{
			for (int b : a)
			{
				s.append(String.format("%-3d", b));
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
	/**
	 * for printing 2D char arrays in a rectangle
	 *
	 * @param arr 2D char array
	 * @return formatted 2D char array
	 */
	public static String deepToString(char[][] arr)
	{
		StringBuilder s = new StringBuilder();
		for (char[] a : arr)
		{
			for (char b : a)
			{
				s.append(b);
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
	/**
	 * for printing 2D boolean arrays in a rectangle
	 *
	 * @param arr       2D boolean array
	 * @param charFalse char to print if false
	 * @param charTrue  char to print if true
	 * @return formatted 2D boolean array
	 */
	public static String deepToString(boolean[][] arr, char charFalse, char charTrue)
	{
		StringBuilder s = new StringBuilder();
		for (boolean[] a : arr)
		{
			for (boolean b : a)
			{
				s.append(b ? charTrue : charFalse);
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
	public static String deepToString(Object[][] arr)
	{
		StringBuilder s = new StringBuilder();
		for (Object[] a : arr)
		{
			for (Object b : a)
			{
				s.append(b).append(" ");
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
	/**
	 * generates random uppercase alphabet
	 *
	 * @return random uppercase alphabet
	 */
	public static char randomAlphabet()
	{
		return (char) (Math.random() * ('Z' - 'A' + 1) + 'A');
	}
	
	public static void printAll(Object... objects)
	{
		System.out.println(toStringAll(objects));
	}
}