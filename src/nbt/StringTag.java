package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StringTag extends Tag {
	
	public String payload;
	private int len;

	public StringTag(InputStream in) throws IOException {
		len = Util.readShort(in);
		char[] array = new char[len];
		for (int i = 0; i < len; i++)
			array[i] = Tag.nextUTF8(in);
		payload = new String(array);
	}

	@Override
	public int size() {
		return 3 + nameLen + 2 + len;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_STRING);
		
			Util.writeShort(out, nameLen);
		
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeShort(out, len);
		
		out.write(payload.getBytes(StandardCharsets.UTF_8));
	}

}
