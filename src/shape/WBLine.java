package shape;

import java.awt.Color;
import java.awt.Graphics;
import java.rmi.RemoteException;

import rmi.Client.IClientRO;

public class WBLine implements IShape {
	public WBLine(int startX, int startY, int endX, int endY, Color color) {
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
		// TODO Auto-generated method stub
    	g.setColor(color);
    	g.drawLine(startX, startY, endX, endY);
	}

	@Override
	public void sendToClient(IClientRO client) throws RemoteException {
		// TODO Auto-generated method stub
		client.drawClientLine(startX, startY, endX, endY, color);
	}

}
