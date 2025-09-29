#pragma once
#include <gl/glew.h>

class ShapeMeshes
{
public:
    ShapeMeshes();
    ~ShapeMeshes();

    void LoadPlaneMesh();
    void LoadSphereMesh();
    void LoadCylinderMesh();
    void LoadTorusMesh();
    void LoadTaperedCylinderMesh();
    void LoadHalfSphereMesh();

    void DrawPlaneMesh();
    void DrawSphereMesh();
    void DrawCylinderMesh();
    void DrawTorusMesh();
    void DrawTaperedCylinderMesh();
    void DrawHalfSphereMesh();

private:
    GLuint planeVAO, sphereVAO, cylinderVAO, torusVAO, taperedCylinderVAO, halfSphereVAO;
    // + VBO/EBO as needed
};