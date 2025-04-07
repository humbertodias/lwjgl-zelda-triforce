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
        // Set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Keep window hidden until ready
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window
        window = glfwCreateWindow(800, 600, "Triforce", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // ESC closes the window
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                glfwSetWindowShouldClose(win, true);
        });

        centerWindow();

        // Make OpenGL context current and enable v-sync
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // Create OpenGL capabilities
        GL.createCapabilities();

        renderer = new Triforce();
    }

    private void centerWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, width, height);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null) {
                int screenWidth = vidmode.width();
                int screenHeight = vidmode.height();
                glfwSetWindowPos(
                        window,
                        (screenWidth - width.get(0)) / 2,
                        (screenHeight - height.get(0)) / 2
                );
            }
        }
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            // Clear screen and depth buffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        // Free window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
