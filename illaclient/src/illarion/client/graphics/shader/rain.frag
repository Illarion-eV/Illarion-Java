#version 110

#ifdef GL_ES
precision mediump float;
#endif

// the texture to render (this is the game screen)
uniform sampler2D texBack;

// the texture of the rain
uniform sampler2D texRain;

// the offset vector of the rain texture
uniform vec2 texRainOffset;

// the size vector of the rain texture
uniform vec2 texRainSize;

// the scaling vector of the rain texture
uniform vec2 texRainScale;

// the animation frame that is supposed to be shown
uniform float animation;

// the animation speed of the gust
uniform float gustAnimation;

// the color of the rain
const vec4 rainDropColor = vec4(0.6, 0.6, 1.0, 1.0);

// the intensity level of the rain
uniform float intensity;

uniform float windDir;

uniform float gustStrength;

uniform vec2 mapOffset;

// its PI!
const float pi = 3.14159265;

float getIntensity() {
    float aniPos = mod(gl_TexCoord[0].s + gustAnimation * sign(-windDir) + gl_TexCoord[0].t * -windDir, 1.0);
	float gustEffect = sin(aniPos * pi) * gustStrength;
	return clamp(intensity + (intensity * gustEffect), 0.0, 1.0);
}

vec2 getRainCoord(in float offset) {
    vec2 scaledTexCoord = gl_TexCoord[0].st * texRainScale.xy;
    vec2 aniCoord = vec2(offset + (gl_TexCoord[0].t * windDir * -2.0), (-animation + offset) * texRainScale.y);
    vec2 result =  texRainOffset + mod(scaledTexCoord + aniCoord + mapOffset, texRainSize.xy);

    return result;
}

void getRainColor(inout vec3 color, in float value, in float levelIntensity) {
    float rainTexColor = 1.0 - texture2D(texRain, getRainCoord(value)).r;
	float rainColor = clamp(rainTexColor * levelIntensity, 0.0, 1.0);
	rainColor = clamp(rainColor - gl_TexCoord[0].t * 0.2, 0.0, 1.0);

	color.rgb *= (1.0 - rainColor);
	color.rgb += rainDropColor.rgb * rainColor;
}

void main() {
    vec4 backColor = texture2D(texBack, gl_TexCoord[0].st);

    float intensity = getIntensity();

    vec3 resultColor = backColor.rgb;

    float levelIntensity = clamp(0.3 + intensity * 2.0 - sqrt(0.0), 0.0, 1.0);
    getRainColor(resultColor, 0.0, levelIntensity);

    levelIntensity = clamp(0.3 + intensity * 2.0 - sqrt(1.0), 0.0, 1.0);
    getRainColor(resultColor, 0.3, levelIntensity);
    getRainColor(resultColor, 0.6, levelIntensity);

    levelIntensity = clamp(0.3 + intensity * 2.0 - sqrt(2.0), 0.0, 1.0);
    getRainColor(resultColor, 0.15, levelIntensity);
    getRainColor(resultColor, 0.45, levelIntensity);
    getRainColor(resultColor, 0.65, levelIntensity);
    getRainColor(resultColor, 0.85, levelIntensity);

    gl_FragColor = vec4(resultColor, 1.0);
}