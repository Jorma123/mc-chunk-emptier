package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Panel extends JPanel implements MouseListener, MouseMotionListener, WindowListener, ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2972302101004133793L;
	private static JFileChooser fc = new JFileChooser(System.getenv("APPDATA") + "\\.minecraft\\saves\\");
	
	private Region[][] regions = new Region[32][32];
	//private final Color sky = new Color(66, 135, 245);
	private final Color outlineColor = new Color(0, 153, 224);
	private final Color fillColor = new Color(0, 153, 224, 100);
	//private int zoom = 0;
	private int mxStart;
	private int myStart;
	private int originX = 0;
	private int originY = 0;
	private int originDx = 0;
	private int originDy = 0;
	private int selX, selY;
	private boolean drawSelection = false;
	private List<Selection> selections = new LinkedList<Selection>();
	private int lastSelX, lastSelY;
	private boolean selFlag = false;
	
	static {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	public Panel() {
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(500, 500));
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			JFrame frame = new JFrame();
			Panel content = new Panel();
			
			frame.add(content);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setTitle("Minecraft chunk emptier");
			
			frame.addWindowListener(content);
			
			
			JMenuBar menuBar = new JMenuBar();
			
			JMenu menu = new JMenu("File");
			
			JMenuItem menuItem = new JMenuItem("Open...");
			menuItem.addActionListener(content);
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Save");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menuItem.addActionListener(content);
			menu.add(menuItem);
			
			menu.addSeparator();
			
			menuItem = new JMenuItem("Exit");
			menuItem.addActionListener(content);
			menu.add(menuItem);
			
			menuBar.add(menu);
			
			menu = new JMenu("Edit");
			
			menuItem = new JMenuItem("Undo");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
			menuItem.addActionListener(content);
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Delete");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			menuItem.addActionListener(content);
			menu.add(menuItem);
			
			menuBar.add(menu);
			
			
			frame.setJMenuBar(menuBar);
			
			frame.setVisible(true);
		}
	}
	
	private void loadWorld(File regionDirectory) {
		for (int z = -16; z < 16; z++) {
			for (int x = -16; x < 16; x++) {
				File file = new File(regionDirectory, "r." + x + "." + z + ".mca");
				if (file.exists()) {
					try {
						if (regions[z+16][x+16] != null)
							regions[z+16][x+16].close();
						regions[z+16][x+16] = new Region(file, x, z);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void selectA(int x, int y) {
		x = translateX2(x);
		y = translateY2(y);
		lastSelX = (x - (x >>> 31) * 15) / 16;
		lastSelY = (y - (y >>> 31) * 15) / 16;
		selFlag = true;
	}
	
	private void selectB(int x, int y) {
		x = translateX2(x);
		y = translateY2(y);
		int ax = Math.min(lastSelX, (x - (x >>> 31) * 15) / 16);
		int bx = Math.max(lastSelX, (x - (x >>> 31) * 15) / 16);
		int ay = Math.min(lastSelY, (y - (y >>> 31) * 15) / 16);
		int by = Math.max(lastSelY, (y - (y >>> 31) * 15) / 16);
		selections.add(new Selection(ax, ay, bx, by));
		selFlag = false;
		repaint();
	}
	
	private int translateX(int x) {
		return x - ((originX + originDx) % 16);
	}
	
	private int translateY(int y) {
		return y - ((originY + originDy) % 16);
	}
	
	private int translateX2(int x) {
		return x - (originX + originDx) - (getWidth() + 15) / 32 * 16;
	}
	
	private int translateY2(int y) {
		return y - (originY + originDy) - (getHeight() + 15) / 32 * 16;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int dx = originX + originDx;
		int dy = originY + originDy;
		int chunksX = (getWidth() + 15) / 16;
		int chunksZ = (getHeight() + 15) / 16;

		int cx = -(chunksX / 2) - dx / 16;
		int cz = -(chunksZ / 2) - dy / 16;
		for (int i = 0; i < chunksZ; i++) {
			for (int j = 0; j < chunksX; j++) {
				int regX = (cx + j - ((cx + j) >>> 31) * 31) / 32;
				int regZ = (cz + i - ((cz + i) >>> 31) * 31) / 32;
				Region owner = regions[regZ+16][regX+16];
				if (owner != null) {
					Chunk chunk = owner.getChunk((cx + j) - (regX * 32), (cz + i) - (regZ * 32));
					if (chunk != null)
						g.drawImage(chunk.getImage(),
								j * 16 + (dx % 16),
								i * 16 + (dy % 16),
								null);
				}
			}
		}
		
		if (drawSelection) {
			int x = selX * 16 + (dx % 16);
			int y = selY * 16 + (dy % 16);
			highlight(g, x, y);
		}
		
		for (Selection sel : selections) {
			for (int y = sel.start.y; y <= sel.end.y; y++) {
				for (int x = sel.start.x; x <= sel.end.x; x++) {
					int x0 = x * 16 + dx + (chunksX / 2) * 16;
					int y0 = y * 16 + dy + (chunksZ / 2) * 16;
					if (x0 >= 0 && y0 >= 0 && x0 < getWidth() && y0 < getHeight())
						highlight(g, x0, y0);
				}
			}
		}
		
		if (selFlag) {
			int startX = Math.min(lastSelX, selX - ((originX + originDx) / 16) - ((getWidth() + 15) / 32));
			int endX = Math.max(lastSelX, selX - ((originX + originDx) / 16) - ((getWidth() + 15) / 32));
			int startY = Math.min(lastSelY, selY - ((originY + originDy) / 16) - ((getHeight() + 15) / 32));
			int endY = Math.max(lastSelY, selY - ((originY + originDy) / 16) - ((getHeight() + 15) / 32));
			for (int x = startX; x <= endX; x++) {
				for (int y = startY; y <= endY; y++) {
					int x0 = x * 16 + dx + (chunksX / 2) * 16;
					int y0 = y * 16 + dy + (chunksZ / 2) * 16;
					if (x0 >= 0 && y0 >= 0 && x0 < getWidth() && y0 < getHeight())
						highlight(g, x0, y0);
				}
			}
		}
	}
	
	private void highlight(Graphics g, int x, int y) {
		g.setColor(outlineColor);
		g.drawRect(x - 1, y - 1, 17, 17);
		g.setColor(fillColor);
		g.fillRect(x, y, 16, 16);
	}
	
	private void close() {
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 32; j++)
				if (regions[i][j] != null)
					regions[i][j].close();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)
			mouseMoved(e);
		if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK) {
			int originDx = e.getX() - mxStart;
			int originDy = e.getY() - myStart;
			this.originDx = originDx;
			this.originDy = originDy;
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = translateX(e.getX());
		int y = translateY(e.getY());
		boolean doRepaint = false;
		if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
			if ((x / 16) != selX || (y / 16) != selY)
				doRepaint = true;
			selX = x / 16;
			selY = y / 16;
			drawSelection = true;
		} else {
			drawSelection = false;
		}
		if (doRepaint)
			repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			selectA(e.getX(), e.getY());
		if (e.getButton() == MouseEvent.BUTTON2) {
			mxStart = e.getX();
			myStart = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			selectB(e.getX(), e.getY());
		if (e.getButton() == MouseEvent.BUTTON2) {
			originX += originDx;
			originY += originDy;
			originDx = 0;
			originDy = 0;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	class Selection {
		
		public final Point start;
		public final Point end;
		
		public Selection(int x0, int y0, int x1, int y1) {
			start = new Point(x0, y0);
			end = new Point(x1, y1);
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		close();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand().toLowerCase()) {
		case "open...":
			int open = fc.showOpenDialog(this);
			
			if (open == JFileChooser.CANCEL_OPTION || open == JFileChooser.ERROR_OPTION)
				break;
			
			File selectedFile = fc.getSelectedFile();
			File regionFile = new File(selectedFile, "region");
			
			if (regionFile.exists())
				loadWorld(regionFile);
			else
				loadWorld(selectedFile);
			break;
		case "save":
			for (int z = 0; z < 32; z++) {
				for (int x = 0; x < 32; x++) {
					if (regions[z][x] != null)
						regions[z][x].save();
				}
			}
			break;
		case "undo":
			if (selections.size() > 0) {
				selections.remove(selections.size() - 1);
				repaint();
			}
			break;
		case "delete":
			for (Selection sel : selections) {
				for (int y = sel.start.y; y <= sel.end.y; y++) {
					for (int x = sel.start.x; x <= sel.end.x; x++) {
						int regX = (x - (x >>> 31) * 31) / 32;
						int regZ = (y - (y >>> 31) * 31) / 32;
						Region owner = regions[regZ+16][regX+16];
						if (owner != null) {
							Chunk chunk = owner.getChunk(x - (regX * 32), y - (regZ * 32));
							if (chunk != null) {
								chunk.empty();
								chunk.update();
							}
						}
					}
				}
			}
			selections.clear();
			repaint();
			break;
		case "exit":
			close();
			System.exit(0);
		default:
			System.err.println("Uncaught actionevent.");
		}
	}

}
