#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 fragmentColor = texture2D(u_texture, v_texCoords.st);
	float gray = dot(fragmentColor.rgb, vec3(0.299, 0.587, 0.114));

	gl_FragColor = vec4(gray, gray, gray, fragmentColor.a);
}