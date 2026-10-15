#version 450 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 nNormal;
layout (location = 2) in vec2 aTexCoord;

layout (location = 0) uniform mat4 transform;
layout (location = 1) uniform mat4 projection;

layout (location = 1) out vec2 texCoord;
layout (location = 0) out vec3 normal;

void main() {
    gl_Position = projection * transform * vec4(aPosition, 1.0);
    normal = nNormal;
    texCoord = aTexCoord;
}
