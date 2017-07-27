package com.cloudcanards.console.commands;

import com.cloudcanards.console.AbstractCommand;
import com.cloudcanards.console.Console;

/**
 * ListCommand
 *
 * @author creativitRy
 */
public class ListCommand extends AbstractCommand
{
	private static final String SEPARATOR = ", ";
	
	@Override
	public void execute(String[] args) throws ArrayIndexOutOfBoundsException
	{
		throw new ArrayIndexOutOfBoundsException("This should print the help method");
	}
	
	@Override
	public String help(int start, String[] args)
	{
		if (start > 1)
		{
			return "Returns list of commands";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : Console.getCommands().keySet())
		{
			sb.append(SEPARATOR).append(s);
		}
		return sb.substring(SEPARATOR.length());
	}
}
