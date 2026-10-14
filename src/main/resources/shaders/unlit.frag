#version 450 core

in vec3 normal;
in vec2 texCooord;

out vec4 FragColor;

uniform vec4 color;

void main() {
    FragColor = color;
}
