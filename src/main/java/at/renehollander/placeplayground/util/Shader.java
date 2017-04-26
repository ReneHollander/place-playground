package at.renehollander.placeplayground.util;

import org.apache.commons.io.IOUtils;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {
    int programID;

    String vertexShaderSource;
    String fragmentShaderSource;
    String geometryShaderSource;

    int vertexShaderID;
    int geometryShaderID;
    int fragmentShaderID;

    public Shader() {
        programID = glCreateProgram();
    }

    public int getProgramID() {
        return programID;
    }

    public int getVertexShaderID() {
        return vertexShaderID;
    }

    public int getGeometryShaderID() {
        return geometryShaderID;
    }

    public int getFragmentShaderID() {
        return fragmentShaderID;
    }

    public void link() {
        glLinkProgram(getProgramID());

        if (glGetProgrami(getProgramID(), GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error linking shader program: " + glGetProgramInfoLog(getProgramID(), glGetProgrami(getProgramID(), GL_INFO_LOG_LENGTH)));
        }
    }

    public void bind() {
        glUseProgram(getProgramID());
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void dispose() {
        unbind();

        glDetachShader(getProgramID(), getVertexShaderID());
        glDetachShader(getProgramID(), getGeometryShaderID());
        glDetachShader(getProgramID(), getFragmentShaderID());

        glDeleteShader(getVertexShaderID());
        glDeleteShader(getGeometryShaderID());
        glDeleteShader(getFragmentShaderID());

        glDeleteProgram(getProgramID());
    }

    private Map<String, Integer> uniforms = new HashMap<>();

    public void setUniform(String uniformName, Matrix4d projMatrix) {
        projMatrix.get(matrixFloatBuffer);
        glUniformMatrix4fv(this.getUniformID(uniformName), false, matrixFloatBuffer);
    }

    public class Uniform {

        private String name;
        private int id;

    }

    public int addUniform(String uniformName) {
        int loc = glGetUniformLocation(getProgramID(), uniformName);
        if (loc == -1) {
            throw new IllegalArgumentException("Uniform with name " + uniformName + " not found!");
        }
        uniforms.put(uniformName, loc);
        return loc;
    }

    private int getUniformID(String uniformName) {
        Integer id = this.uniforms.get(uniformName);
        if (id == null) {
            id = this.addUniform(uniformName);
        }
        return id;
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(this.getUniformID(uniformName), value.x, value.y, value.z);
    }

    private FloatBuffer matrixFloatBuffer = BufferUtils.createFloatBuffer(16);

    public void setUniform(String uniformName, Matrix4f value) {
        value.get(matrixFloatBuffer);
        glUniformMatrix4fv(this.getUniformID(uniformName), false, matrixFloatBuffer);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(this.getUniformID(uniformName), value);
    }

    private void createShader(int type, File file) {
        Objects.requireNonNull(file);

        try {
            createShader(type, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createShader(int type, InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        String source;
        try {
            source = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createShader(type, source);
    }

    private void createShader(int type, String source) {
        Objects.requireNonNull(source);

        int id = glCreateShader(type);

        if (type == GL_VERTEX_SHADER) {
            this.vertexShaderSource = source;
            this.vertexShaderID = id;
        } else if (type == GL_GEOMETRY_SHADER) {
            this.geometryShaderSource = source;
            this.geometryShaderID = id;
        } else if (type == GL_FRAGMENT_SHADER) {
            this.fragmentShaderSource = source;
            this.fragmentShaderID = id;
        }

        glShaderSource(id, source);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error creating shader: " + glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH)));
        }
        glAttachShader(this.getProgramID(), id);
    }

    public void attachVertexShader(String source) {
        createShader(GL_VERTEX_SHADER, source);
    }

    public void attachVertexShader(InputStream inputStream) {
        createShader(GL_VERTEX_SHADER, inputStream);
    }

    public void attachVertexShader(File file) {
        createShader(GL_VERTEX_SHADER, file);
    }

    public void attachGeometryShader(String source) {
        createShader(GL_GEOMETRY_SHADER, source);
    }

    public void attachGeometryShader(InputStream inputStream) {
        createShader(GL_GEOMETRY_SHADER, inputStream);
    }

    public void attachGeometryShader(File file) {
        createShader(GL_GEOMETRY_SHADER, file);
    }

    public void attachFragmentShader(String source) {
        createShader(GL_FRAGMENT_SHADER, source);
    }

    public void attachFragmentShader(InputStream inputStream) {
        createShader(GL_FRAGMENT_SHADER, inputStream);
    }

    public void attachFragmentShader(File file) {
        createShader(GL_FRAGMENT_SHADER, file);
    }

    public static Shader load(String name) {
        Shader shader = new Shader();
        shader.attachVertexShader(new File("res/shader/" + name + ".vert"));
        shader.attachFragmentShader(new File("res/shader/" + name + ".frag"));
        shader.link();
        return shader;
    }
}