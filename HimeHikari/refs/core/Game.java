package core;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import state.*;

import graphic.*;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public abstract class Game {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    /**
     * The error callback for GLFW.
     */
    private GLFWErrorCallback errorCallback;

    /**
     * Shows if the game is running.
     */
    protected boolean running;

    /**
     * The GLFW window used by the game.
     */
    protected Window window;
    /**
     * Used for timing calculations.
     */
    protected Timer timer;
    /**
     * Used for rendering.
     */
    protected Renderer renderer;
    /**
     * Stores the current state.
     */
    protected StateMachine state;

    /**
     * Default contructor for the game.
     */
    public Game() {
        timer = new Timer();
        renderer = new Renderer();
        state = new StateMachine();
    }

    /**
     * This should be called to initialize and start the game.
     */
    public void start() {
        init();
        gameLoop();
        dispose();
    }

    /**
     * Releases resources that where used by the game.
     */
    public void dispose() {
        /* Dipose renderer */
        renderer.dispose();

        /* Set empty state to trigger the exit method in the current state */
        state.change(null);

        /* Release window and its callbacks */
        window.destroy();

        /* Terminate GLFW and release the error callback */
        glfwTerminate();
        errorCallback.free();
    }

    /**
     * Initializes the game.
     */
    public void init() {
        /* Set error callback */
        errorCallback = GLFWErrorCallback.createPrint();
        glfwSetErrorCallback(errorCallback);

        /* Initialize GLFW */
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        /* Create GLFW window */
        window = new Window(640, 480, "Simple Game - Pong", true);

        /* Initialize timer */
        timer.init();

        /* Initialize renderer */
        renderer.init();

        /* Initialize states */
        initStates();

        /* Initializing done, set running to true */
        running = true;
    }

    /**
     * Initializes the states.
     */
    public void initStates() {
        if (Game.isDefaultContext()) {
            state.add("example", new ExampleState());
            state.add("texture", new TextureState());
        } else {
            state.add("example", new LegacyExampleState());
            state.add("texture", new LegacyTextureState());
        }
        state.add("game", new GameState(renderer));
        state.change("game");
    }

    /**
     * The game loop. <br>
     * For implementation take a look at <code>VariableDeltaGame</code> and
     * <code>FixedTimestepGame</code>.
     */
    public abstract void gameLoop();

    /**
     * Handles input.
     */
    public void input() {
        state.input();
    }

    /**
     * Updates the game (fixed timestep).
     */
    public void update() {
        state.update();
    }

    /**
     * Updates the game (variable timestep).
     *
     * @param delta Time difference in seconds
     */
    public void update(float delta) {
        state.update(delta);
    }

    /**
     * Renders the game (no interpolation).
     */
    public void render() {
        state.render();
    }

    /**
     * Renders the game (with interpolation).
     *
     * @param alpha Alpha value, needed for interpolation
     */
    public void render(float alpha) {
        state.render(alpha);
    }

    /**
     * Synchronizes the game at specified frames per second.
     *
     * @param fps Frames per second
     */
    public void sync(int fps) {
        double lastLoopTime = timer.getLastLoopTime();
        double now = timer.getTime();
        float targetTime = 1f / fps;

        while (now - lastLoopTime < targetTime) {
            Thread.yield();

            /* This is optional if you want your game to stop consuming too much
             * CPU but you will loose some accuracy because Thread.sleep(1)
             * could sleep longer than 1 millisecond */
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }

            now = timer.getTime();
        }
    }

    /**
     * Determines if the OpenGL context supports version 3.2.
     *
     * @return true, if OpenGL context supports version 3.2, else false
     */
    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

}
