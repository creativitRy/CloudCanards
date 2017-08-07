package com.cloudcanards.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Logger
 * Prints messages
 *
 * @author creativitRy
 */
public class Logger
{
	/**
	 * Often printed
	 */
	public static final int INFO = 2;
	/**
	 * Rarely printed
	 */
	public static final int DEBUG = 3;
	/**
	 * Most likely printed
	 */
	public static final int ERROR = 1;
	
	private Logger() {}
	
	private static long startTime;
	
	private static int level = INFO;
	
	/**
	 * Gets the level
	 *
	 * @return level
	 * @see #INFO
	 * @see #DEBUG
	 * @see #ERROR
	 */
	public static int getLevel()
	{
		return level;
	}
	
	/**
	 * Sets the level of logging - how the logged messages will be graded
	 *
	 * @param level level
	 * @see #INFO
	 * @see #DEBUG
	 * @see #ERROR
	 */
	public static void setLevel(int level)
	{
		Logger.level = level;
	}
	
	/**
	 * Sets the logging level to {@link #INFO}
	 *
	 * @see #setLevel(int)
	 */
	public static void resetLevel()
	{
		setLevel(INFO);
	}
	
	static
	{
		startTime = TimeUtils.millis();
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
			{
				first = false;
			}
			else
			{
				sb.append(separator);
			}
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
	 * Logs a formatted string using the specified format string and
	 * arguments.
	 * <p>
	 * <p> The locale always used is the one returned by {@link
	 * java.util.Locale#getDefault() Locale.getDefault()}.
	 *
	 * @param format A <a href="../util/Formatter.html#syntax">format string</a>
	 * @param args   Arguments referenced by the format specifiers in the format
	 *               string.  If there are more arguments than format specifiers, the
	 *               extra arguments are ignored.  The number of arguments is
	 *               variable and may be zero.  The maximum number of arguments is
	 *               limited by the maximum dimension of a Java array as defined by
	 *               <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *               The behaviour on a
	 *               {@code null} argument depends on the <a
	 *               href="../util/Formatter.html#syntax">conversion</a>.
	 * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
	 *                                          specifier that is incompatible with the given arguments,
	 *                                          insufficient arguments given the format string, or other
	 *                                          illegal conditions.  For specification of all possible
	 *                                          formatting errors, see the <a
	 *                                          href="../util/Formatter.html#detail">Details</a> section of the
	 *                                          formatter class specification.
	 * @see java.util.Formatter
	 * @since 1.5
	 */
	public static void logf(String format, Object... args)
	{
		log(String.format(format, args));
	}
	
	/**
	 * Prints out a message
	 *
	 * @param message message to print
	 */
	public static void log(String message)
	{
		logWithLevel(getTag(), message);
	}
	
	private static void logWithLevel(String tag, String message)
	{
		switch (level)
		{
			case Application.LOG_INFO:
				Gdx.app.log(tag, message);
				break;
			case Application.LOG_ERROR:
				Gdx.app.error(tag, message);
				break;
			case Application.LOG_DEBUG:
				Gdx.app.debug(tag, message);
				break;
		}
	}
	
	/**
	 * Gets the tag to print the message with
	 * Right now, prints the method caller
	 *
	 * @return tag
	 */
	private static String getTag()
	{
		//return getTimestamp();
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
				{
					return getTimestamp() + ": " + temp;
				}
				return getTimestamp() + ": " + temp.substring(index);
			}
		}
		return getTimestamp();
	}
	
	private static String getTimestamp()
	{
		long elapsed = TimeUtils.timeSinceMillis(startTime);
		long elapsedHours = elapsed / (60 * 60 * 1000);
		long elapsedMinutes = (elapsed - elapsedHours * 60 * 60 * 1000) / (60 * 1000);
		long elapsedSeconds = (elapsed - elapsedHours * 60 * 60 * 1000 - elapsedMinutes * 60 * 1000) / 1000;
		long elapsedMillis = elapsed - elapsedHours * 60 * 60 * 1000 - elapsedMinutes * 60 * 1000 - elapsedSeconds * 1000;
		
		return String.format("%d:%02d:%02d:%03d", elapsedHours, elapsedMinutes, elapsedSeconds, elapsedMillis);
	}
}
