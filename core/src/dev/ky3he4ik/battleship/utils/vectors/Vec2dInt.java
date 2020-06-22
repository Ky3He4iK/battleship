package dev.ky3he4ik.battleship.utils.vectors;

public class Vec2dInt {
    public int x, y;

    public Vec2dInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2dInt)) return false;

        Vec2dInt vec2dInt = (Vec2dInt) o;

        if (x != vec2dInt.x) return false;
        return y == vec2dInt.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
