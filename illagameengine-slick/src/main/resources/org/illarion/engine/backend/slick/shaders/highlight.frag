#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render
uniform sampler2D tex0;

// the highlight color
uniform vec4 highlight;

void main() {
	// get the unaltered color of the fragment
	vec4 color = texture2D(tex0, gl_TexCoord[0].st) * gl_Color;

	// overlay the original color with the fog gray
	gl_FragColor = vec4(color.rgb * (1.0 - highlight.a) + highlight.rgb * highlight.a, color.a);
}