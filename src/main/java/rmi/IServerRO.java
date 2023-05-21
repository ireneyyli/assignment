package rmi;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

/*************************
 * RMI 呼叫傳輸介面
 *************************/

// Define the Method that can be used by the remote end (note: it is for the client)
public interface IServerRO extends Remote {
    public void registerClient(String username, IClientRO client) throws RemoteException;
    
    public Boolean addServerParticipants(String selectedString) throws RemoteException;
    
    public Boolean kickServerParticipants(String selectedIndex, Boolean userKicked) throws RemoteException;
    
    public Boolean newServerBoard(Boolean fileCreated) throws RemoteException;
    
    public Boolean saveServerBoard() throws RemoteException;
    
    public Boolean saveAsServerBoard() throws RemoteException;
    
    public Boolean openServerBoard() throws RemoteException;
    
    public Boolean closeServerBoard() throws RemoteException;
    
    public Boolean sendServerMessage(String message) throws RemoteException;
    
    public Boolean drawServerPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
    
    public Boolean drawServerLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
    
    public Boolean drawServerCircle(int x, int y, int width, int height, Color color) throws RemoteException;
    
    public Boolean drawServerOval(int x, int y, int width, int height, Color color) throws RemoteException;
    
    public Boolean drawServerRect(int x, int y, int width, int height, Color color) throws RemoteException;
    
    public Boolean drawServerText(int startX, int startY, String text, Color color) throws RemoteException;
}
