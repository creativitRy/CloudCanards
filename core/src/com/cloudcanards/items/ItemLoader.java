package com.cloudcanards.items;

import com.cloudcanards.assets.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * ItemLoader
 * <p>
 * An item is a json file in the folder Assets.DIR + Assets.ITEM_DIR or its subfolders.
 * It is formatted like this:
 * <p>
 * item class name that extends abstract item and is located in the same package as item loader
 * <p>
 * name(String)
 * <p>
 * description(String)
 * <p>
 * category(String)
 * <p>
 * value(int)
 * <p>
 * Other stuff depending on the item class
 *
 * @author creativitRy
 */
public class ItemLoader
{
	//todo: change this when changing the todo below
	private static final Class<AbstractItem> DEFAULT_ITEM_CLASS = AbstractItem.class;
	private static JsonReader reader;
	
	public static AbstractItem load(String name)
	{
		return load(Gdx.files.internal(Assets.DIR + Assets.ITEM_DIR + name));
	}
	
	public static AbstractItem load(FileHandle itemFile)
	{
		reader = new JsonReader();
		
		if (!itemFile.exists())
			throw new IllegalArgumentException("No such item with the name " + itemFile.toString() + " exists");
		
		JsonValue value = reader.parse(itemFile);
		
		AbstractItem item = constructItem(value.asString());
		
		item.setId(itemFile.name());
		item.setName((value = value.next()).asString());
		item.setDescription((value = value.next()).asString());
		item.setCategory(toCategory((value = value.next()).asString()));
		item.setValue((value = value.next()).asInt());
		
		if (item.getClass() != DEFAULT_ITEM_CLASS)
		{
			//use the variable value
			//todo: what if the item is like armor or something
		}
		
		reader = null;
		return item;
	}
	
	private static AbstractItem constructItem(String type)
	{
		try
		{
			@SuppressWarnings("unchecked")
			Class<AbstractItem> clazz = ClassReflection.forName(AbstractItem.class.getPackage().getName() + "." + type);
			
			return ClassReflection.newInstance(clazz);
		}
		catch (ReflectionException e)
		{
			//e.printStackTrace();
			throw new SerializationException("Error while parsing what type of item this is: " + type, e.getCause());
		}
		
	}
	
	private static ItemCategory toCategory(String s)
	{
		return ItemCategory.valueOf(s);
	}
}
