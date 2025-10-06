#include <iostream>         // error handling and output
#include <cstdlib>          // EXIT_FAILURE
#include <memory>

#include <GL/glew.h>        // GLEW library
#include "GLFW/glfw3.h"     // GLFW library

// GLM Math Header inclusions
#include <glm/glm.hpp>
#include <glm/gtx/transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "SceneManager.h"
#include "ViewManager.h"
#include "ShapeMeshes.h"
#include "ShaderManager.h"

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

using std::unique_ptr;
using std::make_unique;
using std::cerr;
using std::cout;
using std::endl;

// Namespace for declaring global variables
namespace
{
    const char* const WINDOW_TITLE = "Capstone Project";
    GLFWwindow* g_Window = nullptr;

    unique_ptr<SceneManager> g_SceneManager;
    unique_ptr<ShaderManager> g_ShaderManager;
    unique_ptr<ViewManager> g_ViewManager;

    // === Reflection feature ===
    bool g_ReflectionEnabled = false;
    GLuint g_EnvCubemap = 0;
    float g_Reflectivity = 0.6f;

    glm::vec3 g_CameraPos(0.0f, 1.0f, 5.0f); // Used for reflection direction
}

// Function declarations
bool InitializeGLFW();
bool InitializeGLEW();
GLuint LoadCubemap(const std::vector<std::string>& faces);
void ProcessInput(GLFWwindow* window);

/***********************************************************
 *  main(int, char*)
 ***********************************************************/
int main(int argc, char* argv[])
{
    if (!InitializeGLFW()) return EXIT_FAILURE;

    g_ShaderManager = make_unique<ShaderManager>();
    g_SceneManager = make_unique<SceneManager>(g_ShaderManager.get());
    g_ViewManager = make_unique<ViewManager>(g_ShaderManager.get(), g_SceneManager.get());

    g_Window = g_ViewManager->CreateDisplayWindow(WINDOW_TITLE);

    if (!InitializeGLEW()) return EXIT_FAILURE;

    // === Load and use shaders ===
    g_ShaderManager->LoadShaders(
        "shaders/vertexShader.glsl",
        "shaders/fragmentShader.glsl");
    g_ShaderManager->use();

    // === Load environment cube map ===
    g_EnvCubemap = LoadCubemap({
        "textures/env/right.jpg",
        "textures/env/left.jpg",
        "textures/env/top.jpg",
        "textures/env/bottom.jpg",
        "textures/env/front.jpg",
        "textures/env/back.jpg"
    });

    g_ShaderManager->setSamplerCubeValue("u_EnvMap", 1);
    g_ShaderManager->setFloatValue("u_Reflectivity", g_Reflectivity);
    g_ShaderManager->setBoolValue("u_ReflectionEnabled", g_ReflectionEnabled);

    // === Prepare 3D Scene ===
    g_SceneManager->PrepareScene();

    // === Main Loop ===
    while (!glfwWindowShouldClose(g_Window))
    {
        ProcessInput(g_Window);

        // Enable z-depth
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        g_ShaderManager->use();
        g_ShaderManager->setVec3Value("viewPosition", g_CameraPos);
        g_ShaderManager->setBoolValue("u_ReflectionEnabled", g_ReflectionEnabled);

        g_ViewManager->PrepareSceneView();
        g_SceneManager->RenderScene();

        glfwSwapBuffers(g_Window);
        glfwPollEvents();
    }

    return EXIT_SUCCESS;
}

/***********************************************************
 *  InitializeGLFW()
 ***********************************************************/
bool InitializeGLFW()
{
    glfwInit();
#ifdef __APPLE__
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#else
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
#endif
    return true;
}

/***********************************************************
 *  InitializeGLEW()
 ***********************************************************/
bool InitializeGLEW()
{
    GLenum GLEWInitResult = glewInit();
    if (GLEW_OK != GLEWInitResult)
    {
        cerr << glewGetErrorString(GLEWInitResult) << endl;
        return false;
    }

    cout << "INFO: OpenGL Successfully Initialized\n";
    cout << "INFO: OpenGL Version: " << glGetString(GL_VERSION) << "\n" << endl;
    return true;
}

/***********************************************************
 *  ProcessInput()
 *  Handles keypresses for reflection toggle
 ***********************************************************/
void ProcessInput(GLFWwindow* window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);

    if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
    {
        static bool pressed = false;
        if (!pressed)
        {
            g_ReflectionEnabled = !g_ReflectionEnabled;
            cout << (g_ReflectionEnabled ? "Reflection ON" : "Reflection OFF") << endl;
            pressed = true;
        }
    }
    else
    {
        // Reset flag so next press can toggle again
        static bool pressed = false;
        pressed = false;
    }
}

/***********************************************************
 *  LoadCubemap()
 *  Loads a cube map texture for environment reflections
 ***********************************************************/
GLuint LoadCubemap(const std::vector<std::string>& faces)
{
    GLuint textureID;
    glGenTextures(1, &textureID);
    glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

    int width, height, nrChannels;
    unsigned char* data;
    for (GLuint i = 0; i < faces.size(); i++)
    {
        data = stbi_load(faces[i].c_str(), &width, &height, &nrChannels, 0);
        if (data)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, width, height,
                         0, GL_RGB, GL_UNSIGNED_BYTE, data);
            stbi_image_free(data);
        }
        else
        {
            cerr << "Failed to load cubemap texture at path: " << faces[i] << endl;
            stbi_image_free(data);
        }
    }
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

    return textureID;
}
