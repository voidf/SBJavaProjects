package state;

import game.Ball;
import game.Paddle;
import graphic.Color;
import graphic.Renderer;
import graphic.Texture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glClearColor;


public class GameState implements State {

    public static final int NO_COLLISION = 0;
    public static final int COLLISION_TOP = 1;
    public static final int COLLISION_BOTTOM = 2;
    public static final int COLLISION_RIGHT = 3;
    public static final int COLLISION_LEFT = 4;
    private final Renderer renderer;
    private Texture texture;
    private Paddle player;
    private Paddle opponent;
    private Ball ball;

    private int playerScore;
    private int opponentScore;
    private int gameWidth;
    private int gameHeight;

    public GameState(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void input() {
        player.input();
        opponent.input(ball);
    }

    @Override
    public void update(float delta) {
        /* Update position */
        player.update(delta);
        opponent.update(delta);
        ball.update(delta);

        /* Check for collisions */
        player.checkBorderCollision(gameHeight);
        ball.collidesWith(player);
        opponent.checkBorderCollision(gameHeight);
        ball.collidesWith(opponent);

        /* Update score if necessary */
        switch (ball.checkBorderCollision(gameWidth, gameHeight)) {
            case COLLISION_LEFT:
                opponentScore++;
                break;
            case COLLISION_RIGHT:
                playerScore++;
                break;
        }
    }

    @Override
    public void render(float alpha) {
        /* Clear drawing area */
        renderer.clear();

        /* Draw game objects */
        texture.bind();
        renderer.begin();
        player.render(renderer, alpha);
        opponent.render(renderer, alpha);
        ball.render(renderer, alpha);
        renderer.end();

        /* Draw score */
        String scoreText = "Score";
        int scoreTextWidth = renderer.getTextWidth(scoreText);
        int scoreTextHeight = renderer.getTextHeight(scoreText);
        float scoreTextX = (gameWidth - scoreTextWidth) / 2f;
        float scoreTextY = gameHeight - scoreTextHeight - 5;
        renderer.drawText(scoreText, scoreTextX, scoreTextY, Color.BLACK);

        String playerText = "Player | " + playerScore;
        int playerTextWidth = renderer.getTextWidth(playerText);
        int playerTextHeight = renderer.getTextHeight(playerText);
        float playerTextX = gameWidth / 2f - playerTextWidth - 50;
        float playerTextY = scoreTextY - playerTextHeight;
        renderer.drawText(playerText, playerTextX, playerTextY, Color.BLACK);

        String opponentText = opponentScore + " | Opponent";
        int opponentTextWidth = renderer.getDebugTextWidth(playerText);
        int opponentTextHeight = renderer.getTextHeight(playerText);
        float opponentTextX = gameWidth / 2f + 50;
        float opponentTextY = scoreTextY - opponentTextHeight;
        renderer.drawText(opponentText, opponentTextX, opponentTextY, Color.BLACK);
    }

    @Override
    public void enter() {
        /* Get width and height of framebuffer */
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        /* Load texture */
        texture = Texture.loadTexture("resources/pong.png");

        /* Initialize game objects */
        float speed = 250f;
        player = new Paddle(Color.GREEN, texture, 5f, (height - 100) / 2f, speed, true); // 原图，裁剪
        opponent = new Paddle(Color.RED, texture, width - 25f, (height - 100) / 2f, speed, false);
        ball = new Ball(Color.BLUE, texture, (width - 20) / 2f, (height - 20) / 2f, speed * 1.5f);

        /* Initialize variables */
        playerScore = 0;
        opponentScore = 0;
        gameWidth = width;
        gameHeight = height;

        /* Set clear color to gray */
        glClearColor(0.5f, 0.5f, 0.5f, 1f);
    }

    @Override
    public void exit() {
        texture.delete();
    }

}
