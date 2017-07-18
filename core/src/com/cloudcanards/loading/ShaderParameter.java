package com.cloudcanards.loading;

import com.cloudcanards.assets.Assets;

import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;

/**
 * Class with helpful constructors
 * <p>
 * All file names are appended with {@link Assets#DIR} + {@link Assets#SHADER_DIR}
 *
 * @author creativitRy
 */
public class ShaderParameter extends ShaderProgramLoader.ShaderProgramParameter
{
	/**
	 * All file names are appended with {@link Assets#DIR} + {@link Assets#SHADER_DIR}
	 * <p>
	 * Vertex file = VertexShader.vert {@link Assets#VERTEX_SHADER}
	 * <p>
	 * Fragment File = {@code null}
	 */
	public ShaderParameter()
	{
		this.vertexFile = Assets.DIR + Assets.SHADER_DIR + vertexFile;
	}
	
	/**
	 * All file names are appended with {@link Assets#DIR} + {@link Assets#SHADER_DIR}
	 * <p>
	 * Vertex file = VertexShader.vert {@link Assets#VERTEX_SHADER}
	 *
	 * @param fragmentFile File name to be used for the fragment program instead of the default determined by the file
	 *                     name used to submit this asset to AssetManager.
	 */
	public ShaderParameter(String fragmentFile)
	{
		this(Assets.VERTEX_SHADER, fragmentFile);
	}
	
	/**
	 * All file names are appended with {@link Assets#DIR} + {@link Assets#SHADER_DIR}
	 *
	 * @param vertexFile   File name to be used for the vertex program instead of the default determined by the file
	 *                     name used to submit this asset to AssetManager.
	 * @param fragmentFile File name to be used for the fragment program instead of the default determined by the file
	 *                     name used to submit this asset to AssetManager.
	 */
	public ShaderParameter(String vertexFile, String fragmentFile)
	{
		this.vertexFile = Assets.DIR + Assets.SHADER_DIR + vertexFile;
		this.fragmentFile = Assets.DIR + Assets.SHADER_DIR + fragmentFile;
	}
}
