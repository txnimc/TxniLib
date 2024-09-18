package toni.lib.animation.effects;

public interface IAnimationEffect {
    float getOut();
    float getIn();

    public default Position getPosition(float current) {
        if (current > getOut())
            return Position.AFTER;

        if (current > getIn())
            return Position.DURING;

        return Position.BEFORE;
    }

    public enum Position {
        BEFORE,
        DURING,
        AFTER
    }
}
