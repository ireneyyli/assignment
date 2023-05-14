package shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.rmi.RemoteException;

import rmi.Client;

public class WBPen implements IShape {
	public WBPen(int startX, int startY, int endX, int endY, Color color) {
		super();
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.color = color;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private Color color;
	
	@Override
	public void addToGraphics(Graphics g) {
		g.setColor(color);
    	Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3)); // 設置筆觸寬度為3個像素
    	g2.drawLine(startX, startY, endX, endY);
	};
	
	@Override
	public void sendToClient(Client.IClientRO client) throws RemoteException {
		client.drawClientPen(startX, startY, endX, endY, color);
	};
}
