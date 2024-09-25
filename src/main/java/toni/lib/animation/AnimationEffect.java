package toni.lib.animation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import toni.lib.animation.easing.EasingType;
import toni.lib.animation.effects.IAnimationEffect;

import java.util.function.Function;

public class AnimationEffect implements IAnimationEffect {

    @Getter @Setter
    public float in;
    @Getter @Setter
    public float out;

    @Getter @Setter
    public float intensity;
    @Getter @Setter
    public float speed;

    public Type type;

    public AnimationEffect(float in, float out, float intensity, float speed, Type type) {
        this.in = in;
        this.out = out;
        this.type = type;
        this.intensity = intensity;
        this.speed = speed;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(in);
        buf.writeFloat(out);
        buf.writeFloat(intensity);
        buf.writeFloat(speed);
        buf.writeEnum(type);
    }

    public static AnimationEffect decode(FriendlyByteBuf buf) {
        var in = buf.readFloat();
        var out = buf.readFloat();
        var intensity = buf.readFloat();
        var speed = buf.readFloat();
        var type = buf.readEnum(Type.class);

        return new AnimationEffect(in, out, intensity, speed, type);
    }

    public enum Type implements IAnimationFunction {
        WAVE {
            @Override
            public float calculate(AnimationEffect effect, float time) {
                return effect.intensity * Mth.cos(effect.speed * time);
            }
        }
    }
}