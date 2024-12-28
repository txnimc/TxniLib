package toni.lib.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ColorUtils {
    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return color >> 16 & 255;
    }

    public static int green(int color) {
        return color >> 8 & 255;
    }

    public static int blue(int color) {
        return color & 255;
    }

    public static int color(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int color(int red, int green, int blue) {
        return color(255, red, green, blue);
    }

    public static int color(Vec3 color) {
        return color(as8BitChannel((float)color.x()), as8BitChannel((float)color.y()), as8BitChannel((float)color.z()));
    }

    public static int multiply(int color1, int color2) {
        if (color1 == -1) {
            return color2;
        } else {
            return color2 == -1 ? color1 : color(alpha(color1) * alpha(color2) / 255, red(color1) * red(color2) / 255, green(color1) * green(color2) / 255, blue(color1) * blue(color2) / 255);
        }
    }

    public static int scaleRGB(int color, float scale) {
        return scaleRGB(color, scale, scale, scale);
    }

    public static int scaleRGB(int color, float redScale, float greenScale, float blueScale) {
        return color(alpha(color), Math.clamp((long)((int)((float)red(color) * redScale)), 0, 255), Math.clamp((long)((int)((float)green(color) * greenScale)), 0, 255), Math.clamp((long)((int)((float)blue(color) * blueScale)), 0, 255));
    }

    public static int scaleRGB(int color, int scale) {
        return color(alpha(color), Math.clamp((long)red(color) * (long)scale / 255L, 0, 255), Math.clamp((long)green(color) * (long)scale / 255L, 0, 255), Math.clamp((long)blue(color) * (long)scale / 255L, 0, 255));
    }

    public static int greyscale(int color) {
        int i = (int)((float)red(color) * 0.3F + (float)green(color) * 0.59F + (float)blue(color) * 0.11F);
        return color(i, i, i);
    }

    public static int lerp(float delta, int color1, int color2) {
        int i = Mth.lerpInt(delta, alpha(color1), alpha(color2));
        int j = Mth.lerpInt(delta, red(color1), red(color2));
        int k = Mth.lerpInt(delta, green(color1), green(color2));
        int l = Mth.lerpInt(delta, blue(color1), blue(color2));
        return color(i, j, k, l);
    }

    public static int opaque(int color) {
        return color | -16777216;
    }

    public static int transparent(int color) {
        return color & 16777215;
    }

    public static int color(int alpha, int color) {
        return alpha << 24 | color & 16777215;
    }

    public static int white(float alpha) {
        return as8BitChannel(alpha) << 24 | 16777215;
    }

    public static int colorFromFloat(float alpha, float red, float green, float blue) {
        return color(as8BitChannel(alpha), as8BitChannel(red), as8BitChannel(green), as8BitChannel(blue));
    }

    public static Vector3f vector3fFromRGB24(int color) {
        float f = (float)red(color) / 255.0F;
        float g = (float)green(color) / 255.0F;
        float h = (float)blue(color) / 255.0F;
        return new Vector3f(f, g, h);
    }

    public static int average(int color1, int color2) {
        return color((alpha(color1) + alpha(color2)) / 2, (red(color1) + red(color2)) / 2, (green(color1) + green(color2)) / 2, (blue(color1) + blue(color2)) / 2);
    }

    public static int as8BitChannel(float value) {
        return Mth.floor(value * 255.0F);
    }

    public static float alphaFloat(int color) {
        return from8BitChannel(alpha(color));
    }

    public static float redFloat(int color) {
        return from8BitChannel(red(color));
    }

    public static float greenFloat(int color) {
        return from8BitChannel(green(color));
    }

    public static float blueFloat(int color) {
        return from8BitChannel(blue(color));
    }

    private static float from8BitChannel(int value) {
        return (float)value / 255.0F;
    }

    public static int toABGR(int color) {
        return color & -16711936 | (color & 16711680) >> 16 | (color & 255) << 16;
    }

    public static int fromABGR(int color) {
        return toABGR(color);
    }
}
