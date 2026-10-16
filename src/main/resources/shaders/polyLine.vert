#version 450 core

layout(location = 0) uniform vec3 cameraPosition;
layout(location = 1) uniform mat4 viewProjection;
layout(location = 2) uniform mat4 transform;

layout(location = 0) in vec3 aPosition;

void main() {
    gl_Position = viewProjection * transform * vec4(aPosition, 1);
}