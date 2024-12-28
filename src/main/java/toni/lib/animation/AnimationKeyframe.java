package toni.lib.animation;


import net.minecraft.network.FriendlyByteBuf;
import toni.lib.utils.ColorUtils;

public class AnimationKeyframe {
    public float rotX;
    public float rotY;
    public float rotZ;
    public float posX;
    public float posY;
    public float posZ;
    public float size = 1f;
    public float alpha = 1f;
    public float color = ColorUtils.color(255, 255, 255, 255);

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeFloat(rotX);
        buffer.writeFloat(rotY);
        buffer.writeFloat(rotZ);
        buffer.writeFloat(posX);
        buffer.writeFloat(posY);
        buffer.writeFloat(posZ);
        buffer.writeFloat(size);
        buffer.writeFloat(alpha);
        buffer.writeFloat(color);
    }

    public static AnimationKeyframe decode(FriendlyByteBuf buffer) {
        AnimationKeyframe keyframe = new AnimationKeyframe();
        keyframe.rotX = buffer.readFloat();
        keyframe.rotY = buffer.readFloat();
        keyframe.rotZ = buffer.readFloat();
        keyframe.posX = buffer.readFloat();
        keyframe.posY = buffer.readFloat();
        keyframe.posZ = buffer.readFloat();
        keyframe.size = buffer.readFloat();
        keyframe.alpha = buffer.readFloat();
        keyframe.color = buffer.readFloat();
        return keyframe;
    }

    public void setValue(Binding key, float value) {
        switch (key) {
            case xRot -> rotX = value;
            case yRot -> rotY = value;
            case zRot -> rotZ = value;
            case xPos -> posX = value;
            case yPos -> posY = value;
            case zPos -> posZ = value;
            case Size -> size = value;
            case Alpha -> alpha = value;
            case Color -> color = value;
        }
    }

    public float getValue(Binding key) {
        return switch (key) {
            case xRot -> rotX;
            case yRot -> rotY;
            case zRot -> rotZ;
            case xPos -> posX;
            case yPos -> posY;
            case zPos -> posZ;
            case Size -> size;
            case Alpha -> alpha;
            case Color -> color;
        };
    }


}
