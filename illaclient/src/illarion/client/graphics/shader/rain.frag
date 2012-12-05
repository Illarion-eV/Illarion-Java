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
const float pi = 2.0 * asin(1.0);

float getIntensity() {
	float gustEffect = (sin(mod(gl_TexCoord[0].s + gustAnimation * sign(-windDir) + gl_TexCoord[0].t *
	-windDir, 1.f) * pi)) * min(gustStrength, intensity);
	return max(min(intensity + gustEffect, 1.f), 0.f);
}

vec2 getRainCoord(float offset) {
    vec2 scaledTexCoord = (gl_TexCoord[0].st) * texRainScale.xy;
    vec2 result =  texRainOffset + mod(scaledTexCoord + vec2(offset + (gl_TexCoord[0].t * windDir * -2.f),
    (-animation + offset) * texRainScale.y), texRainSize.xy);

    return result;
}

vec3 getRainColor(vec3 color, float value, int level) {
	float rainColor = min(texture2D(texRain, getRainCoord(value)).r + max(min(getIntensity() * 3 - (sqrt(float(level))), 1), 0), 1.f);
	vec3 resultColor = vec3(color.rgb * rainColor + rainDropColor.rgb * (1.f - rainColor));
	
	return resultColor;
}

void main() {
    vec4 backColor = texture2D(texBack, gl_TexCoord[0].st);

    vec3 resultColor = backColor.rgb;
	bool skipNext = true;
	for (int level = 0; level < 4; level += 1) {
		float stepSize = 1.f / pow(level + 1.f, 2.f);
		skipNext = true;
		for (float value = 0.f; value < 1.f; value += stepSize) {
			if (skipNext) {
				skipNext = false;
			} else {
				resultColor = getRainColor(resultColor, value, level);
				skipNext = true;
			}
		}
	}

	if (intensity > 0.5f) {
	    resultColor = vec3(resultColor.rgb * (intensity - 0.5f) / 2.f + rainDropColor.rgb * (1.f - ((intensity - 0.5f) / 2.f)));
	}

    gl_FragColor = vec4(resultColor, 1.f);
}