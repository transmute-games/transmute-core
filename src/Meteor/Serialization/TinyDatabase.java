package Meteor.Serialization;

import static Meteor.Serialization.TinyUtils.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TinyDatabase extends TinyBase {

	static final byte[] HEADER = "TDB".getBytes();
	static final short VERSION = 0x0100;
	static final byte CONTAINER_TYPE = ContainerType.DATABASE;
	private short objectCount;
	List<TinyObject> objects = new ArrayList<>();
	
	private TinyDatabase() {}
	
	public TinyDatabase(String name) {
		setName(name);
		
		size += HEADER.length + 2 + 1 + 2;
	}
	
	public void addObject(TinyObject object) {
		objects.add(object);
		size += object.getSize();
		
		objectCount = (short)objects.size();
	}
	
	public int getSize() {
		return size;
	}

	private int getBytes(byte[] dest, int pointer) {
		pointer = TinyUtils.writeBytes(dest, pointer, HEADER);
		pointer = TinyUtils.writeBytes(dest, pointer, VERSION);
		pointer = TinyUtils.writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		
		pointer = TinyUtils.writeBytes(dest, pointer, objectCount);
		for (TinyObject object : objects)
			pointer = object.getBytes(dest, pointer);
		
		return pointer;
	}
	
	private static TinyDatabase Deserialize(byte[] data) {
		int pointer = 0;
		assert(TinyUtils.readString(data, pointer, HEADER.length).equals(HEADER));
		pointer += HEADER.length;
		
		if (TinyUtils.readShort(data, pointer) != VERSION) {
			System.err.println("[Error]: Invalid TinyDatabase version.");
			return null;
		}
		pointer += 2;
		
		byte containerType = TinyUtils.readByte(data, pointer++);
		assert(containerType == CONTAINER_TYPE);
		
		TinyDatabase result = new TinyDatabase();
		result.nameLength = TinyUtils.readShort(data, pointer);
		pointer += 2;
		result.name = TinyUtils.readString(data, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;
		
		result.size = TinyUtils.readInt(data, pointer);
		pointer += 4;
		
		result.objectCount = TinyUtils.readShort(data, pointer);
		pointer += 2;
		
		for (int i = 0; i < result.objectCount; i++) {
			TinyObject object = TinyObject.Deserialize(data, pointer);
			result.objects.add(object);
			pointer += object.getSize(); 
		}
		
		return result;
	}
	
	public TinyObject findObject(String name) {
		for (TinyObject object : objects) {
			if (object.getName().equals(name))
				return object;
		}
		return null;
	}

	public static TinyDatabase DeserializeFromFile(String path) {
		byte[] buffer = null;
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
			buffer = new byte[stream.available()];
			stream.read(buffer);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Deserialize(buffer);
	}
	
	public void serializeToFile(String path) {
		byte[] data = new byte[getSize()];
		getBytes(data, 0);
		try {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(data);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
