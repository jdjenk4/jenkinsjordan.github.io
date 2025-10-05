#version 440 core

// ============================
// Vertex Attributes
// ============================
layout (location = 0) in vec3 inVertexPosition;
layout (location = 1) in vec3 inVertexNormal;
layout (location = 2) in vec2 inTextureCoordinate;

// ============================
// Outputs to Fragment Shader
// ============================
out vec3 fragmentPosition;
out vec3 fragmentVertexNormal;
out vec2 fragmentTextureCoordinate;

// ============================
// Uniforms (from C++)
// ============================
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

// ============================
// Main Shader Execution
// ============================
void main()
{
    // Transform the vertex position into world space
    fragmentPosition = vec3(model * vec4(inVertexPosition, 1.0));

    // Correctly transform normals (handles non-uniform scaling)
    fragmentVertexNormal = mat3(transpose(inverse(model))) * inVertexNormal;

    // Pass through texture coordinates
    fragmentTextureCoordinate = inTextureCoordinate;

    // Calculate final position of vertex in clip space
    gl_Position = projection * view * vec4(fragmentPosition, 1.0);
}
