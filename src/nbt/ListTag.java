package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ListTag extends Tag {
	
	private static final Tag[] THE_EMPTY_LIST = new Tag[0];
	
	private int type;
	private int length;
	public Tag[] payload;

	public ListTag(InputStream in) throws IOException {
		type = in.read();
		length = Util.readInt(in);
		payload = new Tag[length];
		for (int i = 0; i < length; i++) {
			switch (type) {
			case TAG_END: // TAG_End
				payload[i] = new EndTag();
				break;
			case TAG_BYTE: // TAG_Byte
				payload[i] = new ByteTag((byte) in.read());
				break;
			case TAG_SHORT: // TAG_Short
				payload[i] = new ShortTag((short) Util.readShort(in));
				break;
			case TAG_INT: // TAG_Int
				payload[i] = new IntTag(Util.readInt(in));
				break;
			case TAG_LONG: // TAG_Long
				payload[i] = new LongTag(Util.readLong(in));
				break;
			case TAG_FLOAT: // TAG_Float
				payload[i] = new FloatTag(Float.intBitsToFloat(Util.readInt(in)));
				break;
			case TAG_DOUBLE: // TAG_Double
				payload[i] = new DoubleTag(Double.longBitsToDouble(Util.readLong(in)));
				break;
			case TAG_BYTE_ARRAY: // TAG_Byte_Array
				payload[i] = new ByteArrayTag(in);
				break;
			case TAG_STRING: // TAG_String
				payload[i] = new StringTag(in);
				break;
			case TAG_LIST: // TAG_List
				payload[i] = new ListTag(in);
				break;
			case TAG_COMPOUND: // TAG_Compound
				payload[i] = new CompoundTag(in);
				break;
			case TAG_INT_ARRAY: // TAG_Int_Array
				payload[i] = new IntArrayTag(in);
				break;
			case TAG_LONG_ARRAY: // TAG_Long_Array
				payload[i] = new LongArrayTag(in);
				break;
			}
		}
	}

	public Tag getTag(String name) {
		if (type == TAG_COMPOUND) {
			for (int i = 0; i < length; i++) {
				Tag t = ((CompoundTag) payload[i]).getTag(name);
				if (t != null)
					return t;
			}
		}
		return null;
	}
	
	public void empty() {
		length = 0;
		payload = THE_EMPTY_LIST;
	}

	@Override
	public int size() {
		int s = 3 + nameLen + 5;
		for (Tag t : payload)
			s += t.size();
		return s;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_LIST);
		
			Util.writeShort(out, nameLen);
		
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		out.write(type);
		
		Util.writeInt(out, length);
		
		for (int i = 0; i < length; i++)
			payload[i].write(out, true);
	}

}
