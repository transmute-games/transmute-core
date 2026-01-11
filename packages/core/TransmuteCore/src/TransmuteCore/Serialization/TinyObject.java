package TransmuteCore.Serialization;

import static TransmuteCore.Serialization.TinyUtils.*;

import java.util.ArrayList;
import java.util.List;

class TinyObject extends TinyBase
{

    private static final byte CONTAINER_TYPE = ContainerType.OBJECT;
    private short fieldCount;
    private List<TinyField> fields = new ArrayList<TinyField>();
    private short stringCount;
    private List<TinyString> strings = new ArrayList<TinyString>();
    private short arrayCount;
    private List<TinyArray> arrays = new ArrayList<TinyArray>();

    private TinyObject()
    {
    }

    public TinyObject(String name)
    {
        size += 1 + 2 + 2 + 2;
        setName(name);
    }

    public void addField(TinyField field)
    {
        fields.add(field);
        size += field.getSize();

        fieldCount = (short) fields.size();
    }

    public void addString(TinyString string)
    {
        strings.add(string);
        size += string.getSize();

        stringCount = (short) strings.size();
    }

    public void addArray(TinyArray array)
    {
        arrays.add(array);
        size += array.getSize();

        arrayCount = (short) arrays.size();
    }

    public int getSize()
    {
        return size;
    }

    public TinyField findField(String name)
    {
        for (TinyField field : fields)
        {
            if (field.getName().equals(name))
                return field;
        }
        return null;
    }

    public TinyString findString(String name)
    {
        for (TinyString string : strings)
        {
            if (string.getName().equals(name))
                return string;
        }
        return null;
    }

    public TinyArray findArray(String name)
    {
        for (TinyArray array : arrays)
        {
            if (array.getName().equals(name))
                return array;
        }
        return null;
    }

    int getBytes(byte[] dest, int pointer)
    {
        pointer = TinyUtils.writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = TinyUtils.writeBytes(dest, pointer, fieldCount);
        for (TinyField field : fields)
            pointer = field.getBytes(dest, pointer);

        pointer = TinyUtils.writeBytes(dest, pointer, stringCount);
        for (TinyString string : strings)
            pointer = string.getBytes(dest, pointer);

        pointer = TinyUtils.writeBytes(dest, pointer, arrayCount);
        for (TinyArray array : arrays)
            pointer = array.getBytes(dest, pointer);

        return pointer;
    }

    static TinyObject Deserialize(byte[] data, int pointer)
    {
        byte containerType = data[pointer++];
        assert (containerType == CONTAINER_TYPE);

        TinyObject result = new TinyObject();
        result.nameLength = TinyUtils.readShort(data, pointer);
        pointer += 2;
        result.name = TinyUtils.readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = TinyUtils.readInt(data, pointer);
        pointer += 4;

        // Early-out: pointer += result.size - sizeOffset - result.nameLength;

        result.fieldCount = TinyUtils.readShort(data, pointer);
        pointer += 2;

        for (int i = 0; i < result.fieldCount; i++)
        {
            TinyField field = TinyField.Deserialize(data, pointer);
            result.fields.add(field);
            pointer += field.getSize();
        }

        result.stringCount = TinyUtils.readShort(data, pointer);
        pointer += 2;

        for (int i = 0; i < result.stringCount; i++)
        {
            TinyString string = TinyString.Deserialize(data, pointer);
            result.strings.add(string);
            pointer += string.getSize();
        }

        result.arrayCount = TinyUtils.readShort(data, pointer);
        pointer += 2;

        for (int i = 0; i < result.arrayCount; i++)
        {
            TinyArray array = TinyArray.Deserialize(data, pointer);
            result.arrays.add(array);
            pointer += array.getSize();
        }

        return result;
    }

}
