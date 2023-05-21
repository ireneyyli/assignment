package shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

import rmi.IClientRO;

public class WBText implements IShape {
	public WBText(int startX, int startY, String text, Color color) {
		super();
		this.startX = startX;
		this.startY = startY;
		this.text = text;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private int startX;
	private int startY;
	private String text;
	private Color color;

	public void addToGraphics(Graphics g) {
		g.setColor(color);
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2.setStroke(new BasicStroke(3));
    	g2.drawString(text, startX, startY);
	};
	
	public void sendToClient(IClientRO client) throws RemoteException {
		client.drawClientText(startX, startY, text, color);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		String hex = "#"+Integer.toHexString(color.getRGB()).substring(2);
		obj.put("name", "Text");
		obj.put("color", hex);
		obj.put("startX", startX);
		obj.put("startY", startY);
		obj.put("text", text);
		return obj;
	};
	
	public static WBText loadFromJSON(JSONObject obj) 
	{
		// TODO Auto-generated method stub
		int startX = Integer.parseInt(obj.get("startX").toString());
		int startY = Integer.parseInt(obj.get("startY").toString());
		String text = obj.get("text").toString();
		Color color = Color.decode(obj.get("color").toString());
		return new WBText(startX, startY, text, color);
	}
}
