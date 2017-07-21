package com.cloudcanards.input;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * InputIO
 *
 * @author creativitRy
 */
public class InputIO
{
	public static final FileHandle FILE = Gdx.files.local("inputs.json");
	public static final FileHandle DEFAULT_FILE = Gdx.files.internal(Assets.DIR + Assets.DEFAULT_INPUT_FILE_NAME);
	
	private static Json json;
	
	static
	{
		json = new Json();
		
		json.setSerializer(InputManager.class, new InputManagerSerializer());
		json.setSerializer(ObjectMap.class, new ObjectMapSerializer());
	}
	
	/**
	 * Loads default only if custom preferences don't exist
	 *
	 * @return
	 */
	public static InputManager load()
	{
		return load(getFile());
	}
	
	public static InputManager load(FileHandle file)
	{
		Logger.log("Inputs loaded");
		return json.fromJson(InputManager.class, file);
	}
	
	//todo - call this
	public static void save(InputManager inputManager)
	{
		Logger.log("Inputs saved");
		json.toJson(inputManager, FILE);
	}
	
	/**
	 * Gets the default file if the regular one doesn't exist
	 *
	 * @return
	 */
	private static FileHandle getFile()
	{
		FileHandle file = FILE;
		if (!file.exists())
			file = DEFAULT_FILE;
		return file;
	}
	
	private static class InputManagerSerializer implements Json.Serializer<InputManager>
	{
		@Override
		public void write(Json json, InputManager object, Class knownType)
		{
			json.writeObjectStart();
			json.writeValue("0", object.getKeyMap());
			json.writeValue("1", object.getMouseButtonMap());
			json.writeValue("2", object.getMouseMoveMap());
			json.writeValue("3", object.getMouseScrollMap());
			json.writeValue("4", object.getControllerButtonMap());
			json.writeValue("5", object.getConstrollerAxisMap());
			json.writeValue("6", object.getControllerPOVMap());
			json.writeValue("7", object.getControllerTriggerMap());
			json.writeObjectEnd();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public InputManager read(Json json, JsonValue jsonData, Class type)
		{
			InputManager manager = new InputManager();
			manager.setKeyMap(json.readValue(ObjectMap.class, jsonData.get(0)));
			manager.setMouseButtonMap(json.readValue(ObjectMap.class, jsonData.get(1)));
			manager.setMouseMoveMap(json.readValue(ObjectMap.class, jsonData.get(2)));
			manager.setMouseScrollMap(json.readValue(ObjectMap.class, jsonData.get(3)));
			manager.setControllerButtonMap(json.readValue(ObjectMap.class, jsonData.get(4)));
			manager.setConstrollerAxisMap(json.readValue(ObjectMap.class, jsonData.get(5)));
			manager.setControllerPOVMap(json.readValue(ObjectMap.class, jsonData.get(6)));
			manager.setControllerTriggerMap(json.readValue(ObjectMap.class, jsonData.get(7)));
			return manager;
		}
	}
	
	private static class ObjectMapSerializer implements Json.Serializer<ObjectMap>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void write(Json json, ObjectMap object, Class knownType)
		{
			json.writeObjectStart();
			for (ObjectMap.Entry<Integer, InputAction> entry : (ObjectMap<Integer, InputAction>) object)
			{
				json.writeValue(entry.key.toString(), entry.value.name());
			}
			json.writeObjectEnd();
		}
		
		@Override
		public ObjectMap read(Json json, JsonValue jsonData, Class type)
		{
			ObjectMap<Integer, InputAction> map = new ObjectMap<>();
			for (JsonValue value : jsonData)
			{
				map.put(Integer.parseInt(value.name()), InputAction.valueOf(value.asString()));
			}
			return map;
		}
	}
}