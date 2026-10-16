#version 450 core

layout (location = 5) uniform vec4 color;

layout (location = 0) out vec4 FragColor;

void main() {
    FragColor = color;
}