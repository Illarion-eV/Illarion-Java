// the texture to render
uniform sampler2D tex0;

// the coordinates of the center
uniform vec2 center;

// the radius distance
uniform float radius;

// transparent color
const vec4 transparentColor = vec4(0.0, 0.0, 0.0, 0.0);

void main() {
    // get the distance to the origin
    float distance = distance(center, gl_TexCoord[0].xy);

    if (distance > radius) {
        gl_FragColor = transparentColor;
    } else {
        gl_FragColor = texture2D(tex0, gl_TexCoord[0].st);
    }
}