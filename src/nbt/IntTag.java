package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IntTag extends Tag {
	
	public int value;
	
	public IntTag(int value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 4;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_INT);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeInt(out, value);
	}

}
