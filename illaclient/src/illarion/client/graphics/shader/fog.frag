// the texture to render
uniform sampler2D tex0;

// the coordinates of the center
uniform vec2 center;

// the fog density
uniform float density;

const vec4 fogGray = vec4(0.9, 0.9, 0.9, 0.0);

void main() {
    // get the distance to the origin
    float distance = length((center - gl_TexCoord[0].xy) * vec2(1.0, 2.0));

	// get the unaltered color of the fragment
	vec4 color = texture2D(tex0, gl_TexCoord[0].st);

	//float fragmentFogDensity = clamp(density * distance / 300.0 * 0.0, 0.0, 0.95);
    float fragmentFogDensity = 0.5;

	// overlay the original color with the fog gray
	gl_FragColor = vec4(color.rgba * (1.0 - fragmentFogDensity) + fogGray.rgba * fragmentFogDensity);
	//gl_FragColor = vec4(color.rgba);
}