package at.renehollander.placeplayground;

import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Tile implements Cloneable {

    private byte[] data = new byte[33];

    private long timestamp = -1;
    private byte[] user = null;
    private int x = -1;
    private int y = -1;
    private int color = -1;

    public Tile() {
    }

    public Tile(byte[] data) {
        this.data = data;
    }

    public Tile(long timestamp, byte[] user, int x, int y, int color) {
        this.timestamp = timestamp;
        this.user = user;
        this.x = x;
        this.y = y;
        this.color = color;

        data[0] = (byte) (timestamp >>> 56);
        data[1] = (byte) (timestamp >>> 48);
        data[2] = (byte) (timestamp >>> 40);
        data[3] = (byte) (timestamp >>> 32);
        data[4] = (byte) (timestamp >>> 24);
        data[5] = (byte) (timestamp >>> 16);
        data[6] = (byte) (timestamp >>> 8);
        data[7] = (byte) (timestamp >>> 0);
        System.arraycopy(user, 0, data, 8, 20);
        data[28] = (byte) ((x >>> 8) & 0xFF);
        data[29] = (byte) ((x >>> 0) & 0xFF);
        data[30] = (byte) ((y >>> 8) & 0xFF);
        data[31] = (byte) ((y >>> 0) & 0xFF);
        data[32] = (byte) color;
    }

    public byte[] getData() {
        return data;
    }

    public long getTimestamp() {
        if (timestamp == -1) {
            timestamp = Longs.fromBytes(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
        }
        return timestamp;
    }

    public byte[] getUser() {
        if (user == null) {
            user = Arrays.copyOfRange(data, 8, 28);
        }
        return user;
    }

    public int getX() {
        if (x == -1) {
            x = Shorts.fromBytes(data[28], data[29]);
        }
        return x;
    }

    public int getY() {
        if (y == -1) {
            y = Shorts.fromBytes(data[30], data[31]);
        }
        return y;
    }

    public int getColor() {
        if (color == -1) {
            color = data[32];
        }
        return color;
    }

    public boolean readNext(InputStream inputStream) throws IOException {
        if (inputStream.read(data) < 0) return false;
        timestamp = -1;
        user = null;
        x = -1;
        y = -1;
        color = -1;
        return true;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "timestamp=" + getTimestamp() +
                ", user=" + Arrays.toString(getUser()) +
                ", x=" + getX() +
                ", y=" + getY() +
                ", color=" + getColor() +
                '}';
    }

}
