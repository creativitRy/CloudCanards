package com.cloudcanards.console;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.console.commands.HelpCommand;
import com.cloudcanards.console.commands.ListCommand;
import com.cloudcanards.console.commands.RenderBox2DCommand;
import com.cloudcanards.console.commands.TpCommand;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.input.InputType;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.TreeMap;

/**
 * ConsoleUI
 *
 * @author creativitRy
 */
public class Console implements InputListener
{
	/**
	 * All registered commands
	 */
	private static void initCommands()
	{
		commands.put("help", HelpCommand.class);
		commands.put("list", ListCommand.class);
		commands.put("renderbox2d", RenderBox2DCommand.class);
		commands.put("tp", TpCommand.class);
	}
	
	private static final TreeMap<String, Class<? extends AbstractCommand>> commands = new TreeMap<>();
	
	public static TreeMap<String, Class<? extends AbstractCommand>> getCommands()
	{
		return commands;
	}
	
	public static Console getInstance()
	{
		return INSTANCE;
	}
	
	private static final Console INSTANCE = new Console();
	
	private static final int MAX_HISTORY_AMOUNT = 30;
	
	private TextField textField;
	private boolean inputEnabled;
	private Array<String> history;
	private int historyIndex;
	
	private ArrayDeque<Runnable> postRunnables;
	
	private Console()
	{
		initCommands();
		
		textField = new TextField("", CloudCanards.getInstance().getSkin(), "console");
		textField.setSize(GameScreen.SCREEN_WIDTH, 30);
		textField.setPosition(0, 0);
		textField.getStyle().background.setLeftWidth(10); //idk why this cannot be put in the skin json file
		
		textField.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener()
		{
			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if (keycode == Input.Keys.ESCAPE)
				{
					disableConsole();
					return true;
				}
				else if (keycode == Input.Keys.ENTER)
				{
					execute(textField.getText());
					disableConsole();
					postExecute();
					return true;
				}
				else if (keycode == Input.Keys.UP)
				{
					if (historyIndex <= 0)
					{
						historyIndex = 0;
						return true;
					}
					
					historyIndex--;
					textField.setText(history.get(historyIndex));
					textField.setCursorPosition(textField.getText().length());
					
					return true;
				}
				else if (keycode == Input.Keys.DOWN)
				{
					historyIndex++;
					
					if (historyIndex >= history.size)
					{
						historyIndex = history.size;
						textField.setText("");
						return true;
					}
					
					textField.setText(history.get(historyIndex));
					textField.setCursorPosition(textField.getText().length());
					
					return true;
				}
				return false;
			}
		});
		
		disableConsole();
		
		InputManager.register(this);
		
		history = new Array<>();
		postRunnables = new ArrayDeque<>();
	}
	
	/**
	 * Executes a command
	 *
	 * @param command
	 */
	public boolean execute(String command)
	{
		command = command.trim();
		addHistory(command);
		System.out.println(command);
		if (command.isEmpty())
		{
			return false;
		}
		if (command.charAt(0) == '/')
		{
			if (command.length() == 1)
			{
				return false;
			}
			command = command.substring(1);
		}
		
		String[] args = command.split("\\p{javaWhitespace}+");
		
		try
		{
			Class<? extends AbstractCommand> commandClass = commands.get(convert(args[0]));
			
			if (commandClass == null)
			{
				return false;
			}
			
			ClassReflection.newInstance(commandClass).tryExecute(args);
		}
		catch (ReflectionException | ClassCastException e)
		{
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds command to history and removes duplicates
	 */
	private void addHistory(String command)
	{
		history.removeValue(command, false);
		history.add(command);
		
		while (history.size > MAX_HISTORY_AMOUNT)
		{
			history.removeIndex(0);
		}
	}
	
	/**
	 * converts to lowercase
	 */
	public static String convert(String arg)
	{
		return arg.toLowerCase();
		//return arg.substring(0, 1).toUpperCase() + arg.substring(1) + "Command";
	}
	
	private void enableConsole()
	{
		textField.setText("");
		historyIndex = history.size;
		
		inputEnabled = false;
		textField.setVisible(true);
		Gdx.input.setInputProcessor(GameScreen.getInstance().getUiStage());
		GameScreen.getInstance().getUiStage().setKeyboardFocus(textField);
		InputManager.setEnabled(false);
	}
	
	private void disableConsole()
	{
		inputEnabled = true;
		textField.setVisible(false);
		GameScreen.getInstance().getUiStage().unfocus(textField);
		InputManager.setEnabled(true);
	}
	
	private void postExecute()
	{
		while (!postRunnables.isEmpty())
		{
			postRunnables.remove().run();
		}
	}
	
	ArrayDeque<Runnable> getPostRunnables()
	{
		return postRunnables;
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		if (action == InputAction.CONSOLE)
		{
			if (args[0] instanceof Boolean && !((Boolean) args[0]))
			{
				enableConsole();
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isEnabled()
	{
		return inputEnabled;
	}
	
	@Override
	public void enable()
	{
		inputEnabled = true;
	}
	
	@Override
	public void disable()
	{
		inputEnabled = false;
	}
	
	public TextField ui()
	{
		return textField;
	}
}
