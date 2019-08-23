package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ShortTag extends Tag {

	public short value;
	
	public ShortTag(short value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 2;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_SHORT);
		
			Util.writeShort(out, nameLen);
		
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeShort(out, value);
	}
	
}
