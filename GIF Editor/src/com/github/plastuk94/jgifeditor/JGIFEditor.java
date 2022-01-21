package com.github.plastuk94.jgifeditor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class JGIFEditor extends JFrame {
	
	public static void main(String[] args) {
		new JGIFEditor();
	}
	
	public JGIFEditor() {
		setVisible(true);
		Graphics2D graphics = (Graphics2D) this.getGraphics();
		System.out.println(graphics.toString());
		JMenuItem open = new JMenuItem("Open");
		JMenuItem reverseGIF = new JMenuItem("Reverse GIF");
		open.addActionListener(e -> {
				String openPath = fileMenu(JFileChooser.OPEN_DIALOG);
				drawImage(graphics, openPath);
				reverseGIF.addActionListener(f -> {
					try {
						reverseGIF(openPath);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
		});

		JMenuItem save = new JMenuItem("Save");

		JMenu fileMenu = new JMenu("File");
		JMenu effectsMenu = new JMenu("Effects");
		effectsMenu.add(reverseGIF);
		fileMenu.add(open);
		fileMenu.add(save);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(effectsMenu);
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle maxScreenSize = new Rectangle(resolution);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setMaximizedBounds(maxScreenSize);
		setJMenuBar(menuBar);
		setTitle("GIF Editor");
		DropTarget dt = new DropTarget(this, new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
			}
			@Override
			public void dragExit(DropTargetEvent dte) {
			}
			@Override
			public void dragOver(DropTargetDragEvent dtde) {
			}
			@Override
			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				try {
					File[] dropFiles = (File[]) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

		});

	}

	public void drawImage(Graphics2D graphics, String path) {
		ImageIcon icon;
		icon = new ImageIcon(path);
		JLabel label = new JLabel(icon);
		this.getContentPane().removeAll();
		this.getContentPane().add(label);
		this.pack();
	}
	public void drawImage(Graphics2D graphics, Image image) {
		ImageIcon icon = new ImageIcon();
		icon.setImage(image);
		JLabel label = new JLabel(icon);
		this.getContentPane().removeAll();
		this.getContentPane().add(label);
		this.pack();
	}

	public String fileMenu(int openOrSave) {
		JFileChooser fileMenu = new JFileChooser();
		String filePath = "";
		fileMenu.setMultiSelectionEnabled(false);
		fileMenu.setCurrentDirectory(new File(System.getProperty("user.home")));

		int r = 0;

		switch (openOrSave) {
		case JFileChooser.SAVE_DIALOG:
			r = fileMenu.showSaveDialog(fileMenu);
			break;
		case JFileChooser.OPEN_DIALOG:
			r = fileMenu.showOpenDialog(fileMenu);
			break;
		default:
			r = fileMenu.showOpenDialog(fileMenu);
		}
		
		if (r == JFileChooser.APPROVE_OPTION) {
			filePath = fileMenu.getSelectedFile().getAbsolutePath();
			System.out.println("Selected file: "+filePath);
			return fileMenu.getSelectedFile().getAbsolutePath();
		}
		return filePath;
	}
	
	public void reverseGIF(String path) throws IOException {
		File newImage = new File("image.gif");
		ImageReader reader = ImageIO.getImageReadersByFormatName("GIF").next();
		ImageWriter writer = ImageIO.getImageWritersByFormatName("GIF").next();
		try {
			System.out.println(path);
			ImageInputStream in = ImageIO.createImageInputStream(new File(path));
			ImageOutputStream out = ImageIO.createImageOutputStream(newImage);
			reader.setInput(in);
			writer.setOutput(out); 
			writer.prepareWriteSequence(null);
			int count = reader.getNumImages(true);
			for (int i = count - 1; i > 0; i--) {
				IIOMetadata metadata = reader.getImageMetadata(0); // Grab metadata from the first frame, so it loops correctly.
				BufferedImage imageFrame = reader.read(i);
				IIOImage iioImage = new IIOImage(imageFrame, null, metadata);
				writer.writeToSequence(iioImage, null);
			}
			writer.endWriteSequence();
			this.drawImage((Graphics2D)this.getRootPane().getGraphics(),"image.gif");
			newImage.delete();
			out.flush();
			out.close();
			in.flush();
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to print image");
		}
	}
}
/*class Toolbar extends JToolBar {
	private Toolbar(Component parent) {
		
	}
}*/
