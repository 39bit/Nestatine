package tk.harpseal.nes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class NESCanvas extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7880867418849092046L;
	
	private static BufferedImage frame = null;
  
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.drawImage(frame, 0, 0, getSize().width, getSize().height, 0, 0, 256, 224, null);
	}
	
	public void submitNewFrame(BufferedImage f) {
		frame = f;
	}

	public Dimension getPreferredSize() {
		return new Dimension(256, 224);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
           