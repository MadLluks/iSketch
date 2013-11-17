package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JButton;
import javax.swing.JPanel;

import controler.Client;

public class DrawListener implements MouseListener, MouseMotionListener {

	private Client client;
	private GraphicalView view;
	private Point p1, p2;
	
	public DrawListener(Client client, GraphicalView view){
		this.client = client;
		this.view = view;
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof JButton){
			JButton btn = (JButton)e.getSource();
			switch(btn.getText()) {
			case "Rejoindre":
				this.view.openConnectionFrame();
				break;
			case "Entrer":
				String infos = this.view.readConnectionInfoFromUser();
				ConnectDialog conn = this.view.getConnectDial();
				if(conn.getPortTxt().getBackground() != Color.RED &&
						conn.getHostTxt().getBackground() != Color.RED &&
						conn.getNameTxt().getBackground() != Color.RED){
					this.view.closeConnectionFrame();
					this.view.getConnectBtn().setEnabled(false);
					this.view.getLogBtn().setEnabled(false);
					this.view.getDisconnectBtn().setEnabled(true);
					this.client.newConnection(infos);
				}
				break;
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
		if(e.getSource() instanceof JPanel){
			JPanel panel = (JPanel)e.getSource();
			this.view.consoleView(panel.getName());
			this.view.setCoord(e.getPoint(), true);
			p1 = new Point(e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getSource() instanceof JPanel){
			JPanel panel = (JPanel)e.getSource();
			if(e.getX() <= panel.getWidth()){
				this.view.consoleView(panel.getName());
				this.view.setCoord(e.getPoint(), false);
				p2 = new Point(e.getX(), e.getY());
				panel.paint(this.view.getBufferedImage().getGraphics());
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if(e.getSource() instanceof JPanel){
			JPanel panel = (JPanel)e.getSource();
			Graphics2D g = (Graphics2D)panel.getGraphics();
			panel.update(g);
			g.drawImage(this.view.getBufferedImage(), 0, 0, panel);
			g.drawLine((int)p1.getX(), (int)p1.getY(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
}
