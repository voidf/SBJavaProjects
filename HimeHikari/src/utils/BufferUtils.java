package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils {
    private BufferUtils() {
    }

    public static ByteBuffer createByteBuffer(byte[] arr) {
        var res = ByteBuffer.allocateDirect(arr.length).order(ByteOrder.nativeOrder());
        res.put(arr).flip();
        return res;
    }

    public static FloatBuffer createFloatBuffer(float[] arr) {
        var res = ByteBuffer.allocateDirect(arr.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        res.put(arr).flip();
        return res;
    }

    public static IntBuffer createIntBuffer(int[] arr) {
        var res = ByteBuffer.allocateDirect(arr.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
        res.put(arr).flip();
        return res;
    }
}