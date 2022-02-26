package io.github.c20c01.tool.proTool.Packets;

import io.github.c20c01.tool.proTool.VarInputStream;
import io.github.c20c01.tool.proTool.VarOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.Deflater;

public class Packet {

    protected int id;
    private final ByteArrayOutputStream rawBuffer = new ByteArrayOutputStream();
    private final VarOutputStream varBuffer = new VarOutputStream(rawBuffer);

    protected Packet(int id) {
        this.id = id;
    }

    protected Packet(int id, byte[] data) throws IOException {
        this.id = id;
        varBuffer.write(data);
    }

    protected void putVarInt(int v) {
        try {
            varBuffer.writeVarInt(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put an Integer to this packet
     *
     * @param v Integer value
     */
    protected void putInt(int v) {
        try {
            varBuffer.writeInt(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a byte to this packet
     *
     * @param v VarInt value
     */
    protected void putByte(int v) {
        try {
            varBuffer.writeByte(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a Long to this packet
     *
     * @param v value
     */
    protected void putLong(long v) {
        try {
            varBuffer.writeLong(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a Float to this packet
     *
     * @param v value
     */
    protected void putFloat(float v) {
        try {
            varBuffer.writeFloat(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a Boolean to this packet
     *
     * @param v value
     */
    protected void putBoolean(boolean v) {
        try {
            varBuffer.writeBoolean(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a Double to this packet
     *
     * @param v value
     */
    protected void putDouble(double v) {
        try {
            varBuffer.writeDouble(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put a byte array to this packet
     *
     * @param v byte array
     */
    protected void putBytes(byte[] v) {
        try {
            varBuffer.write(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put short to this packet
     *
     * @param v value
     */
    protected void putShort(int v) {
        try {
            varBuffer.writeShort(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put String to this packet
     *
     * @param v value
     */
    protected void putString(String v) {
        try {
            varBuffer.writeString(v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get this packet's bytes as ready to send data in Minecraft's packet format
     *
     * @param compression whether to use post-compression format
     * @return byte array with packet's data
     */
    private final Deflater deflater = new Deflater();

    public byte[] getData(boolean compression) {
        try {
            byte[] data = rawBuffer.toByteArray();
            ByteArrayOutputStream tmpBuf = new ByteArrayOutputStream();
            VarOutputStream varBuf = new VarOutputStream(tmpBuf);
            varBuf.writeVarInt(data.length + (compression ? 2 : 1));
            if (compression)
                varBuf.writeByte(0);
            varBuf.writeVarInt(id);
            varBuf.write(data);
            return tmpBuf.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get {@link VarInputStream} with this packet's contents
     *
     * @return input stream with packet's contents
     */

    protected VarInputStream getInputStream() {
        return new VarInputStream(new ByteArrayInputStream(rawBuffer.toByteArray()));
    }

    /**
     * Access packet's method via reflection
     *
     * @param name method name
     * @return method's return value
     */
    public Object accessPacketMethod(String name) {
        Class<? extends Packet> cl = getClass();
        try {
            return cl.getDeclaredMethod(name).invoke(this);
        } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
