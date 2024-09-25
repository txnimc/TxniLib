package toni.lib.animation;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import toni.lib.animation.easing.EasingFunctionArgs;
import toni.lib.animation.easing.EasingManager;
import toni.lib.animation.easing.EasingType;
import toni.lib.animation.effects.IAnimationEffect;

import java.util.ArrayList;
import java.util.List;

public class Transition implements IAnimationEffect {

    private AnimationTimeline parent;

    @Getter @Setter
    private float in;
    @Getter @Setter
    private float out;

    @Getter @Setter
    private float startValue;
    @Getter @Setter
    private float endValue;

    private EasingType easingFunctionTypeCache;
    private Double easingFunctionArgCache;

    private Double2DoubleFunction easingFunction;

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(in);
        buf.writeFloat(out);
        buf.writeFloat(startValue);
        buf.writeFloat(endValue);

        buf.writeEnum(easingFunctionTypeCache);
        buf.writeDouble(easingFunctionArgCache);
    }

    public static Transition decode(FriendlyByteBuf buf) {

        var in = buf.readFloat();
        var out = buf.readFloat();
        var startValue = buf.readFloat();
        var endValue = buf.readFloat();

        var easingFunctionTypeCache = buf.readEnum(EasingType.class);
        var easingFunctionArgCache = buf.readDouble();

        return new Transition(in, out, easingFunctionTypeCache, startValue, endValue, easingFunctionArgCache);
    }

    public Transition(float in, float out, EasingType easing, float start, float end) {
        this.in = in;
        this.out = out;
        this.startValue = start;
        this.endValue = end;

        easingFunctionTypeCache = easing;
        easingFunctionArgCache = 1.0d;

        easingFunction = EasingManager.getEasingFunction.apply(new EasingFunctionArgs(easing, null));
    }

    public Transition(float in, float out, EasingType easing, float start, float end, double elasticity) {
        this.in = in;
        this.out = out;
        this.startValue = start;
        this.endValue = end;

        easingFunctionTypeCache = easing;
        easingFunctionArgCache = elasticity;

        easingFunction = EasingManager.getEasingFunction.apply(new EasingFunctionArgs(easing, elasticity));
    }

    public float eval(Binding binding, float current) {
        var lerp = Mth.clamp((double) (current - in) / (out - in), 0f, 1f);
        lerp = easingFunction.apply(lerp);

        lerp = Mth.clamp(lerp, 0, 1f);

        if (binding == Binding.Color)
            return FastColor.ARGB32.lerp((float) lerp, (int) startValue, (int) endValue);

        return (float) Mth.lerp(lerp, startValue, endValue);
    }


}

