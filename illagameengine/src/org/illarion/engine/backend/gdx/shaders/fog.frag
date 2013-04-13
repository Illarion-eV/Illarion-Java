#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec2 u_center;
uniform float u_density;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

const vec4 c_fogGray = vec4(0.9, 0.9, 0.9, 1.0);

void main() {
    float distance = abs(length((u_center - v_texCoords.xy) * vec2(1.0, 1.0)));

	vec4 color = texture2D(u_texture, v_texCoords.st);
	float fragmentFogDensity = clamp(u_density * distance, 0.0, 0.98);

	gl_FragColor = vec4(color.rgba * (1.0 - fragmentFogDensity) + c_fogGray.rgba * fragmentFogDensity);
}