
package graphic;

import static org.lwjgl.opengl.GL30.*;


public class VertexArrayObject {

    /**
     * Stores the handle of the VAO.
     */
    private final int id;

    /**
     * Creates a Vertex Array Object (VAO).
     */
    public VertexArrayObject() {
        id = glGenVertexArrays();
    }

    /**
     * Binds the VAO.
     */
    public void bind() {
        glBindVertexArray(id);
    }

    /**
     * Deletes the VAO.
     */
    public void delete() {
        glDeleteVertexArrays(id);
    }

    /**
     * Getter for the Vertex Array Object ID.
     *
     * @return Handle of the VAO
     */
    public int getID() {
        return id;
    }

}
