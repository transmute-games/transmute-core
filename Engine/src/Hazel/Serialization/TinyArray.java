package Hazel.Serialization;

import static Hazel.Serialization.TinyUtils.*;

class TinyArray extends TinyBase
{

    private static final byte CONTAINER_TYPE = ContainerType.ARRAY;
    private byte type;
    private int count;
    private byte[] data;

    private short[] shortData;
    private char[] charData;
    private int[] intData;
    private long[] longData;
    private float[] floatData;
    private double[] doubleData;
    private boolean[] booleanData;

    private TinyArray()
    {
        size += 1 + 1 + 4;
    }

    private void updateSize()
    {
        size += getDataSize();
    }

    int getBytes(byte[] dest, int pointer)
    {
        pointer = TinyUtils.writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);
        pointer = TinyUtils.writeBytes(dest, pointer, type);
        pointer = TinyUtils.writeBytes(dest, pointer, count);

        switch (type)
        {
            case Type.BYTE:
                pointer = TinyUtils.writeBytes(dest, pointer, data);
                break;
            case Type.SHORT:
                pointer = TinyUtils.writeBytes(dest, pointer, shortData);
                break;
            case Type.CHAR:
                pointer = TinyUtils.writeBytes(dest, pointer, charData);
                break;
            case Type.INTEGER:
                pointer = TinyUtils.writeBytes(dest, pointer, intData);
                break;
            case Type.LONG:
                pointer = TinyUtils.writeBytes(dest, pointer, longData);
                break;
            case Type.FLOAT:
                pointer = TinyUtils.writeBytes(dest, pointer, floatData);
                break;
            case Type.DOUBLE:
                pointer = TinyUtils.writeBytes(dest, pointer, doubleData);
                break;
            case Type.BOOLEAN:
                pointer = TinyUtils.writeBytes(dest, pointer, booleanData);
                break;
        }
        return pointer;
    }

    public int getSize()
    {
        return size;
    }

    int getDataSize()
    {
        switch (type)
        {
            case Type.BYTE:
                return data.length * Type.getSize(Type.BYTE);
            case Type.SHORT:
                return shortData.length * Type.getSize(Type.SHORT);
            case Type.CHAR:
                return charData.length * Type.getSize(Type.CHAR);
            case Type.INTEGER:
                return intData.length * Type.getSize(Type.INTEGER);
            case Type.LONG:
                return longData.length * Type.getSize(Type.LONG);
            case Type.FLOAT:
                return floatData.length * Type.getSize(Type.FLOAT);
            case Type.DOUBLE:
                return doubleData.length * Type.getSize(Type.DOUBLE);
            case Type.BOOLEAN:
                return booleanData.length * Type.getSize(Type.BOOLEAN);
        }
        return 0;
    }

    public static TinyArray Byte(String name, byte[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.BYTE;
        array.count = data.length;
        array.data = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Short(String name, short[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.SHORT;
        array.count = data.length;
        array.shortData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Char(String name, char[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.CHAR;
        array.count = data.length;
        array.charData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Integer(String name, int[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.INTEGER;
        array.count = data.length;
        array.intData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Long(String name, long[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.LONG;
        array.count = data.length;
        array.longData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Float(String name, float[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.FLOAT;
        array.count = data.length;
        array.floatData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Double(String name, double[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.DOUBLE;
        array.count = data.length;
        array.doubleData = data;
        array.updateSize();
        return array;
    }

    public static TinyArray Boolean(String name, boolean[] data)
    {
        TinyArray array = new TinyArray();
        array.setName(name);
        array.type = Type.BOOLEAN;
        array.count = data.length;
        array.booleanData = data;
        array.updateSize();
        return array;
    }

    static TinyArray Deserialize(byte[] data, int pointer)
    {
        byte containerType = data[pointer++];
        assert (containerType == CONTAINER_TYPE);

        TinyArray result = new TinyArray();
        result.nameLength = TinyUtils.readShort(data, pointer);
        pointer += 2;
        result.name = TinyUtils.readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = TinyUtils.readInt(data, pointer);
        pointer += 4;

        result.type = data[pointer++];

        result.count = TinyUtils.readInt(data, pointer);
        pointer += 4;

        switch (result.type)
        {
            case Type.BYTE:
                result.data = new byte[result.count];
                TinyUtils.readBytes(data, pointer, result.data);
                break;
            case Type.SHORT:
                result.shortData = new short[result.count];
                TinyUtils.readShorts(data, pointer, result.shortData);
                break;
            case Type.CHAR:
                result.charData = new char[result.count];
                TinyUtils.readChars(data, pointer, result.charData);
                break;
            case Type.INTEGER:
                result.intData = new int[result.count];
                TinyUtils.readInts(data, pointer, result.intData);
                break;
            case Type.LONG:
                result.longData = new long[result.count];
                TinyUtils.readLongs(data, pointer, result.longData);
                break;
            case Type.FLOAT:
                result.floatData = new float[result.count];
                TinyUtils.readFloats(data, pointer, result.floatData);
                break;
            case Type.DOUBLE:
                result.doubleData = new double[result.count];
                TinyUtils.readDoubles(data, pointer, result.doubleData);
                break;
            case Type.BOOLEAN:
                result.booleanData = new boolean[result.count];
                TinyUtils.readBooleans(data, pointer, result.booleanData);
                break;
        }

        pointer += result.count * Type.getSize(result.type);

        return result;
    }

}
