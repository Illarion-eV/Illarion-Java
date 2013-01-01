#version 120

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

// its PI!
const float pi = 3.14159265;

float getIntensity() {
    float aniPos = mod(gl_TexCoord[0].s + gustAnimation * sign(-windDir) + gl_TexCoord[0].t * -windDir, 1.f);
	float gustEffect = sin(aniPos * pi) * gustStrength;
	return clamp(intensity + (intensity * gustEffect), 0.f, 1.f);
}

vec2 getRainCoord(in float offset) {
    vec2 scaledTexCoord = (gl_TexCoord[0].st) * texRainScale.xy;
    vec2 aniCoord = vec2(offset + (gl_TexCoord[0].t * windDir * -2.f), (-animation + offset) * texRainScale.y);
    vec2 result =  texRainOffset + mod(scaledTexCoord + aniCoord, texRainSize.xy);

    return result;
}

vec3 getRainColor(in vec3 color, in float value, in float levelIntensity) {
    float rainTexColor = 1.f - texture2D(texRain, getRainCoord(value)).r;
	float rainColor = clamp(rainTexColor * levelIntensity, 0.f, 1.f);
	
	return vec3(color.rgb * (1.f - rainColor) + rainDropColor.rgb * rainColor);
}

void main() {
    vec4 backColor = texture2D(texBack, gl_TexCoord[0].st);

    float intensity = getIntensity();

    vec3 resultColor = backColor.rgb;
	bool skipNext = true;
	for (int level = 0; level < 3; level += 1) {
		float stepSize = 1.f / pow(level + 1.f, 2.f);
		skipNext = true;
		float levelIntensity = clamp(0.3f + intensity * 3.f - sqrt(float(level)), 0.f, 1.f);
		for (float value = 0.f; value < 1.f; value += stepSize) {
			if (skipNext) {
				skipNext = false;
			} else {
				resultColor = getRainColor(resultColor, value, levelIntensity);
				skipNext = true;
			}
		}
	}

    gl_FragColor = vec4(resultColor, 1.f);
}