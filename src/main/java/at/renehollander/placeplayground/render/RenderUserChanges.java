package at.renehollander.placeplayground.render;

import at.renehollander.placeplayground.Static;
import at.renehollander.placeplayground.Tile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static at.renehollander.placeplayground.util.Util.toHash;
import static at.renehollander.placeplayground.util.Util.writeImage;

public class RenderUserChanges {

    public static void main(String[] args) throws IOException {
        byte[][] hashes = createUserHashes(args);

        BufferedInputStream bin_in = new BufferedInputStream(new FileInputStream(Static.TILES_SORTED_BIN));
        Tile tile = new Tile();

        byte[] canvas = new byte[1000 * 1000];
        int contributions = 0;
        while (tile.readNext(bin_in)) {
            if (tile.getX() >= 1000 || tile.getY() >= 1000) continue;
            int idx = tile.getY() * 1000 + tile.getX();
            if (contains(tile.getUser(), hashes)) {
                canvas[idx] = (byte) tile.getColor();
                contributions++;
            }
        }
        System.out.println("Number of contributions: " + contributions);

        writeImage(canvas, new File("out/userchanges.png"));
    }

    private static byte[][] createUserHashes(String... names) {
        byte[][] hashes = new byte[names.length][];
        for (int i = 0; i < names.length; i++) {
            hashes[i] = toHash(names[i]);
        }
        return hashes;
    }

    private static boolean contains(byte[] hash, byte[][] userHashes) {
        for (byte[] user : userHashes) {
            if (Arrays.equals(user, hash)) return true;
        }
        return false;
    }

}
