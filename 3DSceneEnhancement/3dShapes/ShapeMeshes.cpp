#include "ShapeMeshes.h"
#include <iostream>

using std::cout;
using std::endl;

ShapeMeshes::ShapeMeshes() {
    glGenVertexArrays(1, &dummyVAO);
}
ShapeMeshes::~ShapeMeshes() {
    if (dummyVAO) glDeleteVertexArrays(1, &dummyVAO);
}

void ShapeMeshes::LoadPlaneMesh() { cout << "Loaded Plane Mesh" << endl; }
void ShapeMeshes::LoadSphereMesh() { cout << "Loaded Sphere Mesh" << endl; }
void ShapeMeshes::LoadCylinderMesh() { cout << "Loaded Cylinder Mesh" << endl; }
void ShapeMeshes::LoadTorusMesh() { cout << "Loaded Torus Mesh" << endl; }
void ShapeMeshes::LoadTaperedCylinderMesh() { cout << "Loaded Tapered Cylinder Mesh" << endl; }
void ShapeMeshes::LoadHalfSphereMesh() { cout << "Loaded Half-Sphere Mesh" << endl; }

void ShapeMeshes::DrawPlaneMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 6); }
void ShapeMeshes::DrawSphereMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 36); }
void ShapeMeshes::DrawCylinderMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 36); }
void ShapeMeshes::DrawTorusMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 36); }
void ShapeMeshes::DrawTaperedCylinderMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 36); }
void ShapeMeshes::DrawHalfSphereMesh() { glBindVertexArray(dummyVAO); glDrawArrays(GL_TRIANGLES, 0, 36); }