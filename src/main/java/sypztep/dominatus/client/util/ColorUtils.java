package sypztep.dominatus.client.util;

public final class ColorUtils {
    public static int rgbaToHex(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                (blue & 0xFF);
    }

    public static int fromRgb(int red, int green, int blue) {
        return rgbaToHex(red, green, blue, 255);
    }
    /**
     * Interpolates between two colors using a ratio.
     *
     * @param color1 The starting color in RGBA format.
     * @param color2 The ending color in RGBA format.
     * @param ratio  The interpolation ratio between 0.0 (color1) and 1.0 (color2).
     * @return The interpolated color in RGBA format.
     */
    public static int interpolateColor(int color1, int color2, float ratio) {
        // Extract RGBA components from the first color
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;

        // Extract RGBA components from the second color
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;

        // Interpolate each component
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        int a = (int) (a1 + (a2 - a1) * ratio);

        // Clamp the values to the range [0, 255]
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));
        a = Math.min(255, Math.max(0, a));

        // Combine components back into an RGBA color
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
