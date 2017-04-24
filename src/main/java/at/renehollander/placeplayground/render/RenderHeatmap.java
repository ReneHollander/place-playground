package at.renehollander.placeplayground.render;

import at.renehollander.placeplayground.Static;
import at.renehollander.placeplayground.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static at.renehollander.placeplayground.Util.toRGB;

public class RenderHeatmap {

    public static void main(String[] args) throws IOException {
        BufferedInputStream tileInput = new BufferedInputStream(new FileInputStream(Static.TILES_BIN));
        Tile tile = new Tile();

        int[] changes = new int[1000 * 1000];
        while (tile.readNext(tileInput)) {
            if (tile.getX() >= 1000 || tile.getY() >= 1000) continue;
            int idx = tile.getY() * 1000 + tile.getX();
            changes[idx]++;
        }

        BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                int idx = y * 1000 + x;
                bufferedImage.setRGB(x, y, toRGB(changes[idx] >= 255 ? 255 : changes[idx], 0, 0));
            }
        }
        ImageIO.write(bufferedImage, "png", new File("out/heatmap.png"));
    }

}
