package shape;
import java.awt.Graphics;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

import rmi.IClientRO;

public interface IShape {
	public void addToGraphics(Graphics g);
	
	public void sendToClient(IClientRO client) throws RemoteException;
	
	public JSONObject toJSON();
		
}
