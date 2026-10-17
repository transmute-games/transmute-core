package TransmuteCore.math;

/**
 * Deep collision helpers for axis-aligned boxes.
 * Prefer this over inventing per-game AABB math in templates.
 */
public final class Collision
{
    private Collision()
    {
    }

    public static boolean aabb(float x1, float y1, float w1, float h1,
                              float x2, float y2, float w2, float h2)
    {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    /**
     * Resolves {@code moving} against {@code solid} by separating on the shallowest axis.
     * Mutates {@code moving} position fields (x, y) via the returned {@link Resolution}.
     */
    public static Resolution resolveAabb(
        float mx, float my, float mw, float mh,
        float sx, float sy, float sw, float sh)
    {
        if (!aabb(mx, my, mw, mh, sx, sy, sw, sh))
        {
            return Resolution.none();
        }

        float overlapLeft = (mx + mw) - sx;
        float overlapRight = (sx + sw) - mx;
        float overlapTop = (my + mh) - sy;
        float overlapBottom = (sy + sh) - my;

        float minX = Math.min(overlapLeft, overlapRight);
        float minY = Math.min(overlapTop, overlapBottom);

        if (minX < minY)
        {
            float dx = overlapLeft < overlapRight ? -overlapLeft : overlapRight;
            return new Resolution(mx + dx, my, true, false, dx < 0, dx > 0, false, false);
        }
        float dy = overlapTop < overlapBottom ? -overlapTop : overlapBottom;
        return new Resolution(mx, my + dy, false, true, false, false, dy < 0, dy > 0);
    }

    /**
     * Result of separating two AABBs. {@code landed} is true when pushed up out of a solid
     * (typical platformer floor contact).
     */
    public static final class Resolution
    {
        public final float x;
        public final float y;
        public final boolean hitHorizontal;
        public final boolean hitVertical;
        public final boolean hitLeft;
        public final boolean hitRight;
        public final boolean hitTop;
        public final boolean hitBottom;
        public final boolean collided;

        private Resolution(float x, float y, boolean hitHorizontal, boolean hitVertical,
                           boolean hitLeft, boolean hitRight, boolean hitTop, boolean hitBottom)
        {
            this.x = x;
            this.y = y;
            this.hitHorizontal = hitHorizontal;
            this.hitVertical = hitVertical;
            this.hitLeft = hitLeft;
            this.hitRight = hitRight;
            this.hitTop = hitTop;
            this.hitBottom = hitBottom;
            this.collided = hitHorizontal || hitVertical;
        }

        private static Resolution none()
        {
            return new Resolution(0, 0, false, false, false, false, false, false);
        }

        /** True when the mover was pushed upward (standing on a floor). */
        public boolean landed()
        {
            return hitTop;
        }
    }
}
