package toni.lib.animation;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import toni.lib.animation.easing.EasingType;
import toni.lib.animation.effects.IAnimationEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimationTimeline {
    public final float duration;

    @Getter @Setter
    private float current;
    private AnimationKeyframe effected_keyframe = new AnimationKeyframe();
    private AnimationKeyframe keyframe = new AnimationKeyframe();

    private boolean hasSortedTransitions = false;
    private final HashMap<Binding, List<Transition>> transitions = new HashMap<>();
    private final HashMap<Binding, List<AnimationEffect>> effects = new HashMap<>();
    private final Binding[] bindings = Binding.values();

    public static AnimationTimeline builder(float duration) {
        return new AnimationTimeline(duration);
    }

    private AnimationTimeline(float duration) {
        this.duration = duration;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeFloat(duration);
        buffer.writeFloat(current);
        effected_keyframe.encode(buffer);
        keyframe.encode(buffer);

        buffer.writeMap(transitions, FriendlyByteBuf::writeEnum, (buf, val) -> {
            buf.writeVarInt(val.size());
            val.forEach(i -> i.encode(buf));
        });

        buffer.writeMap(effects, FriendlyByteBuf::writeEnum, (buf, val) -> {
            buf.writeVarInt(val.size());
            val.forEach(i -> i.encode(buf));
        });
    }

    public static AnimationTimeline decode(FriendlyByteBuf buffer) {
        var duration = buffer.readFloat();
        var current = buffer.readFloat();
        var effected_keyframe = AnimationKeyframe.decode(buffer);
        var keyframe = AnimationKeyframe.decode(buffer);

        var transitions = buffer.readMap(enumClass -> buffer.readEnum(Binding.class), (buf) -> {
            var size = buffer.readVarInt();
            var list = new ArrayList<Transition>();
            for (int i = 0; i < size; i++) {
                list.add(Transition.decode(buf));
            }
            return list;
        });

        var effects = buffer.readMap(enumClass -> buffer.readEnum(Binding.class), (buf) -> {
            var size = buffer.readVarInt();
            var list = new ArrayList<AnimationEffect>();
            for (int i = 0; i < size; i++) {
                list.add(AnimationEffect.decode(buf));
            }
            return list;
        });

        var ths = new AnimationTimeline(duration);
        ths.current = current;
        ths.effected_keyframe = effected_keyframe;
        ths.keyframe = keyframe;
        ths.transitions.putAll(transitions);
        ths.effects.putAll(effects);

        return ths;
    }

    public AnimationKeyframe getKeyframe() {
        if (!hasSortedTransitions)
        {
            hasSortedTransitions = true;
            for (var kvp : transitions.entrySet())
                kvp.getValue().sort((a, b) -> Float.compare(a.getOut(), b.getOut()));
        }

        // loop over the list of transitions on the timeline
        for (var kvp : transitions.entrySet()) {
            // they should be sorted, so it's safe to just grab the first start value, and treat them all as if they're not overlapping
            float value = kvp.getValue().get(0).getStartValue();
            for (var transition : kvp.getValue()) {
                switch (transition.getPosition(current)) {
                    case DURING -> value = transition.eval(kvp.getKey(), current);
                    case AFTER -> value = transition.getEndValue();
                }
            }

            keyframe.setValue(kvp.getKey(), value);
        }

        for (var key : bindings) {
            if (!effects.containsKey(key)) {
                effected_keyframe.setValue(key, keyframe.getValue(key));
                continue;
            }

            for (var effect : effects.get(key)) {
                if (effect.getPosition(current) == IAnimationEffect.Position.DURING)
                    effected_keyframe.setValue(key, keyframe.getValue(key) + effect.type.calculate(effect, current));
            }
        }

        return effected_keyframe;
    }

    public AnimationKeyframe applyPose(GuiGraphics context, float objectWidth, float objectHeight) {
        var key = getKeyframe();

        PoseUtils.applyScale(context, key.size);
        PoseUtils.applyPosition(context, key.size, objectWidth, objectHeight, key.posX, key.posY, key.posZ);
        PoseUtils.applyYRotation(context, key.size, objectWidth, objectHeight, key.rotY);
        PoseUtils.applyXRotation(context, key.size, objectWidth, objectHeight, key.rotX);
        PoseUtils.applyZRotation(context, key.size, objectWidth, objectHeight, key.rotZ);

        return key;
    }


    public int getColor() {
        var keyframe = getKeyframe();
        return FastColor.ARGB32.color(
                Mth.clamp((int) (keyframe.alpha * 255 + 5), 0, 255),
                FastColor.ARGB32.red((int) keyframe.color),
                FastColor.ARGB32.green((int) keyframe.color),
                FastColor.ARGB32.blue((int) keyframe.color));
    }



    public AnimationTimeline advancePlayhead(float delta) {
        current = Math.max(0, Math.min(current + delta, duration));
        return this;
    }

    public AnimationTimeline resetPlayhead(float newPosition) {
        current = Math.max(0, Math.min(newPosition, duration));
        return this;
    }

    public AnimationTimeline withColor(int alpha, int red, int green, int blue) {
        keyframe.color = FastColor.ARGB32.color(alpha, red, green, blue);
        return this;
    }

    public AnimationTimeline withXPosition(float x) {
        keyframe.posX = x;
        return this;
    }

    public AnimationTimeline withYPosition(float y) {
        keyframe.posY = y;
        return this;
    }

    public AnimationTimeline withZPosition(float z) {
        keyframe.posZ = z;
        return this;
    }

    public AnimationTimeline withXRotation(float x) {
        keyframe.rotX = x;
        return this;
    }

    public AnimationTimeline withYRotation(float y) {
        keyframe.rotY = y;
        return this;
    }

    public AnimationTimeline withZRotation(float z) {
        keyframe.rotZ = z;
        return this;
    }

    public AnimationTimeline withSize(float size) {
        keyframe.size = size;
        return this;
    }

    public AnimationTimeline transition(Binding binding, float in, float out, float start, float end, EasingType easing) {
        var transition = new Transition(in, out, easing, start, end);

        if (!transitions.containsKey(binding))
            transitions.put(binding, new ArrayList<>());

        transitions.get(binding).add(transition);
        return this;
    }

    public AnimationTimeline fadeout(float time) {
        return transition(Binding.Alpha, duration - time, duration, 1f, 0f, EasingType.EaseOutSine);
    }

    public AnimationTimeline fadeout(float time, EasingType easing) {
        return transition(Binding.Alpha, duration - time, duration, 1f, 0f, easing);
    }

    public AnimationTimeline fadein(float time) {
        return transition(Binding.Alpha, 0f, time, 0f, 1f, EasingType.EaseOutSine);
    }

    public AnimationTimeline fadein(float time, EasingType easing) {
        return transition(Binding.Alpha, 0f, time, 0f, 1f, easing);
    }


    public AnimationTimeline shake(float time) {
        transition(Binding.zRot, time, time + 0.1f, 0f, 5f, EasingType.EaseInBounce);
        transition(Binding.Size, time, time + 0.1f, 1f, 1.1f, EasingType.EaseInCubic);
        transition(Binding.Size, time + 0.1f, time + 0.2f, 1.f, 1f, EasingType.EaseOutCubic);
        return transition(Binding.zRot, time, time + 0.2f, 5f, 0, EasingType.EaseOutBounce);
    }

    public AnimationTimeline waveEffect(Binding binding, float intensity, float speed, float in, float out) {
        if (!effects.containsKey(binding))
            effects.put(binding, new ArrayList<>());

        effects.get(binding).add(new AnimationEffect(in, out, intensity, speed, AnimationEffect.Type.WAVE));
        return this;
    }

    public AnimationTimeline waveEffect(Binding binding, float intensity, float speed) {
        return waveEffect(binding, intensity, speed, 0f, duration);
    }

    public AnimationTimeline waveEffect() {
        return waveEffect(Binding.zRot, 2.5f, 5f, 0f, duration);
    }
}


