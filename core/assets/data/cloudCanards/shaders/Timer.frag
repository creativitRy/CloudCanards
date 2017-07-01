//FRAGMENT SHADER

/**
 * Timer.frag
 * Renders the timer where the alpha channel defines how full the timer is
 *
 * @author Gahwon Lee
 * Period: 3
 * Date: 5/13/2017
 */

#ifdef GL_ES
	precision mediump float;
#endif

const float ALPHA_MAX = 0.5;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
/**
 * timer value where 0 means the timer is completely depleted
 * and 1 means the timer is completely full
 */
uniform float u_timerVal;

void main()
{
	vec4 color = texture2D(u_texture, v_texCoords).rgba;
	vec3 final = color.rgb;
	float alpha = sign(color.a) * ((sign(color.a - (1 - u_timerVal)) + 1) / 2)
	    * ALPHA_MAX;

	gl_FragColor = vec4(final, alpha);
}