package com.cloudcanards.console.commands;

import com.cloudcanards.console.AbstractCommand;
import com.cloudcanards.console.Console;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * HelpCommand
 *
 * @author creativitRy
 */
public class HelpCommand extends AbstractCommand
{
	@Override
	public void execute(String[] args) throws ArrayIndexOutOfBoundsException
	{
		String print = getHelpMessage(args);
		if (print == null || print.isEmpty())
		{
			print = "Unable to get help for this command";
		}
		
		print(print);
	}
	
	private String getHelpMessage(String[] args)
	{
		try
		{
			Class<? extends AbstractCommand> commandClass = Console.getCommands().get(convert(args[1]));
			
			if (commandClass == null)
			{
				return "Unknown command " + Console.convert(args[1]);
			}
			
			return ClassReflection.newInstance(commandClass).help(2, args);
		}
		catch (ReflectionException | ClassCastException e)
		{
			//e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String help(int start, String[] args)
	{
		return "Usage: help CommandName";
	}
	
	/**
	 * Capitalizes the first letter of the given string and adds Command at the end
	 */
	private String convert(String arg)
	{
		return arg.substring(0, 1).toUpperCase() + arg.substring(1) + "Command";
	}
}
