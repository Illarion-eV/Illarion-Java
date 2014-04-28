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
uniform vec4 u_top;
uniform vec4 u_bottom;
uniform vec4 u_left;
uniform vec4 u_right;
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
	vec3 topColor = (u_top.rgb + u_topLeft.rgb + u_topRight.rgb + u_center.rgb) / 4.0;
	vec3 bottomColor = (u_bottom.rgb + u_bottomLeft.rgb + u_bottomRight.rgb + u_center.rgb) / 4.0;
	vec3 rightColor = (u_right.rgb + u_topRight.rgb + u_bottomRight.rgb + u_center.rgb) / 4.0;
	vec3 leftColor = (u_left.rgb + u_bottomLeft.rgb + u_topLeft.rgb + u_center.rgb) / 4.0;

	vec2 topCoords = vec2((u_bottomRightCoords.x - u_topLeftCoords.x) / 2.0, u_topLeftCoords.y);
	vec2 bottomCoords = vec2((u_bottomRightCoords.x - u_topLeftCoords.x) / 2.0, u_bottomRightCoords.y);
	vec2 rightCoords = vec2(u_bottomRightCoords.x, (u_bottomRightCoords.y - u_topLeftCoords.y) / 2.0);
	vec2 leftCoords = vec2(u_topLeftCoords.x, (u_bottomRightCoords.y - u_topLeftCoords.y) / 2.0);

	vec2 leftTopVector = topCoords - leftCoords;
	vec2 leftBottomVector = bottomCoords - leftCoords;

    // project v_texCoords on leftTopVector
	float weightRight = dot(v_texCoords - leftCoords, leftTopVector) / dot(leftTopVector, leftTopVector);
	// project v_texCoords on leftBottomVector
	float weightBottom = dot(v_texCoords - leftCoords, leftBottomVector) / dot(leftBottomVector, leftBottomVector);

	vec3 topInterpolation = weightRight * topColor + (1.0 - weightRight) * leftColor;
    vec3 bottomInterpolation = weightRight * rightColor + (1.0 - weightRight) * bottomColor;
    vec3 usedColor = weightBottom * bottomInterpolation + (1.0 - weightBottom) * topInterpolation;

	// apply the color to the fragment
	vec3 actualLight = usedColor * ambientFactor + v_color.rgb;
	gl_FragColor = vec4(color.rgb * actualLight, color.a);
}