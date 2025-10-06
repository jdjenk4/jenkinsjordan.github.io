#pragma once
#include <string>
#include <glm/glm.hpp>
#include <gl/glew.h>

class ShaderManager
{
public:
    ShaderManager();
    ~ShaderManager();

    bool LoadShaders(const char* vertexPath, const char* fragmentPath);
    void use();

    // Uniform setters
    void setBoolValue(const std::string& name, bool value) const;
    void setIntValue(const std::string& name, int value) const;
    void setFloatValue(const std::string& name, float value) const;
    void setVec2Value(const std::string& name, const glm::vec2& value) const;
    void setVec3Value(const std::string& name, const glm::vec3& value) const;
    void setVec3Value(const std::string& name, float x, float y, float z) const;
    void setVec4Value(const std::string& name, const glm::vec4& value) const;
    void setMat4Value(const std::string& name, const glm::mat4& mat) const;
    void setSampler2DValue(const std::string& name, int slot) const;
    void setSamplerCubeValue(const std::string& name, int slot) const;

private:
    GLuint m_ID;  // Shader program ID

    std::string readFile(const char* filePath);
    GLuint compileShader(const char* source, GLenum type);
};