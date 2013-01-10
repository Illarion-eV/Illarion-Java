#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render
uniform sampler2D tex0;

// the highlight color
const vec3 highlight = vec3(1.0);

// the amount of color replaced with the highlight
uniform float highlightShare;

void main() {
	// get the unaltered color of the fragment
	vec4 color = texture2D(tex0, gl_TexCoord[0].st) * gl_Color;

	// overlay the original color with the fog gray
	gl_FragColor = vec4(color.rgb * (1.0 - highlightShare) + highlight.rgb * highlightShare, color.a);
}