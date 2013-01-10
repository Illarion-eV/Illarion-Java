#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render
uniform sampler2D tex0;

// the coordinates of the center
uniform vec2 center;

// the radius distance
uniform float radius;

// center marker size
uniform float markerSize;

// transparent color
const vec4 transparentColor = vec4(0.0);

// color of the center marker
const vec4 markerColor = vec4(1.0, 0.0, 0.0, 0.8);

// the color of the default background
const vec4 backgroundColor = vec4(0.0, 0.0, 0.0, 1.0);

void main() {
    // get the distance to the origin
    float distance = distance(center, gl_TexCoord[0].xy);

    if (distance < markerSize) {
        gl_FragColor = markerColor;
    } else if (distance > radius) {
        gl_FragColor = transparentColor;
    } else if (gl_TexCoord[0].s < 0.0 || gl_TexCoord[0].s >= 1.0) {
        gl_FragColor = backgroundColor;
    } else if (gl_TexCoord[0].t < 0.0 || gl_TexCoord[0].t >= 1.0) {
        gl_FragColor = backgroundColor;
    } else {
        gl_FragColor = texture2D(tex0, gl_TexCoord[0].st);
    }
}