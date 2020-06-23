package dev.ky3he4ik.battleship.utils.vectors;

/**
 * Двумерный вектор float
 */
public class Vec2d {
    public float x, y;

    public Vec2d(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2d)) return false;

        Vec2d vec2d = (Vec2d) o;

        if (Float.compare(vec2d.x, x) != 0) return false;
        return Float.compare(vec2d.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
