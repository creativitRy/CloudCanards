package com.cloudcanards.console;

import com.cloudcanards.util.Logger;

/**
 * AbstractCommand
 *
 * @author creativitRy
 */
public abstract class AbstractCommand
{
	/**
	 * Executes a command. If an ArrayIndexOutOfBoundsException occurs, the help message will be printed
	 *
	 * @param args arguments where [0] is this command name
	 */
	public abstract void execute(String[] args) throws ArrayIndexOutOfBoundsException;
	
	final void tryExecute(String[] args)
	{
		try
		{
			execute(args);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			print(help(1, args));
		}
	}
	
	/**
	 * Gets the help message
	 *
	 * @param start index of first argument
	 * @param args  arguments where [start - 2] is help and [start - 1] is this command name
	 * @return help message or ("" || null) for no help
	 */
	public String help(int start, String[] args)
	{
		return "This command did not overwrite the help method lol";
	}
	
	/**
	 * Gets the help message. Index of first argument is 1
	 *
	 * @param args arguments where [0] is this command name
	 * @return help message or ("" || null) for no help
	 */
	public final String help(String[] args)
	{
		return help(1, args);
	}
	
	/**
	 * Prints out a message
	 *
	 * @param x message to print
	 */
	protected final void print(String x)
	{
		Logger.log(x);
	}
	
	/**
	 * Run this after setting every input stuff back to normal
	 */
	protected final void addPostExecutable(Runnable runnable)
	{
		Console.getInstance().getPostRunnables().add(runnable);
	}
}
