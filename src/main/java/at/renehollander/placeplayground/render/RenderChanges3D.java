package at.renehollander.placeplayground.render;

import at.renehollander.placeplayground.Static;
import at.renehollander.placeplayground.Tile;
import at.renehollander.placeplayground.util.Camera;
import at.renehollander.placeplayground.util.Shader;
import at.renehollander.placeplayground.util.Util;
import at.renehollander.placeplayground.util.VertexData;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import sun.misc.Unsafe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderChanges3D {

    private long window;
    private Matrix4d projMatrix = new Matrix4d();

    private int width = 0;
    private int height = 0;
    private boolean grabbed = false;
    private Camera camera = new Camera(new Vector3d(10, 10, 10), 0.6719517620178328, 5.522221753309998);

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "r/place", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, this::windowKeyCallback);
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);
        glfwSetMouseButtonCallback(window, this::windowMousButtonCallback);
        glfwSetCursorPosCallback(window, this::windowCursorCallback);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            width = pWidth.get(0);
            height = pHeight.get(0);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
            updateProjectionMatrix();
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
    }

    private Matrix4d vp = new Matrix4d();

    private void windowMousButtonCallback(long window, int button, int action, int mode) {
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            grabbed = true;
        }
    }

    private double oldMouseX = Double.NaN;
    private double oldMouseY = Double.NaN;
    private double mouse_sensitivity = 0.1;

    private void windowCursorCallback(long window, double xpos, double ypos) {
        if (grabbed) {
            double dX = 0;
            double dY = 0;
            if (!Double.isNaN(oldMouseX) && !Double.isNaN(oldMouseY)) {
                dX = xpos - oldMouseX;
                dY = ypos - oldMouseY;
            }
            oldMouseX = xpos;
            oldMouseY = ypos;

            camera.yaw(Math.toRadians(dX * mouse_sensitivity));
            camera.pitch(Math.toRadians(dY * mouse_sensitivity));
        }
    }

    private void updateProjectionMatrix() {
        projMatrix.setPerspective((float) Math.toRadians(45f), (float) width / (float) height, 0.1f, 5000.0f);
    }

    private void windowSizeCallback(long window, int width, int height) {
        this.width = width;
        this.height = height;
        updateProjectionMatrix();
    }

    private void windowKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            grabbed = false;
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

//        int s = 100;
//        int n = s * s;
//        float[] instancedata = new float[n * 3 + n * 3];
//        int cnt = 0;
//        float f = 0;
//        for (int i = 0; i < s; i++) {
//            for (int j = 0; j < s; j++) {
//                instancedata[cnt++] = i;
//                instancedata[cnt++] = 0;
//                instancedata[cnt++] = j;
//                instancedata[cnt++] = f;
//                instancedata[cnt++] = f;
//                instancedata[cnt++] = f;
//                f += 1f / (float) n;
//            }
//        }

        float[] instancedata = new float[0];
        try {
//            instancedata = loadTilesFlat(Static.TILES_SORTED_BIN);
            instancedata = loadTiles3D(Static.TILES_SORTED_BIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int instance_count = instancedata.length / 6;

        int cube_vertexarray = glGenVertexArrays();
        glBindVertexArray(cube_vertexarray);
        {
            int cube_vertexbuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, cube_vertexbuffer);
            glBufferData(GL_ARRAY_BUFFER, VertexData.CUBE_VERTICES, GL_STATIC_DRAW);

            int cube_elementbuffer = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, cube_elementbuffer);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexData.CUBE_ELEMENTS, GL_STATIC_DRAW);

            int cube_instancedata = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, cube_instancedata);
            glBufferData(GL_ARRAY_BUFFER, instancedata, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, cube_vertexbuffer);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            glEnableVertexAttribArray(1);
            glBindBuffer(GL_ARRAY_BUFFER, cube_instancedata);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 0 * 4);
            glVertexAttribDivisor(1, 1);

            glEnableVertexAttribArray(2);
            glBindBuffer(GL_ARRAY_BUFFER, cube_instancedata);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
            glVertexAttribDivisor(2, 1);
        }
        glBindVertexArray(0);

        Matrix4d scale = new Matrix4d().scale(0.1);

        Shader cubeShader = Shader.load("cube");

        while (!glfwWindowShouldClose(window)) {
            glViewport(0, 0, width, height);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glEnable(GL_DEPTH_TEST);

            updateInputDevices();

            projMatrix.mul(camera.getViewMatrix(), vp);

            cubeShader.bind();
            cubeShader.setUniform("vp", vp);
            cubeShader.setUniform("scale", scale);

            glBindVertexArray(cube_vertexarray);
            glDrawElementsInstanced(GL_TRIANGLES, VertexData.CUBE_ELEMENTS.length, GL_UNSIGNED_SHORT, 0, instance_count);
            glBindVertexArray(0);

            cubeShader.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateInputDevices() {
        double speed = 0.1;

        if (isPressed(GLFW_KEY_LEFT_SHIFT) || isPressed(GLFW_KEY_RIGHT_SHIFT)) {
            speed *= 10;
        }

        if (isPressed(GLFW_KEY_W)) camera.forwards(speed);
        if (isPressed(GLFW_KEY_S)) camera.backwards(speed);
        if (isPressed(GLFW_KEY_A)) camera.left(speed);
        if (isPressed(GLFW_KEY_D)) camera.right(speed);
        if (isPressed(GLFW_KEY_SPACE)) camera.up(speed);
        if (isPressed(GLFW_KEY_LEFT_CONTROL)) camera.down(speed);
    }

    public boolean isPressed(int code) {
        return glfwGetKey(window, code) == GLFW_PRESS;
    }

    public static void main(String[] args) {
        new RenderChanges3D().run();
    }

    private static float[] loadTilesFlat(File input) throws IOException {
        BufferedInputStream tileInput = new BufferedInputStream(new FileInputStream(input));
        int s = 1000;
        int n = s * s;

        Tile tile = new Tile();
        byte[][] canvas = new byte[s][s];
        while (tile.readNext(tileInput)) {
            if (tile.getX() >= s || tile.getY() >= s) continue;
            canvas[tile.getY()][tile.getX()] = (byte) tile.getColor();
        }

        float[] data = new float[n * 6];
        int cnt = 0;
        for (int y = 0; y < s; y++) {
            for (int x = 0; x < s; x++) {
                data[cnt++] = x;
                data[cnt++] = 0;
                data[cnt++] = y;
                data[cnt++] = Util.toFloatR(Util.toColor(canvas[y][x]));
                data[cnt++] = Util.toFloatG(Util.toColor(canvas[y][x]));
                data[cnt++] = Util.toFloatB(Util.toColor(canvas[y][x]));
            }
        }
        return data;
    }

    private static float[] loadTiles3D(File input) throws IOException {
        BufferedInputStream tileInput = new BufferedInputStream(new FileInputStream(input));
        int n = 16559898;

        float[] data = new float[n * 6];

        int cnt = 0;
        Tile tile = new Tile();
        int[] height = new int[1000 * 1000];
        while (tile.readNext(tileInput)) {
            if (tile.getX() >= 1000 || tile.getY() >= 1000) continue;
            int idx = tile.getY() * 1000 + tile.getX();
            int color = Util.toColor((byte) tile.getColor());
            data[cnt++] = tile.getX();
            data[cnt++] = height[idx];
            data[cnt++] = tile.getY();
            data[cnt++] = Util.toFloatR(color);
            data[cnt++] = Util.toFloatG(color);
            data[cnt++] = Util.toFloatB(color);
            height[idx]++;
        }

        return data;
    }


}
