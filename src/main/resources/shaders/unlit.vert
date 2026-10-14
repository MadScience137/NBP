#version 450 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 nNormal;
layout (location = 2) in vec2 aTexCoord;

uniform mat4 transform;
uniform mat4 projection;

out vec2 texCoord;
out vec3 normal;

void main() {
    gl_Position = projection * transform * vec4(aPosition, 1.0);
    normal = nNormal;
    texCoord = aTexCoord;
}
