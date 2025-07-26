import graphics.Shader;
import level.Level;
import maths.M4f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class Main implements Runnable {

    // The window handle
    private long window;
    private Thread thread;
    private int width = 1366;
    private int height = 768;
    private boolean running = false;
    private float r_color = 0.0f;
    private float g_color = 0.5f;
    private float b_color = 1.0f;
    private GLCapabilities caps;
    private Level level;
    private long monitor;


    public static void main(String[] args) {
        new Main().run();
    }

    private void setMouseTexture(MemoryStack stack) throws IOException {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);

        ByteBuffer cursorImage;


        cursorImage = utils.Utils.ioResourceToByteBuffer("Assets/osu-resources/osu.Game.Resources/Textures/Cursor/cur.png", 1024);

        ByteBuffer pixels = STBImage.stbi_load_from_memory(cursorImage, w, h, comp, 0);
        System.out.println(w.get(0));
        System.out.println(h.get(0));
        System.out.println(comp.get(0));
        var img = GLFWImage.mallocStack(stack).width(w.get(0)).height(h.get(0)).pixels(pixels);

        var cursor = glfwCreateCursor(img, 0, 4);

        glfwSetCursor(window, cursor);
    }

        private void createCubemapTexture() throws IOException {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, tex);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        ByteBuffer imageBuffer;
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        ByteBuffer image;
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE);
        for (int i = 0; i < 6; i++) {
            imageBuffer = utils.Utils.ioResourceToByteBuffer("Assets/bgt.jpg", 8 * 1024);
            if (!stbi_info_from_memory(imageBuffer, w, h, comp))
                throw new IOException("Failed to read image information: " + stbi_failure_reason());
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null)
                throw new IOException("Failed to load image: " + stbi_failure_reason());
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB8, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            stbi_image_free(image);
        }
        if (caps.OpenGL32 || caps.GL_ARB_seamless_cube_map) {
            glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        }
    }



    private void registerKeySniffer(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_RELEASE)
                switch (key) {
                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(w, true);
                        System.out.print("ESC");
                        break;
                    case GLFW_KEY_SPACE:
                        System.out.println("SPACE");
                        break;
                }
        }); // 检测按键
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long lastTime = System.nanoTime(); // 帧率检测
            double delta = 0.0;
            double ns = 1000000000.0 / 60.0;
            long timer = System.currentTimeMillis();
            int updates = 0;
            int frames = 0;
            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                if (delta >= 1.0) {
                    update();
                    updates++;
                    delta--;
                }
                render();
                frames++;
                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    System.out.println(updates + " ups, " + frames + " fps");
                    updates = 0;
                    frames = 0;
                }
                if (glfwWindowShouldClose(window) == true)
                    running = false;
            }
        }
    }

    public void run() {
        running = true;
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        loop();


        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
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
        window = glfwCreateWindow(1366, 768, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowSizeCallback(window, (window, hei, wid) -> {
            System.out.println(hei);
            System.out.println(wid);
        });

//        glfwSetKeyCallback(window, new Input());
        glfwMakeContextCurrent(window);
        registerKeySniffer(window);


        try (MemoryStack stack = stackPush()) {

            setMouseTexture(stack);// 这一块用来设置鼠标贴图


            IntBuffer pWidth = stack.mallocInt(1); // 设置显示
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            monitor = glfwGetPrimaryMonitor();
            GLFWVidMode vidmode = glfwGetVideoMode(monitor);

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );

        } catch (IOException e) {
            throw new RuntimeException(e);// the stack frame is popped automatically
        }

        // Enable v-sync
        glfwSwapInterval(1);
        caps = GL.createCapabilities();


        glfwShowWindow(window);

        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);

        glEnableClientState(GL_VERTEX_ARRAY);
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

        Shader.loadAll();
        try {
            createCubemapTexture();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix4f pr_matrix = M4f.orthographic(
                -10.0f,
                10.0f,
                -10.0f * 9.0f / 16.0f,
                10.0f * 9.0f / 16.0f,
                -1.0f,
                1.0f
        );

        Shader.BG.setUniform("pr_matrix", pr_matrix);
        Shader.BG.setUniform("tex", 1);

        Shader.BIRD.setUniform("pr_matrix", pr_matrix);
        Shader.BIRD.setUniform("tex", 1);

        Shader.PIPE.setUniform("pr_matrix", pr_matrix);
        Shader.PIPE.setUniform("tex", 1);


        if (!caps.OpenGL20) {
            throw new AssertionError("Require OpenGL 2.0.");
        }
        IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
        nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
        // Make the OpenGL context current


        level = new Level();
    }

    private void update() {
        glfwPollEvents();
        level.update();
        if (level.isGameOver()) {
            level = new Level();
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        level.render();
        var error = glGetError();
        if (error != GL_NO_ERROR)
            System.out.println(error);
        glfwSwapBuffers(window); // swap the color buffers
    }


}

