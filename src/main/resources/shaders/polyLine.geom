#version 450 core

layout(location = 0) uniform vec3 cameraPosition;
layout(location = 3) uniform float thickness;
layout(location = 4) uniform vec2 viewportSize;

layout(lines) in;
layout(location = 0) in float cameraDistance[];

layout(triangle_strip, max_vertices = 4) out;

void main() {
    vec4 p1 = gl_in[0].gl_Position;
    vec4 p2 = gl_in[1].gl_Position;

    vec2 ndc1 = p1.xy / p1.w;
    vec2 ndc2 = p2.xy / p2.w;

    vec2 direction = (ndc2 - ndc1) * viewportSize;
    if (dot(direction, direction) < 1e-8) return;

    vec2 normal = normalize(vec2(-direction.y, direction.x));

    vec2 offset = normal * thickness / viewportSize;

    gl_Position = p1 + vec4(-offset * p1.w, 0.0, 0.0);
    EmitVertex();

    gl_Position = p2 + vec4(-offset * p2.w, 0.0, 0.0);
    EmitVertex();

    gl_Position = p1 + vec4(offset * p1.w, 0.0, 0.0);
    EmitVertex();

    gl_Position = p2 + vec4(offset * p2.w, 0.0, 0.0);
    EmitVertex();

    EndPrimitive();
}