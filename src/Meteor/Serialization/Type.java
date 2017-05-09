package Meteor.Serialization;

class Type {
	static final byte UNKNOWN = 0;
	static final byte BYTE 	= 1;
	static final byte SHORT = 2;
	static final byte CHAR 	= 3;
	static final byte INTEGER = 4;
	static final byte LONG = 5;
	static final byte FLOAT	= 6;
	static final byte DOUBLE = 7;
	static final byte BOOLEAN = 8;
	
	static int getSize(byte type) {
		switch (type) {
		case BYTE:		return 1;
		case SHORT:		return 2;
		case CHAR:		return 2;
		case INTEGER:	return 4;
		case LONG:		return 8;
		case FLOAT:		return 4;
		case DOUBLE:	return 8;
		case BOOLEAN:	return 1;
		}

		return 0;
	}
	
}
