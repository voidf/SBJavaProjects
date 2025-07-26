import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.debug.org.eclipse.jetty.util.BufferUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

class cb implements GLFWWindowSizeCallbackI {
    @Override
    public void invoke(long window, int width, int height) {
        System.out.println(width * height);
        System.out.println(width ^ height);
    }
}

public class GameFrame {

    // The window handle
    private long window;
    private float r_color = 0.0f;
    private float g_color = 0.5f;
    private float b_color = 1.0f;
    private GLCapabilities caps;

    public static void cb() {
        System.out.println("callback");
    }

    public static void main(String[] args) {
        new GameFrame().run();
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

    private void setBackGroundTexture() throws IOException {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // 放大过滤，线性插值
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST); // 缩小过滤，线性插值

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        ByteBuffer bgimg = utils.Utils.ioResourceToByteBuffer("Assets/osu-resources/osu.Game.Resources/Textures/Backgrounds/bg2.jpg", 8192);
        if (!STBImage.stbi_info_from_memory(bgimg, w, h, comp)) // 这个是读
            throw new IOException("Failed to read image information: " + STBImage.stbi_failure_reason());
        ByteBuffer img = STBImage.stbi_load_from_memory(bgimg, w, h, comp, 0); // 这是写入
        if (img == null) throw new IOException("Failed to load image: " + STBImage.stbi_failure_reason());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, img);
        STBImage.stbi_image_free(img);
    }

    private void registerKeySniffer(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            // We will detect this in the rendering loop
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

    public void run() {
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

        GLFWWindowSizeCallbackI c = new cb();
        var c2 = new GLFWWindowSizeCallbackI() {
            @Override
            public void invoke(long window, int width, int height) {
                System.out.println("From inline");
                System.out.println(window);
            }
        };
//        glfwSetWindowSizeCallback(window, c);
//        glfwSetWindowSizeCallback(window, c2); // 只有最後一個會生效

        registerKeySniffer(window);

        try (MemoryStack stack = stackPush()) {

            setMouseTexture(stack);// 这一块用来设置鼠标贴图


            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );

        } catch (IOException e) {
            throw new RuntimeException(e);// the stack frame is popped automatically
        }
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        glfwShowWindow(window);
        caps = GL.createCapabilities();
        if (!caps.OpenGL20) {
            throw new AssertionError("Require OpenGL 2.0.");
        }
        IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
        nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
        // Make the OpenGL context current

        try {
            setBackGroundTexture();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        glfwPollEvents();
    }

    private void render() {

    }

    private void loop() {

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            var t = System.currentTimeMillis();
//            System.out.println(t);
//            var s = Double.valueOf(t % 1000) / 1000f * Math.PI;
//            glClearColor((float) Math.abs(Math.cos(s)), (float) Math.abs(Math.cos(s + 2f / 3f * Math.PI)), (float) Math.abs(Math.cos(s + 4f / 3f * Math.PI)), 0.0f);
//            System.out.println(Math.cos(s));
//            System.out.println(s);
            glfwSwapBuffers(window); // swap the color buffers

        }
    }

}

