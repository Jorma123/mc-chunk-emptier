package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LongTag extends Tag {
	
	public long value;
	
	public LongTag(long value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 8;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_LONG);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		Util.writeLong(out, value);
	}

}
