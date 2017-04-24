package at.renehollander.placeplayground;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static void writeImage(byte[] canvas, File out) throws IOException {
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                int idx = y * 1000 + x;
                image.setRGB(x, y, toColor(canvas[idx]));
            }
        }
        ImageIO.write(image, "png", out);
    }

    public static int toColor(byte b) {
        switch (b) {
            case 0:
                return 0xFFFFFF;
            case 1:
                return 0xE4E4E4;
            case 2:
                return 0x888888;
            case 3:
                return 0x222222;
            case 4:
                return 0xFFA7D1;
            case 5:
                return 0xE50000;
            case 6:
                return 0xE59500;
            case 7:
                return 0xA06A42;
            case 8:
                return 0xE5D900;
            case 9:
                return 0x94E044;
            case 10:
                return 0x02BE01;
            case 11:
                return 0x00E5F0;
            case 12:
                return 0x0083C7;
            case 13:
                return 0x0000EA;
            case 14:
                return 0xE04AFF;
            case 15:
                return 0x820080;
            default:
                throw new IllegalArgumentException("color not found");
        }
    }

    public static byte[] toHash(String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(username.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static double map(double in, double min, double max, double newMin, double newMax) {
        return (in - min) * (newMax - newMin) / (max - min) + newMin;
    }

    public static int toRGB(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
    }

}
