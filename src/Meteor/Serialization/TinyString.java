package Meteor.Serialization;

import static Meteor.Serialization.TinyUtils.*;

class TinyString extends TinyBase {
	
	private static final byte CONTAINER_TYPE = ContainerType.STRING;
	private int count;
	private char[] characters;
	
	private TinyString() {
		size += 1 + 4;
	}
	
	public String getString() {
		return new String(characters);
	}
	
	private void updateSize() {
		size += getDataSize();
	}
	
	int getBytes(byte[] dest, int pointer) {
		pointer = TinyUtils.writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = TinyUtils.writeBytes(dest, pointer, count);
		pointer = TinyUtils.writeBytes(dest, pointer, characters);
		return pointer;
	}
	
	public int getSize() {
		return size;
	}
	
	private int getDataSize() {
		return characters.length * Type.getSize(Type.CHAR);
	}
	
	public static TinyString Create(String name, String data) {
		TinyString string = new TinyString();
		string.setName(name);
		string.count = data.length();
		string.characters = data.toCharArray();
		string.updateSize();
		return string;
	}
	
	static TinyString Deserialize(byte[] data, int pointer) {
		byte containerType = data[pointer++];
		assert(containerType == CONTAINER_TYPE);
		
		TinyString result = new TinyString();
		result.nameLength = TinyUtils.readShort(data, pointer);
		pointer += 2;
		result.name = TinyUtils.readString(data, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;
		
		result.size = TinyUtils.readInt(data, pointer);
		pointer += 4;
		
		result.count = TinyUtils.readInt(data, pointer);
		pointer += 4;
		
		result.characters = new char[result.count];
		TinyUtils.readChars(data, pointer, result.characters);
		
		pointer += result.count * Type.getSize(Type.CHAR);
		return result;
	}

}
