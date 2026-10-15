#version 450 core

layout(location = 0) uniform vec3 cameraPosition;
layout(location = 1) uniform mat4 viewProjection;
layout(location = 2) uniform mat4 transform;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aTexCoord;

layout(location = 0) out vec3 worldPosition;
layout(location = 1) out vec3 viewRay;
layout(location = 2) out vec3 normal;
layout(location = 3) out vec2 texCoord;

void main() {
    worldPosition = (transform * vec4(aPosition, 1)).xyz;
    viewRay = normalize(cameraPosition - worldPosition);
    normal = transpose(inverse(mat3(transform))) * aNormal;
    texCoord = aTexCoord;

    gl_Position = viewProjection * transform * vec4(aPosition, 1);
}
