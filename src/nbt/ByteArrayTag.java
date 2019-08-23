package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ByteArrayTag extends Tag {
	
	byte[] data;
	private int length;

	public ByteArrayTag(InputStream in) throws IOException {
		length = Util.readInt(in);
		data = new byte[length];
		for (int i = 0; i < length; i++)
			data[i] = (byte) in.read();
	}

	@Override
	public int size() {
		return 3 + nameLen + 4 + data.length;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_BYTE_ARRAY);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeInt(out, length);
		
		for (int i = 0; i < length; i++)
			out.write(data[i]);
	}

}
