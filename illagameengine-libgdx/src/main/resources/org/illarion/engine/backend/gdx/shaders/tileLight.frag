#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec4 u_topLeft;
uniform vec4 u_topRight;
uniform vec4 u_bottomLeft;
uniform vec4 u_bottomRight;
uniform vec4 u_center;

uniform vec2 u_topLeftCoords;
uniform vec2 u_bottomRightCoords;

varying LOWP vec4 v_color;   // color of the vertex -> ambient light
varying vec2 v_texCoords;    // coordinates on the texture

void main() {
	// the color of the texture
	vec4 color = texture2D(u_texture, v_texCoords.st);

	// the factor of the ambient light
	float ambientFactor = dot(v_color.rgb, vec3(1.0 / 3.0));

	// used light color for this fragment
	vec3 usedColor = u_center.rgb;

	// apply the color to the fragment
	vec3 actualLight = usedColor * ambientFactor + v_color.rgb;
	gl_FragColor = vec4(color.rgb * actualLight, color.a);
}