#version 450 core

const float PI = 3.14159265359;

layout(location = 3) uniform vec4 ambientLightColor;
layout(location = 4) uniform vec4 lightColor;
layout(location = 5) uniform vec3 lightDirection;
layout(location = 6) uniform vec4 albedo;
layout(location = 7) uniform float roughness;
layout(location = 8) uniform float metallic;
layout(location = 9) uniform float ao;

layout(location = 0) in vec3 worldPosition;
layout(location = 1) in vec3 viewRay;
layout(location = 2) in vec3 normal;
layout(location = 3) in vec2 texCoord;

out vec4 FragColor;

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;

    float num = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float geometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;

    float num = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = geometrySchlickGGX(NdotV, roughness);
    float ggx1  = geometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

void main() {
    vec3 N = normalize(normal);
    if (!gl_FrontFacing) {
        N = -N;
    }

    vec3 V = normalize(viewRay);
    vec3 L = normalize(lightDirection);
    vec3 halfVector = normalize(V + L);

    float cosTheta = max(dot(N, L), 0.0);
    vec3 lo = vec3(0.0);

    vec3 f0 = vec3(0.04);
    f0 = mix(f0, albedo.xyz, metallic);
    vec3 f  = fresnelSchlick(max(dot(halfVector, V), 0.0), f0);

    float ndf = distributionGGX(N, halfVector, roughness);
    float g = geometrySmith(N, V, L, roughness);

    vec3 numerator = ndf * g * f;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0)  + 0.0001;
    vec3 specular = numerator / denominator;

    vec3 kS = f;
    vec3 kD = vec3(1.0) - kS;

    kD *= 1.0 - metallic;

    float NdotL = max(dot(N, L), 0.0);
    lo += (kD * albedo.xyz / PI + specular) * lightColor.xyz * NdotL;

    vec3 ambient = ambientLightColor.xyz * albedo.xyz * ao;
    vec3 color   = ambient + lo;

    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));

    FragColor = vec4(color, albedo.w);
}
