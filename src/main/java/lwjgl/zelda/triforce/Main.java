package lwjgl.zelda.triforce;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private long window;
    private Triforce renderer;
    private boolean isFullscreen = false;
    private boolean wireframe = false;
    private int windowedX, windowedY, windowedWidth = 800, windowedHeight = 600;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        System.out.println("Starting Triforce with LWJGL...");
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowedWidth, windowedHeight, "Triforce", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        setupKeyCallback();
        centerWindow();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(window, (win, width, height) -> glViewport(0, 0, width, height));

        renderer = new Triforce();
    }

    private void setupKeyCallback() {
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action != GLFW_PRESS) return;

            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(win, true);
                    break;
                case GLFW_KEY_F:
                    toggleFullscreen();
                    break;
                case GLFW_KEY_W:
                    toggleWireframe();
                    break;
            }
        });
    }

    private void toggleWireframe() {
        wireframe = !wireframe;
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
    }

    private void toggleFullscreen() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null) return;

        if (isFullscreen) {
            glfwSetWindowMonitor(window, NULL, windowedX, windowedY, windowedWidth, windowedHeight, vidmode.refreshRate());
            centerWindow();
        } else {
            saveWindowedState();
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
        }

        isFullscreen = !isFullscreen;
    }

    private void saveWindowedState() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            glfwGetWindowPos(window, x, y);
            glfwGetWindowSize(window, width, height);

            windowedX = x.get(0);
            windowedY = y.get(0);
            windowedWidth = width.get(0);
            windowedHeight = height.get(0);
        }
    }

    private void centerWindow() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null) return;

        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, width, height);

            int x = (vidmode.width() - width.get(0)) / 2;
            int y = (vidmode.height() - height.get(0)) / 2;
            glfwSetWindowPos(window, x, y);
        }
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderer.render();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
