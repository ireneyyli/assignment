package shape;

import java.awt.Color;
import java.awt.Graphics;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

import rmi.IClientRO;

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

	public void addToGraphics(Graphics g) {
		// TODO Auto-generated method stub
    	g.setColor(color);
    	g.drawLine(startX, startY, endX, endY);
	}

	public void sendToClient(IClientRO client) throws RemoteException {
		// TODO Auto-generated method stub
		client.drawClientLine(startX, startY, endX, endY, color);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		String hex = "#"+Integer.toHexString(color.getRGB()).substring(2);
		obj.put("name", "Line");
		obj.put("color", hex);
		obj.put("startX", startX);
		obj.put("startY", startY);
		obj.put("endX", endX);
		obj.put("endY", endY);
		return obj;
	}
	
	public static WBLine loadFromJSON(JSONObject obj) 
	{
		// TODO Auto-generated method stub
		int startX = Integer.parseInt(obj.get("startX").toString());
		int startY = Integer.parseInt(obj.get("startY").toString());
		int endX = Integer.parseInt(obj.get("endX").toString());
		int endY = Integer.parseInt(obj.get("endY").toString());
		Color color = Color.decode(obj.get("color").toString());
		return new WBLine(startX, startY, endX, endY, color);
	}

}
