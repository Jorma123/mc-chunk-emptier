package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FloatTag extends Tag {
	
	public float value;
	
	public FloatTag(float value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 4;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_FLOAT);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		int bits = Float.floatToIntBits(value);
		Util.writeInt(out, bits);
	}

}
