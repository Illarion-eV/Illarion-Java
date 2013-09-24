/*
 * This shader is used to alter the HSV value of any pixel processed by it in the same way.
 */

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec3 u_hsv;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

vec3 rgb2hsv(vec3 c);
vec3 hsv2rgb(vec3 c);

void main() {
	vec4 color = texture2D(u_texture, v_texCoords.st);
	vec3 hsv = rgb2hsv(color.rgb);
	vec3 newColor = hsv2rgb(vec3(hsv.x + u_hsv.x, hsv.yz * u_hsv.yz));
	gl_FragColor = newColor;
}