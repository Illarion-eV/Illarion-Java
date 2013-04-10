#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render
uniform sampler2D u_texture;

// the coordinates of the center
uniform vec2 center;

// the fog density
uniform float density;

// the color of the fog
const vec4 fogGray = vec4(0.9, 0.9, 0.9, 1.0);

void main() {
    // get the distance to the origin
    float distance = abs(length((center - gl_TexCoord[0].xy) * vec2(1.0, 1.0)));

	// get the unaltered color of the fragment
	vec4 color = texture2D(u_texture, gl_TexCoord[0].st);

	float fragmentFogDensity = clamp(density * distance, 0.0, 0.98);

	// overlay the original color with the fog gray
	gl_FragColor = vec4(color.rgba * (1.0 - fragmentFogDensity) + fogGray.rgba * fragmentFogDensity);
}