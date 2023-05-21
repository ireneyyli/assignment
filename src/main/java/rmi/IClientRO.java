package rmi;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;

/*************************
 * RMI 呼叫傳輸介面
 *************************/

// Define the Method that can be used by the remote end (note: it is for the server end)
public interface IClientRO extends Remote {
	public Boolean setClientParticipants(DefaultListModel listModel) throws RemoteException;
	public Boolean addClientParticipants(String selectedString) throws RemoteException;
	public Boolean exitClient(Boolean userKicked) throws RemoteException;
	public Boolean kickClientParticipantsList(String selectedString, Boolean userKicked) throws RemoteException;
	public Boolean newClientBoard() throws RemoteException;
	public Boolean closeClientBoard() throws RemoteException;
	public Boolean sendClientMessage(String message) throws RemoteException;
	public Boolean drawClientPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
    public Boolean drawClientLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
    public Boolean drawClientCircle(int x, int y, int width, int height, Color color) throws RemoteException;
    public Boolean drawClientOval(int x, int y, int width, int height, Color color) throws RemoteException;
    public Boolean drawClientRect(int x, int y, int width, int height, Color color) throws RemoteException;
    public Boolean drawClientText(int startX, int startY, String text, Color color) throws RemoteException;
}
