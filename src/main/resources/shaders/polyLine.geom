#version 450 core

layout(location = 0) uniform vec3 cameraPosition;
layout(location = 4) uniform float startThickness;
layout(location = 5) uniform float endThickness;
layout(location = 6) uniform vec2 viewportSize;

layout(lines) in;
layout(location = 0) in float lineFractions[];

layout(triangle_strip, max_vertices = 4) out;
layout(location = 0) out float lineFraction;

void main() {
    vec4 p1 = gl_in[0].gl_Position;
    vec4 p2 = gl_in[1].gl_Position;

    vec2 ndc1 = p1.xy / p1.w;
    vec2 ndc2 = p2.xy / p2.w;

    vec2 direction = (ndc2 - ndc1) * viewportSize;
    if (dot(direction, direction) < 1e-8) return;

    vec2 normal = normalize(vec2(-direction.y, direction.x));

    vec2 offset1 = normal * mix(startThickness, endThickness, lineFractions[0]) / viewportSize;
    vec2 offset2 = normal * mix(startThickness, endThickness, lineFractions[1]) / viewportSize;

    gl_Position = p1 + vec4(-offset1 * p1.w, 0.0, 0.0);
    lineFraction = lineFractions[0];
    EmitVertex();

    gl_Position = p2 + vec4(-offset2 * p2.w, 0.0, 0.0);
    lineFraction = lineFractions[1];
    EmitVertex();

    gl_Position = p1 + vec4(offset1 * p1.w, 0.0, 0.0);
    lineFraction = lineFractions[0];
    EmitVertex();

    gl_Position = p2 + vec4(offset2 * p2.w, 0.0, 0.0);
    lineFraction = lineFractions[1];
    EmitVertex();

    EndPrimitive();
}