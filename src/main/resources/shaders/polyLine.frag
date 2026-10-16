#version 450 core

layout (location = 7) uniform vec4 startColor;
layout (location = 8) uniform vec4 endColor;

layout (location = 0) in float lineFraction;
layout (location = 0) out vec4 FragColor;

void main() {
    FragColor = mix(startColor, endColor, lineFraction);
}