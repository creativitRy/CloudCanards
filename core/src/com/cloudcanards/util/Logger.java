package com.cloudcanards.util;

import com.badlogic.gdx.Gdx;

/**
 * Logger
 * Prints messages
 *
 * @author creativitRy
 */
public class Logger
{
	private Logger()
	{
	
	}
	
	/**
	 * Prints all objects with a space in between
	 *
	 * @param objects objects to print
	 */
	public static void logAll(Object... objects)
	{
		logAllWithSeparator(" ", objects);
	}
	
	/**
	 * Prints all objects with the specified separator in between
	 *
	 * @param separator string to print between objects
	 * @param objects   objects to print
	 */
	public static void logAllWithSeparator(String separator, Object... objects)
	{
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (Object object : objects)
		{
			if (first)
				first = false;
			else
				sb.append(separator);
			sb.append(object);
		}
		
		log(sb.toString());
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(int message)
	{
		log("" + message);
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(long message)
	{
		log("" + message);
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(float message)
	{
		log("" + message);
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(double message)
	{
		log("" + message);
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(Object message)
	{
		log(message.toString());
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(String message)
	{
		Gdx.app.log(getTag(), message);
	}
	
	/**
	 * Gets the tag to print the message with
	 * Right now, prints the method caller
	 *
	 * @return tag
	 */
	private static String getTag()
	{
		//return "";
		//debug
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++)
		{
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0)
			{
				String temp = ste.getClassName();
				int index = temp.lastIndexOf(".") + 1;
				if (index == 0)
					return temp;
				return temp.substring(index);
			}
		}
		return "";
	}
}
