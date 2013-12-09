#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec2 u_center;
uniform float u_radius;
uniform float u_markerSize;

const LOWP vec4 transparentColor = vec4(0.0);
const LOWP vec4 markerColor = vec4(1.0, 0.0, 0.0, 0.8);
const LOWP vec4 backgroundColor = vec4(0.0, 0.0, 0.0, 1.0);

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

void main() {
    float distance = distance(u_center, v_texCoords.xy);

    if (distance < u_markerSize) {
        gl_FragColor = markerColor;
    } else if (distance > u_radius) {
        gl_FragColor = transparentColor;
    } else if (v_texCoords.s < 0.0 || v_texCoords.s >= 1.0) {
        gl_FragColor = backgroundColor;
    } else if (v_texCoords.t < 0.0 || v_texCoords.t >= 1.0) {
        gl_FragColor = backgroundColor;
    } else {
        gl_FragColor = texture2D(u_texture, v_texCoords.st);
    }
}