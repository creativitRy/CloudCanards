package com.cloudcanards.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayDeque;

/**
 * ResourceManager
 *
 * @author creativitRy
 */
public class ResourceManager
{
	private AssetManager assetManager;
	private ArrayDeque<AbstractTask> tasks;
	private ArrayDeque<AbstractTask> multithreadAddFixer;
	private boolean multithreadRemoveFixer;
	
	public ResourceManager()
	{
		InternalFileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver, false);
		addLoaders(resolver);
		tasks = new ArrayDeque<>();
		multithreadAddFixer = new ArrayDeque<>();
	}
	
	private void addLoaders(FileHandleResolver resolver)
	{
		//defaults
		assetManager.setLoader(BitmapFont.class, new BitmapFontLoader(resolver));
		assetManager.setLoader(Music.class, new MusicLoader(resolver));
		assetManager.setLoader(Pixmap.class, new PixmapLoader(resolver));
		assetManager.setLoader(Sound.class, new SoundLoader(resolver));
		assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
		assetManager.setLoader(Texture.class, new TextureLoader(resolver));
		assetManager.setLoader(Skin.class, new SkinLoader(resolver));
		assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(resolver));
		//		assetManager.setLoader(com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class,
		//			new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader(resolver));
		assetManager.setLoader(PolygonRegion.class, new PolygonRegionLoader(resolver));
		assetManager.setLoader(I18NBundle.class, new I18NBundleLoader(resolver));
		//		assetManager.setLoader(Model.class, ".g3dj", new G3dModelLoader(new JsonReader(), resolver));
		//		assetManager.setLoader(Model.class, ".g3db", new G3dModelLoader(new UBJsonReader(), resolver));
		//		assetManager.setLoader(Model.class, ".obj", new ObjLoader(resolver));
		assetManager.setLoader(ShaderProgram.class, new ShaderProgramLoader(resolver));
		
		//more
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	
	public void addTask(AbstractTask task)
	{
		multithreadAddFixer.add(task);
	}
	
	/**
	 * @return true if done loading everything
	 */
	public boolean update()
	{
		//add tasks to the queue
		while (!multithreadAddFixer.isEmpty())
		{
			if (tasks.isEmpty())
			{
				runTask(multithreadAddFixer.peek());
			}
			
			tasks.add(multithreadAddFixer.remove());
		}
		
		//done updating
		if (tasks.isEmpty())
			return true;
		
		//remove task and run next task
		if (multithreadRemoveFixer)
		{
			tasks.remove();
			multithreadRemoveFixer = false;
			
			if (!tasks.isEmpty())
			{
				runTask(tasks.peek());
			}
		}
		
		//set task to be removed and run postrun
		if (!tasks.isEmpty() && tasks.peek().isDone())
		{
			multithreadRemoveFixer = true;
		}
		
		//still updating
		return false;
	}
	
	private void runTask(AbstractTask task)
	{
		if (task.runInNewThread())
		{
			new Thread(task::run).start();
		}
		else
			task.run();
	}
	
	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public void dispose()
	{
		assetManager.dispose();
	}
}
