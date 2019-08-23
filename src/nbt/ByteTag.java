package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ByteTag extends Tag {
	
	public byte value;
	
	public ByteTag(byte value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 1;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_BYTE);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		out.write(value);
	}

}
