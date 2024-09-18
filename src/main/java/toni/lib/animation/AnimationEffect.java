package toni.lib.animation;

import lombok.Getter;
import lombok.Setter;
import toni.lib.animation.effects.IAnimationEffect;

import java.util.function.Function;

public class AnimationEffect implements IAnimationEffect {
    @Getter @Setter
    private float in;
    @Getter @Setter
    private float out;

    public Function<Float, Float> transformer;

    public AnimationEffect(float in, float out, Function<Float, Float> transformer) {
        this.in = in;
        this.out = out;
        this.transformer = transformer;
    }
}
