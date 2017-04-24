package at.renehollander.placeplayground.process;

import at.renehollander.placeplayground.Static;
import at.renehollander.placeplayground.Tile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

public class TileFileConverter {

    public static void convert(BufferedReader reader, OutputStream outputStream, OutputStream sortedStream) throws IOException {
        System.out.println("Reading Tiles");
        List<Tile> tiles = new ArrayList<Tile>(16559898);
        String header = reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String items[] = line.split(",");
            long timestamp = Long.parseLong(items[0]);
            byte[] user = Base64.getDecoder().decode(items[1]);
            short x = Short.parseShort(items[2]);
            short y = Short.parseShort(items[3]);
            byte color = Byte.parseByte(items[4]);
            Tile tile = new Tile(timestamp, user, x, y, color);
            outputStream.write(tile.getData());
            tiles.add(tile);
        }

        System.out.println("Sorting Tiles");
        tiles.sort(Comparator.comparingLong(Tile::getTimestamp));

        System.out.println("Writing Tiles");
        for (Tile t : tiles) {
            sortedStream.write(t.getData());
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(Static.TILES_CSV));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(Static.TILES_BIN));
        OutputStream sortedStream = new BufferedOutputStream(new FileOutputStream(Static.TILES_SORTED_BIN));
        convert(in, outputStream, sortedStream);
        in.close();
        outputStream.close();
        sortedStream.close();
    }

}
