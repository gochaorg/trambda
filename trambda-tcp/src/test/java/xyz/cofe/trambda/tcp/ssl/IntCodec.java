package xyz.cofe.trambda.tcp.ssl;

public class IntCodec {
    public static byte[] toBytes(int x) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (x & 0xFF);
        bytes[1] = (byte) ((x >> 8) & 0xFF);
        bytes[2] = (byte) ((x >> 8 * 2) & 0xFF);
        bytes[3] = (byte) ((x >> 8 * 3) & 0xFF);
        return bytes;
    }

    public static int fromBytes(byte[] b) {
        if (b == null) throw new IllegalArgumentException("b==null");
        if (b.length < 4) throw new IllegalArgumentException("b.length<4");
        int b0 = (int) (b[0] & 0xff);
        int b1 = ((int) (b[1]  & 0xff)) << 8;
        int b2 = ((int) (b[2]  & 0xff)) << 8 * 2;
        int b3 = ((int) (b[3]  & 0xff)) << 8 * 3;
        return b0 | b1 | b2 | b3;
    }
}
