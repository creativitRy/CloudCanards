//VERTEX SHADER

/**
 * VertexShader.vert
 * Renders the object on the normal position
 *
 * @author Gahwon Lee
 * Period: 3
 * Date: 5/13/2017
 */

const float ALPHA_CONVERSION = 256.0/255.0;

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;

void main()
{
	v_color = a_color;
	v_color.a = v_color.a * ALPHA_CONVERSION;
	v_texCoords = a_texCoord0;
	gl_Position = u_projTrans * a_position;
}