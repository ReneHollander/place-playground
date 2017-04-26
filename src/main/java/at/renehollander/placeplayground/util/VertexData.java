package at.renehollander.placeplayground.util;

public class VertexData {

    public static final float[] CUBE_VERTICES = {
            // front
            0f, 0f, 1.0f,
            1.0f, 0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            0f, 1.0f, 1.0f,
            // back
            0f, 0f, 0f,
            1.0f, 0f, 0f,
            1.0f, 1.0f, 0f,
            0f, 1.0f, 0f,
    };

    public static final short[] CUBE_ELEMENTS = {
            // front
            0, 1, 2,
            2, 3, 0,
            // top
            1, 5, 6,
            6, 2, 1,
            // back
            7, 6, 5,
            5, 4, 7,
            // bottom
            4, 0, 3,
            3, 7, 4,
            // left
            4, 5, 1,
            1, 0, 4,
            // right
            3, 2, 6,
            6, 7, 3,
    };
//
//    public static final float[] CUBE_INSTANCEDATA = {
//            0, 0, 0,
//            1, 0, 0,
//            2, 0, 0,
//            3, 0, 0
//    };

}
