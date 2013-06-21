#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render
uniform sampler2D tex0;

void main() {
    vec4 fragmentColor = texture2D(tex0, gl_TexCoord[0].st);
	float gray = dot(fragmentColor.rgb, vec3(0.299, 0.587, 0.114));

	gl_FragColor = vec4(gray, gray, gray, fragmentColor.a);
}