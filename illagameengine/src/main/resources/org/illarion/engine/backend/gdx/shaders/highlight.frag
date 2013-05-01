#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec4 u_colorHighlight;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

void main() {
	// get the unaltered color of the fragment
	vec4 color = texture2D(u_texture, v_texCoords.st) * v_color;

	// overlay the original color with the fog gray
	gl_FragColor = vec4(color.rgb * (1.0 - u_colorHighlight.a) + u_colorHighlight.rgb * u_colorHighlight.a, color.a);
}