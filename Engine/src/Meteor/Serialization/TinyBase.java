package Meteor.Serialization;

abstract class TinyBase
{

    short nameLength;
    byte[] name;

    int size = 2 + 4;

    String getName()
    {
        return new String(name, 0, nameLength);
    }

    void setName(String name)
    {
        assert (name.length() < Short.MAX_VALUE);

        if (this.name != null)
            size -= this.name.length;

        nameLength = (short) name.length();
        this.name = name.getBytes();
        size += nameLength;
    }

    public abstract int getSize();

}
