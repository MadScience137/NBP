#version 450 core

layout (location = 0) in vec3 normal;
layout (location = 1) in vec2 texCoord;

layout (location = 2) uniform vec4 color;

layout (location = 0) out vec4 FragColor;

void main() {
    FragColor = color;
}
