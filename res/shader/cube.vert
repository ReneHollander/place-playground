#version 330 core

layout(location = 0) in vec3 position;

layout(location = 1) in vec3 tile_position;
layout(location = 2) in vec3 tile_color;

uniform mat4 vp;
uniform mat4 scale;

out vec3 color;

void main() {
    color = tile_color;
    gl_Position = vp * scale * vec4(position + tile_position, 1.0);
}