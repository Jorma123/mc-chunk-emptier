package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Tag {
	
	public static final int TAG_END = 0;
	public static final int TAG_BYTE = 1;
	public static final int TAG_SHORT = 2;
	public static final int TAG_INT = 3;
	public static final int TAG_LONG = 4;
	public static final int TAG_FLOAT = 5;
	public static final int TAG_DOUBLE = 6;
	public static final int TAG_BYTE_ARRAY = 7;
	public static final int TAG_STRING = 8;
	public static final int TAG_LIST = 9;
	public static final int TAG_COMPOUND = 10;
	public static final int TAG_INT_ARRAY = 11;
	public static final int TAG_LONG_ARRAY = 12;
	
	public String name = "";
	int nameLen;
	
	Tag() {
	}
	
	public static Tag read(InputStream in) {
		try {
			int id = in.read();
			int len = 0;
			char[] array = null;
			if (id != 0) {
				len = Util.readShort(in);
				array = new char[len];
				for (int i = 0; i < len; i++)
					array[i] = nextUTF8(in);
			}
			Tag t;
			switch (id) {
			case TAG_END:
				return new EndTag();
			case TAG_BYTE:
				t = new ByteTag((byte) in.read());
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_SHORT:
				t = new ShortTag((short) Util.readShort(in));
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_INT:
				t = new IntTag(Util.readInt(in));
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_LONG:
				t = new LongTag(Util.readLong(in));
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_FLOAT:
				t = new FloatTag(Float.intBitsToFloat(Util.readInt(in)));
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_DOUBLE:
				t = new DoubleTag(Double.longBitsToDouble(Util.readLong(in)));
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_BYTE_ARRAY:
				t = new ByteArrayTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_STRING:
				t = new StringTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_LIST:
				t = new ListTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_COMPOUND:
				t = new CompoundTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_INT_ARRAY:
				t = new IntArrayTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			case TAG_LONG_ARRAY:
				t = new LongArrayTag(in);
				t.name = new String(array);
				t.nameLen = len;
				return t;
			default:
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static char nextUTF8(InputStream in) throws IOException {
		int nextByte = in.read();
		if ((nextByte >>> 7) == 0) {
			return (char) (nextByte & 0x7F);
		} else if ((nextByte >>> 5) == 0b110) {
			return (char) (((nextByte & 0x1F) << 6) |
							(in.read() & 0x3F));
		} else if ((nextByte >>> 4) == 0b1110) {
			return (char) (((nextByte & 0xF) << 12) |
							((in.read() & 0x3F) << 6) |
							(in.read() & 0x3F));
		} else if ((nextByte >>> 3) == 0b11110) {
			return (char) (((nextByte & 0x7) << 18) |
					((in.read() & 0x3F) << 12) |
					((in.read() & 0x3F) << 6) |
					(in.read() & 0x3F));
		}
		return 0;
	}
	
	public abstract int size();
	
	public abstract void write(OutputStream out, boolean nameless) throws IOException;
	
}
