package nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Util {
	
	private Util() { }
	
	public static int readShort(InputStream in) throws IOException {
		return (in.read() << 8) | in.read();
	}
	
	public static int readInt(InputStream in) throws IOException {
		return (in.read() << 24) |
				(in.read() << 16) |
				(in.read() << 8) |
				in.read();
	}
	
	public static long readLong(InputStream in) throws IOException {
		return (((long) in.read()) << 56) |
				(((long) in.read()) << 48) |
				(((long) in.read()) << 40) |
				(((long) in.read()) << 32) |
				(((long) in.read()) << 24) |
				(((long) in.read()) << 16) |
				(((long) in.read()) << 8) |
				((long) in.read());
	}
	
	public static void writeShort(OutputStream out, int s) throws IOException {
		out.write(s >> 8);
		out.write(s);
	}
	
	public static void writeInt(OutputStream out, int i) throws IOException {
		out.write(i >> 24);
		out.write(i >> 16);
		out.write(i >> 8);
		out.write(i);
	}
	
	public static void writeLong(OutputStream out, long l) throws IOException {
		out.write((int) (l >> 56));
		out.write((int) (l >> 48));
		out.write((int) (l >> 40));
		out.write((int) (l >> 32));
		out.write((int) (l >> 24));
		out.write((int) (l >> 16));
		out.write((int) (l >> 8));
		out.write((int) l);
	}

}
