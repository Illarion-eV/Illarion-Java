#version 110

#ifdef GL_ES
precision mediump float;
#endif

uniform mat4 u_projTrans;

void main() {
   gl_TexCoord[0] = gl_MultiTexCoord0;
   gl_Position = u_projTrans * gl_Vertex;
}