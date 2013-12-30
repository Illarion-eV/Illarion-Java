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

uniform vec3 u_hsv_r;
uniform vec3 u_hsv_g;
uniform vec3 u_hsv_b;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

vec3 rgb2hsv(vec3 c);
vec3 hsv2rgb(vec3 c);

void main() {
	vec4 color = texture2D(u_texture, v_texCoords.st);
	vec3 hsv = rgb2hsv(color.rgb);
	vec3 newColor_r = hsv2rgb(vec3(hsv.x + u_hsv_r.x, hsv.yz * u_hsv_r.yz));
	vec3 newColor_g = hsv2rgb(vec3(hsv.x + u_hsv_g.x, hsv.yz * u_hsv_g.yz));
	vec3 newColor_b = hsv2rgb(vec3(hsv.x + u_hsv_b.x, hsv.yz * u_hsv_b.yz));

	vec3 resultColor = color.rgb;
	resultColor = vec3(newColor_r * color.r + resultColor * (1.0 - color.r));
	resultColor = vec3(newColor_g * color.g + resultColor * (1.0 - color.g));
	resultColor = vec3(newColor_b * color.b + resultColor * (1.0 - color.b));
	gl_FragColor = resultColor;
}