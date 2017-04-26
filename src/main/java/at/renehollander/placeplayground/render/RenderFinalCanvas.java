package at.renehollander.placeplayground.render;

import at.renehollander.placeplayground.Static;
import at.renehollander.placeplayground.Tile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static at.renehollander.placeplayground.util.Util.writeImage;

public class RenderFinalCanvas {

    public static void main(String[] args) throws IOException {
        BufferedInputStream tileInput = new BufferedInputStream(new FileInputStream(Static.TILES_SORTED_BIN));
        Tile tile = new Tile();

        byte[] canvas = new byte[1000 * 1000];
        while (tile.readNext(tileInput)) {
            if (tile.getX() >= 1000 || tile.getY() >= 1000) continue;
            int idx = tile.getY() * 1000 + tile.getX();
            canvas[idx] = (byte) tile.getColor();
        }
        writeImage(canvas, new File("out/final_canvas.png"));
    }

}
