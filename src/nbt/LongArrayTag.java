package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LongArrayTag extends Tag {
	
	public long[] data;
	private int length;

	public LongArrayTag(InputStream in) throws IOException {
		length = Util.readInt(in);
		data = new long[length];
		for (int i = 0; i < length; i++)
			data[i] = Util.readLong(in);
	}
	
	public void zero() {
		Arrays.fill(data, 0);
	}

	@Override
	public int size() {
		return 3 + nameLen + 4 + data.length * 8;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_LONG_ARRAY);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeInt(out, length);
		
		for (int i = 0; i < length; i++)
			Util.writeLong(out, data[i]);
	}

}
