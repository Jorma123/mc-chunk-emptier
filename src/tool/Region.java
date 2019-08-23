package tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import tool.Region.Location;

import nbt.CompoundTag;
import nbt.ListTag;
import nbt.LongArrayTag;
import nbt.StringTag;
import nbt.Tag;

public class Region {
	
	public static final Map<String, Color> tileMap = new HashMap<String, Color>();
	
	//private static final int LOCATIONS = 0;
	//private static final int TIMESTAMPS = 4096;
	//private static final int CHUNKS = 8192;
	
	private RandomAccessFile region;
	private Location[] locations = new Location[1024];
	private Chunk[] chunks = new Chunk[1024];
	
	public final int regX, regZ;
	
	static {
		Color grey = new Color(101, 101, 101), lGreen = new Color(30, 222, 80),
				dGreen = new Color(12, 125, 42), lBrown = new Color(255, 162, 31);
		
		tileMap.put("minecraft:air", Color.WHITE);
		tileMap.put("minecraft:stone", grey);
		tileMap.put("minecraft:cobblestone", grey);
		tileMap.put("minecraft:cobblestone_slab", grey);
		tileMap.put("minecraft:coal_ore", grey);
		tileMap.put("minecraft:iron_ore", grey);
		tileMap.put("minecraft:diorite", grey);
		tileMap.put("minecraft:andesite", grey);
		tileMap.put("minecraft:granite", grey);
		tileMap.put("minecraft:gravel", new Color(153, 137, 113));
		tileMap.put("minecraft:sand", new Color(250, 236, 40));
		tileMap.put("minecraft:snow", new Color(222, 222, 222));

		tileMap.put("minecraft:sugar_cane", lGreen);
		tileMap.put("minecraft:fern", lGreen);
		tileMap.put("minecraft:large_fern", lGreen);
		tileMap.put("minecraft:vine", lGreen);
		tileMap.put("minecraft:sweet_berry_bush", lGreen);
		tileMap.put("minecraft:grass", lGreen);
		tileMap.put("minecraft:tall_grass", lGreen);
		tileMap.put("minecraft:grass_block", lGreen);
		tileMap.put("minecraft:dandelion", lGreen);
		tileMap.put("minecraft:poppy", lGreen);
		tileMap.put("minecraft:blue_orchid", lGreen);
		tileMap.put("minecraft:azure_bluet", lGreen);
		tileMap.put("minecraft:red_tulip", lGreen);
		tileMap.put("minecraft:orange_tulip", lGreen);
		tileMap.put("minecraft:white_tulip", lGreen);
		tileMap.put("minecraft:pink_tulip", lGreen);
		tileMap.put("minecraft:oxeye_daisy", lGreen);
		tileMap.put("minecraft:cornflower", lGreen);
		tileMap.put("minecraft:lily_of_the_valley", lGreen);
		tileMap.put("minecraft:wither_rose", lGreen);
		tileMap.put("minecraft:sunflower", lGreen);
		tileMap.put("minecraft:lilac", lGreen);
		tileMap.put("minecraft:rose_bush", lGreen);
		tileMap.put("minecraft:peony", lGreen);
		
		tileMap.put("minecraft:dirt", new Color(92, 45, 17));
		tileMap.put("minecraft:pumpkin", new Color(214, 107, 0));

		//tileMap.put("minecraft:leaves", new Color(36, 200, 80));
		tileMap.put("minecraft:acacia_leaves", dGreen);
		tileMap.put("minecraft:birch_leaves", dGreen);
		tileMap.put("minecraft:dark_oak_leaves", dGreen);
		tileMap.put("minecraft:jungle_leaves", dGreen);
		tileMap.put("minecraft:oak_leaves", dGreen);
		tileMap.put("minecraft:spruce_leaves", dGreen);
		
		tileMap.put("minecraft:oak_planks", lBrown);
		tileMap.put("minecraft:oak_slab", lBrown);
		tileMap.put("minecraft:oak_stairs", lBrown);
		tileMap.put("minecraft:birch_planks", lBrown);
		tileMap.put("minecraft:birch_slab", lBrown);
		tileMap.put("minecraft:birch_stairs", lBrown);
		tileMap.put("minecraft:jungle_planks", lBrown);
		tileMap.put("minecraft:jungle_slab", lBrown);
		tileMap.put("minecraft:jungle_stairs", lBrown);
		tileMap.put("minecraft:spruce_planks", lBrown);
		tileMap.put("minecraft:spruce_slab", lBrown);
		tileMap.put("minecraft:spruce_stairs", lBrown);
		tileMap.put("minecraft:dark_oak_planks", lBrown);
		tileMap.put("minecraft:dark_oak_slab", lBrown);
		tileMap.put("minecraft:dark_oak_stairs", lBrown);
		tileMap.put("minecraft:acacia_planks", lBrown);
		tileMap.put("minecraft:acacia_slab", lBrown);
		tileMap.put("minecraft:acacia_stairs", lBrown);
		
		tileMap.put("minecraft:seagrass", Color.BLUE);
		tileMap.put("minecraft:tall_seagrass", Color.BLUE);
		tileMap.put("minecraft:kelp", Color.BLUE);
		tileMap.put("minecraft:lily_pad", Color.BLUE);
		tileMap.put("minecraft:water", Color.BLUE);
		tileMap.put("minecraft:flowing_water", Color.BLUE);
		tileMap.put("minecraft:lava", Color.RED);
		tileMap.put("minecraft:flowing_lava", Color.RED);
	}
	
	public Region(File regionFile, int regX, int regZ) throws FileNotFoundException {
		this.regX = regX;
		this.regZ = regZ;
		
		region = new RandomAccessFile(regionFile, "rw");
		
		try {
			for (int i = 0; i < locations.length; i++) {
				locations[i] = new Location(region);
				if (locations[i].offset >= 2) {
					long l = region.getFilePointer();
					locations[i].locate(region);
					try {
						//System.out.println("Reg x: " + regX + " Reg z: " + regZ + " Chunk: " + (i/32) + " " + (i%32));
						chunks[i] = new Chunk(region, this, i);
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
					region.seek(l);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i] != null) {
				int newLen = chunks[i].save(locations[i]);
				if (newLen >= 0) {
					try {
						region.seek(i * 4 + 3);
						region.write(newLen);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public Chunk getChunk(int x, int z) {
		return chunks[z*32 + x];
	}
	
	public String[][] regionTopBlocks() {
		return null;
	}
	
	public void close() {
		try {
			region.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Location {
		
		public final int offset;
		public final int sectorCount;
		
		public Location(RandomAccessFile ra) throws IOException {
			int i = ra.readInt();
			offset = i >>> 8;
			sectorCount = i & 0xFF;
		}
		
		public void locate(RandomAccessFile ra) throws IOException {
			ra.seek(offset * 4096);
		}
		
	}

}

class Chunk {
	
	private Region region;
	private RandomAccessFile file;
	private int location;
	
	private int length;
	//private int compression;
	private byte[] data;
	private byte[] uncompressed;
	
	//private int uncompressedLen;
	private CompoundTag rootTag;
	
	//private CompoundTag heightmaps;
	
	public int[][] hMap = new int[16][16];
	private String[][] idMap = new String[16][16];
	private BufferedImage img;
	
	private boolean unsavedChange = false;
	
	public Chunk(RandomAccessFile ra, Region region, int location) throws IOException, DataFormatException {
		this.region = region;
		file = ra;
		this.location = location;
		
		length = ra.readInt();
		/*compression = */ra.read();
		data = new byte[length-1];
		ra.readFully(data);
		
		uncompressed = new byte[2048];
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		long previousRead = 0;
		while (inflater.getBytesRead() < data.length) {
			inflater.inflate(uncompressed, (int) inflater.getBytesWritten(), (int) (uncompressed.length - inflater.getBytesWritten()));
			if (inflater.getBytesRead() == previousRead) {
				if (uncompressed.length >= 1024 * 1024)
					break;
				byte[] newArray = new byte[2*uncompressed.length];
				System.arraycopy(uncompressed, 0, newArray, 0, uncompressed.length);
				uncompressed = newArray;
			}
			previousRead = inflater.getBytesRead();
		}
		//uncompressedLen = (int) inflater.getBytesWritten();
		//System.out.println(uncompressedLen);
		inflater.end();
		
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(uncompressed);
		rootTag = (CompoundTag) Tag.read(inputStream);
		
		topDownImage();
	}
	
	private void topDownImage() {
		for (String[] array : idMap)
			for (int i = 0; i < array.length; i++)
				array[i] = "minecraft:air";
		
		for (int[] array : hMap)
			Arrays.fill(array, 0);
		
		CompoundTag level = (CompoundTag) rootTag.getTag("Level");
		
		Tag[] list = ((ListTag) level.getTag("Sections")).payload;
		CompoundTag[] sections = new CompoundTag[list.length];
		for (int i = 0; i < sections.length; i++)
			sections[i] = (CompoundTag) list[i];
			
		for (int i0 = sections.length - 1; i0 >= 0; i0--) {
			CompoundTag section = sections[i0];
			
			ListTag palette = (ListTag) section.getTag("Palette");
			
			if (palette == null)
				continue;
			
			String[] ids = new String[palette.payload.length];
			int airId = -1, caveAirId = -1;
			for (int i = 0; i < ids.length; i++) {
				ids[i] = ((StringTag) ((CompoundTag) palette.payload[i]).getTag("Name")).payload;
				//System.out.println(ids[i]);
				if (ids[i].equals("minecraft:air"))
					airId = i;
				else if (ids[i].equals("minecraft:cave_air"))
					caveAirId = i;
			}
			
			long[] l = ((LongArrayTag) section.getTag("BlockStates")).data;
			int bitsPerIndex = l.length * 64 / 4096;
			BitStream stream = new BitStream(l);
			
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					if ("minecraft:air".equals(idMap[z][x])) {
						for (int y = 15; y >= 0; y--) {
							stream.jump(bitsPerIndex * (256*y + 16*z + x));
							int index = 0;
							for (int i = 0; i < bitsPerIndex; i++)
								index = index | (stream.readBit() << i);
							if (index != airId && index != caveAirId) {
								idMap[z][x] = ids[index];
								hMap[z][x] = i0 * 16 + y;
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public Image getImage() {
		if (img == null) {
			img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			int cx = location % 32;
			int cz = location / 32;
			
			for (int z1 = 0; z1 < 16; z1++) {
				for (int x1 = 0; x1 < 16; x1++) {
					Color c = Region.tileMap.get(idMap[z1][x1]);
					
					if (c == null)
						c = Color.WHITE;
					
					int shade = 0;
					int left = hMap[z1][x1];
					int bottom = left;
					
					if (x1 > 0)
						left = hMap[z1][x1-1];
					else if (cx > 0 && (region.getChunk(cx - 1, cz) != null))
						left = region.getChunk(cx - 1, cz).hMap[z1][15];
					
					if (z1 < 15)
						bottom = hMap[z1+1][x1];
					else if (cz < 31 && (region.getChunk(cx, cz + 1) != null))
						bottom = region.getChunk(cx, cz + 1).hMap[0][x1];
						
					shade = (left + bottom) / 2 - hMap[z1][x1];
					shade = shade * 25 + 10;
					int r = c.getRed() - shade;
					int g = c.getGreen() - shade;
					int b = c.getBlue() - shade;
					if (r < 0) r = 0; else if (r > 255) r = 255;
					if (g < 0) g = 0; else if (g > 255) g = 255;
					if (b < 0) b = 0; else if (b > 255) b = 255;
					img.setRGB(x1, z1, (r << 16) | (g << 8) | b);
				}
			}
		}
		return img;
	}
	
	public void update() {
		topDownImage();
		img = null;
	}
	
	public void empty() {
		unsavedChange = true;
		
		CompoundTag level = (CompoundTag) rootTag.getTag("Level");
		ListTag sections = (ListTag) level.getTag("Sections");
		
		CompoundTag heightmaps = (CompoundTag) level.getTag("Heightmaps");
		LongArrayTag h1, h2, h3, h4;
		h1 = h2 = h3 = h4 = null;
		if (heightmaps != null) {
			h1 = (LongArrayTag) heightmaps.getTag("MOTION_BLOCKING");
			h2 = (LongArrayTag) heightmaps.getTag("MOTION_BLOCKING_NO_LEAVES");
			h3 = (LongArrayTag) heightmaps.getTag("OCEAN_FLOOR");
			h4 = (LongArrayTag) heightmaps.getTag("WORLD_SURFACE");
		}
		
		if (sections != null) sections.empty();
		if (h1 != null) h1.zero();
		if (h2 != null) h2.zero();
		if (h3 != null) h3.zero();
		if (h4 != null) h4.zero();
	}
	
	public int save(Location location) {
		if (!unsavedChange)
			return -1;
		ByteArrayOutputStream stream = new ByteArrayOutputStream(rootTag.size());
		try {
			rootTag.write(stream, false);
		} catch (IOException ex) {
			ex.printStackTrace();
			return -1;
		}
		byte[] compressed = new byte[stream.size()];
		Deflater deflater = new Deflater();
		deflater.setInput(stream.toByteArray());
		deflater.finish();
		int compressedLength = 0;
		int c;
		do {
			c = deflater.deflate(compressed, compressedLength, compressed.length - compressedLength);
			compressedLength += c;
		} while (c > 0);
		deflater.end();
		
		try {
			int padding = location.sectorCount * 4096 - compressedLength - 5;
			location.locate(file);
			
			file.writeInt(compressedLength + 1);
			
			// Compression method, 2 = zlib
			file.write(2);
			
			file.write(compressed, 0, compressedLength);
			while (padding-- > 0)
				file.write(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return (compressedLength + 5 + 4095) / 4096;
	}
	
	class BitStream {
		
		private long[] data;
		private int index;
		
		public BitStream(long[] buffer) {
			data = buffer;
			index = 0;
		}
		
		public void jump(int bitIndex) {
			index = bitIndex;
		}
		
		public int readBit() {
			//long l = data[index/64];
			//l = (l >>> 56) | ((l >>> 40) & 0xFF00) | ((l >>> 24) & 0xFF0000) | ((l >>> 8) & 0xFF000000) |
			//	((l & 0xFF000000) << 8) | ((l & 0xFF0000) << 24) | ((l & 0xFF00) << 40) | (l << 56);
			int bit = (int) ((data[index/64] >>> (index % 64)) & 1);
			index++;
			return bit;
		}
		
	}
	
}
