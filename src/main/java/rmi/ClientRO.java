package rmi;

import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import shape.WBCircle;
import shape.WBLine;
import shape.WBOval;
import shape.WBPen;
import shape.WBRect;
import shape.WBText;
import whiteboard.JoinWhiteBoard;

//All implementations of Method that can be used by the remote
public class ClientRO extends UnicastRemoteObject implements IClientRO {
    private JoinWhiteBoard board;
    private ByteArrayOutputStream out;

    protected ClientRO(JoinWhiteBoard board) throws RemoteException {
        this.board = board;
    }

    // Receive the message returned by the server
    public Boolean setClientParticipants(DefaultListModel listModel) throws RemoteException {
        try {
        	board.setListModel(listModel);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean addClientParticipants(String selectedString) throws RemoteException {
        try {
        	board.getListModel().addElement(selectedString);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    public Boolean exitClient(Boolean userKicked) throws RemoteException {
        try {
        	
        	if (userKicked)
	        	JOptionPane.showMessageDialog(null, "You have been removed from the room by the manager", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
        	
        	new Thread() {
        	    @Override
        	    public void run() {
        	      System.out.println("Shutting down...");
        	      try {
        	        sleep(1000);
        	      } catch (InterruptedException e) {
        	      }
        	      System.out.println("Done");
        	      System.exit(0);
        	    }

        	  }.start();
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean kickClientParticipantsList(String selectedString, Boolean userKicked) throws RemoteException {
        try {
        	board.getListModel().removeElement(selectedString);
        	
        	
        	//JOptionPane.showMessageDialog(null, "You have been removed from the room by the manager", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
        	//board.dispose();
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean newClientBoard() throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.board.getDrawingArea().getWidth(), this.board.getDrawingArea().getHeight());
            
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean closeClientBoard() throws RemoteException {
        try {
        	JOptionPane.showMessageDialog(null, "Manager closes the room", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
        	board.dispose();
            
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    } 
    
    public Boolean sendClientMessage(String message) throws RemoteException {
        try {
        	board.getChatWindow().append(message);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	WBPen pen = new WBPen(startX, startY, endX, endY, color);
        	pen.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	WBLine line = new WBLine(startX, startY, endX, endY, color);
        	line.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientCircle(int x, int y, int width, int height, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	WBCircle circle = new WBCircle(x, y, width, height, color);
        	circle.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientOval(int x, int y, int width, int height, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	WBOval oval = new WBOval(x, y, width, height, color);
        	oval.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientRect(int x, int y, int width, int height, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	WBRect rect = new WBRect(x, y, width, height, color);
        	rect.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean drawClientText(int startX, int startY, String text, Color color) throws RemoteException {
        try {
        	Graphics g = this.board.getDrawingArea().getGraphics();
        	if (text == null) text = "";
        	WBText textContent = new WBText(startX, startY, text, color);
        	textContent.addToGraphics(g);
        	
            return true;
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}