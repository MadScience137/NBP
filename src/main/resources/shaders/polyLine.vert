#version 450 core

layout(location = 0) uniform vec3 cameraPosition;
layout(location = 1) uniform mat4 viewProjection;
layout(location = 2) uniform mat4 transform;
layout(location = 3) uniform float length;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in float aDistance;

layout(location = 0) out float lineFraction;

void main() {
    lineFraction = aDistance / length;
    gl_Position = viewProjection * transform * vec4(aPosition, 1);
}