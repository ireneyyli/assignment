package shape;

import java.awt.Color;
import java.awt.Graphics;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

import rmi.IClientRO;

public class WBOval implements IShape {
	public WBOval(int x, int y, int width, int height, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;
	
	public void addToGraphics(Graphics g) {
		g.setColor(color);
    	g.drawOval(x, y, width, height);
	}
	
	public void sendToClient(IClientRO client)  throws RemoteException {
		client.drawClientOval(x, y, width, height, color);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		String hex = "#"+Integer.toHexString(color.getRGB()).substring(2);
		obj.put("name", "Oval");
		obj.put("color", hex);
		obj.put("x", x);
		obj.put("y", y);
		obj.put("width", width);
		obj.put("height", height);
		return obj;
	}
	
	public static WBOval loadFromJSON(JSONObject obj) 
	{
		// TODO Auto-generated method stub
		int x = Integer.parseInt(obj.get("x").toString());
		int y = Integer.parseInt(obj.get("y").toString());
		int width = Integer.parseInt(obj.get("width").toString());
		int height = Integer.parseInt(obj.get("height").toString());
		Color color = Color.decode(obj.get("color").toString());
		return new WBOval(x, y, width, height, color);
	}

}
