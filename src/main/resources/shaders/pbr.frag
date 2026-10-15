#version 450 core

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

void main() {
    vec3 n = normalize(normal);
    if (!gl_FrontFacing) {
        n = -n;
    }
    float cosTheta = max(dot(n, lightDirection), 0.0);
    FragColor = vec4(albedo.xyz * (lightColor.xyz * cosTheta + ambientLightColor.xyz), albedo.w);
}
