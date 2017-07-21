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
	private Logger() {}
	
	private static long startTime;
	
	private static int level = Application.LOG_INFO;
	
	/**
	 * Gets the level
	 *
	 * @return level
	 * @see Application#LOG_INFO
	 * @see Application#LOG_DEBUG
	 * @see Application#LOG_ERROR
	 */
	public static int getLevel()
	{
		return level;
	}
	
	/**
	 * Sets the level of logging - how the logged messages will be graded
	 *
	 * @param level level
	 * @see Application#LOG_INFO
	 * @see Application#LOG_DEBUG
	 * @see Application#LOG_ERROR
	 */
	public static void setLevel(int level)
	{
		Logger.level = level;
	}
	
	/**
	 * Sets the logging level to {@link Application#LOG_INFO}
	 *
	 * @see #setLevel(int)
	 */
	public static void resetLevel()
	{
		setLevel(Application.LOG_INFO);
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
