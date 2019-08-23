package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CompoundTag extends Tag {
	
	List<Tag> tagList = new ArrayList<Tag>();
	
	public CompoundTag(InputStream in) {
		Tag t;
		do {
			t = Tag.read(in);
			tagList.add(t);
		} while (!(t instanceof EndTag));
	}
	
	public Tag getTag(String name) {
		for (int i = 0; i < tagList.size(); i++) {
			Tag t = tagList.get(i), t0;
			if (t.name.equals(name))
				return tagList.get(i);
			else if (t instanceof CompoundTag)
				if ((t0 = ((CompoundTag) t).getTag(name)) != null)
					return t0;
			else if (t instanceof ListTag)
				if ((t0 = ((ListTag) t).getTag(name)) != null)
					return t0;
		}
		return null;
	}

	@Override
	public int size() {
		int s = 3 + nameLen;
		for (Tag t : tagList)
			s += t.size();
		return s;
	}

	@Override
	public void write(OutputStream out, boolean nameless) throws IOException {
		if (!nameless) {
			out.write(Tag.TAG_COMPOUND);
			
			Util.writeShort(out, nameLen);
			
			out.write(name.getBytes(StandardCharsets.UTF_8));
		}
		
		for (int i = 0; i < tagList.size(); i++)
			tagList.get(i).write(out, false);
	}

}
