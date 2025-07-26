package game;

import math.*;


public class AABB {

    public Vector2f min, max;

    public AABB(Entity entity) {
        min = new Vector2f(entity.getX(), entity.getY());
        max = new Vector2f(
                entity.getX() + entity.getWidth(),
                entity.getY() + entity.getHeight()
        );
    }

    /**
     * Checks if this AABB intersects another AABB.
     *
     * @param other The other AABB
     *
     * @return true if a collision was detected.
     */
    public boolean intersects(AABB other) {
        if (this.max.x < other.min.x) {
            return false;
        }

        if (this.max.y < other.min.y) {
            return false;
        }

        if (this.min.x > other.max.x) {
            return false;
        }

        if (this.min.y > other.max.y) {
            return false;
        }

        // All tests failed, we have a intersection
        return true;
    }

}
