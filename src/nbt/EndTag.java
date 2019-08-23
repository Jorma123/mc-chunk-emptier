package nbt;

import java.io.IOException;
import java.io.OutputStream;

public class EndTag extends Tag {

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		out.write(Tag.TAG_END);
	}

}
