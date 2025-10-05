#include "ShaderManager.h"
#include <fstream>
#include <sstream>
#include <iostream>
#include <glm/gtc/type_ptr.hpp>

using std::cerr;
using std::cout;
using std::endl;

ShaderManager::ShaderManager() : m_ID(0) {}
ShaderManager::~ShaderManager() { if (m_ID) glDeleteProgram(m_ID); }

std::string ShaderManager::readFile(const char* filePath)
{
    std::ifstream file(filePath, std::ios::in | std::ios::binary);
    if (!file) {
        cerr << "Failed to read file: " << filePath << endl;
        return "";
    }
    std::ostringstream contents;
    contents << file.rdbuf();
    return contents.str();
}

GLuint ShaderManager::compileShader(const char* source, GLenum type)
{
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, nullptr);
    glCompileShader(shader);

    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char log[512];
        glGetShaderInfoLog(shader, 512, nullptr, log);
        cerr << "Shader compilation failed: " << log << endl;
    }
    return shader;
}

bool ShaderManager::LoadShaders(const char* vertexPath, const char* fragmentPath)
{
    std::string vCode = readFile(vertexPath);
    std::string fCode = readFile(fragmentPath);
    if (vCode.empty() || fCode.empty()) return false;

    GLuint vertexShader = compileShader(vCode.c_str(), GL_VERTEX_SHADER);
    GLuint fragmentShader = compileShader(fCode.c_str(), GL_FRAGMENT_SHADER);

    m_ID = glCreateProgram();
    glAttachShader(m_ID, vertexShader);
    glAttachShader(m_ID, fragmentShader);
    glLinkProgram(m_ID);

    GLint success;
    glGetProgramiv(m_ID, GL_LINK_STATUS, &success);
    if (!success) {
        char log[512];
        glGetProgramInfoLog(m_ID, 512, nullptr, log);
        cerr << "Shader linking failed: " << log << endl;
        return false;
    }

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    return true;
}

void ShaderManager::use() { glUseProgram(m_ID); }

// --- Uniform setters ---
void ShaderManager::setBoolValue(const std::string& name, bool value) const {
    glUniform1i(glGetUniformLocation(m_ID, name.c_str()), (int)value);
}
void ShaderManager::setIntValue(const std::string& name, int value) const {
    glUniform1i(glGetUniformLocation(m_ID, name.c_str()), value);
}
void ShaderManager::setFloatValue(const std::string& name, float value) const {
    glUniform1f(glGetUniformLocation(m_ID, name.c_str()), value);
}
void ShaderManager::setVec2Value(const std::string& name, const glm::vec2& value) const {
    glUniform2fv(glGetUniformLocation(m_ID, name.c_str()), 1, &value[0]);
}
void ShaderManager::setVec3Value(const std::string& name, const glm::vec3& value) const {
    glUniform3fv(glGetUniformLocation(m_ID, name.c_str()), 1, &value[0]);
}
void ShaderManager::setVec3Value(const std::string& name, float x, float y, float z) const {
    glUniform3f(glGetUniformLocation(m_ID, name.c_str()), x, y, z);
}
void ShaderManager::setVec4Value(const std::string& name, const glm::vec4& value) const {
    glUniform4fv(glGetUniformLocation(m_ID, name.c_str()), 1, &value[0]);
}
void ShaderManager::setMat4Value(const std::string& name, const glm::mat4& mat) const {
    glUniformMatrix4fv(glGetUniformLocation(m_ID, name.c_str()), 1, GL_FALSE, glm::value_ptr(mat));
}
void ShaderManager::setSampler2DValue(const std::string& name, int slot) const {
    glUniform1i(glGetUniformLocation(m_ID, name.c_str()), slot);
}