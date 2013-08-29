/*
 * This is a partial shader that is used to supply some functions to change
 * the color of textures. This shader is not usable as stand alone shader.
 * it has to be compiled and used along with additional shaders.
 */

vec4 rgbaChangeColor(const vec4 rgbaSource, const vec3 hueSaturationValue) {
    const vec4  kRGBToY = vec4 (0.299, 0.587, 0.114, 0.0);
    const vec4  kRGBToI = vec4 (0.596, -0.275, -0.321, 0.0);
    const vec4  kRGBToQ = vec4 (0.212, -0.523, 0.311, 0.0);

    const vec4  kYIQToR = vec4 (1.0, 0.956, 0.621, 0.0);
    const vec4  kYIQToG = vec4 (1.0, -0.272, -0.647, 0.0);
    const vec4  kYIQToB = vec4 (1.0, -1.107, 1.704, 0.0);

    // Convert to YIQ
    float Y = dot (rgbaSource, kRGBToYPrime);
    float I = dot (rgbaSource, kRGBToI);
    float Q = dot (rgbaSource, kRGBToQ);

    // Calculate the hue and chroma
    float hue    = atan(Q, I);
    float chroma = sqrt(I * I + Q * Q);

    hue    += hueSaturationValue.x;
    chroma += hueSaturationValue.y;
    Y      += hueSaturationValue.z;

    // Convert back to YIQ
    Q = chroma * sin(hue);
    I = chroma * cos(hue);

    // Convert back to RGB
    vec4 yIQ = vec4 (Y, I, Q, 0.0);
    return vec4(dot(yIQ, kYIQToR), dot(yIQ, kYIQToG), dot(yIQ, kYIQToB), rgbaSource.a);
}