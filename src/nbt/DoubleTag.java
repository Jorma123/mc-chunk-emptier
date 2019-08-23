package nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DoubleTag extends Tag {
	
	public double value;
	
	public DoubleTag(double value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 3 + nameLen + 8;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_DOUBLE);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		long bits = Double.doubleToLongBits(value);
		Util.writeLong(out, bits);
	}

}
