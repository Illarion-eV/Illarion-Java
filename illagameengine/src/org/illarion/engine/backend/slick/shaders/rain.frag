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

vec2 getRainCoord() {
    vec2 scaledTexCoord = gl_TexCoord[0].st * texRainScale.xy;
    vec2 aniCoord = vec2(gl_TexCoord[0].t * windDir * -2.0, -animation * texRainScale.y);
    vec2 result = texRainOffset + mod(scaledTexCoord + aniCoord + mapOffset, texRainSize.xy);

    return result;
}

void main() {
    vec4 backColor = texture2D(texBack, gl_TexCoord[0].st);

    float intensity = getIntensity();

    vec3 resultColor = backColor.rgb;

    float levelIntensityR = clamp(0.3 + intensity * 2.0, 0.0, 1.0);
    float levelIntensityG = clamp(0.3 + intensity * 2.0 - 1.0, 0.0, 1.0);
    float levelIntensityB = clamp(0.3 + intensity * 2.0 - 1.4, 0.0, 1.0);

    vec3 levelIntensity = vec3(levelIntensityR, levelIntensityG, levelIntensityB);
    float rainTexColor = dot(texture2D(texRain, getRainCoord()).rgb, levelIntensity);
	rainTexColor = clamp(rainTexColor - gl_TexCoord[0].t * 0.2, 0.0, 1.0);

    resultColor.rgb = resultColor.rgb * (1.0 - rainTexColor) + rainDropColor.rgb * rainTexColor;

    gl_FragColor = vec4(resultColor, 1.0);
}