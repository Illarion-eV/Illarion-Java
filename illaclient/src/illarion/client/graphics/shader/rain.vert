#version 110

#ifdef GL_ES
precision mediump float;
#endif

void main() {
   gl_TexCoord[0] = gl_MultiTexCoord0;
   gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}