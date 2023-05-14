package shape;
import java.awt.Graphics;
import java.rmi.RemoteException;

import rmi.Client;

public interface IShape {
	public void addToGraphics(Graphics g);
	
	public void sendToClient(Client.IClientRO client) throws RemoteException;
	
}
