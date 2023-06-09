package rmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shape.WBCircle;
import shape.WBLine;
import shape.WBOval;
import shape.WBPen;
import shape.WBRect;
import shape.WBText;
import whiteboard.CreateWhiteBoard;
import whiteboard.JoinWhiteBoard;

public class Client {
//    /*************************
//     * RMI 呼叫傳輸介面
//     *************************/
//
//    // Define the Method that can be used by the remote end (note: it is for the server end)
//    public interface IClientRO extends Remote {
//    	public Boolean setClientParticipants(DefaultListModel listModel) throws RemoteException;
//    	public Boolean addClientParticipants(String selectedString) throws RemoteException;
//    	public Boolean exitClient() throws RemoteException;
//    	public Boolean kickClientParticipantsList(String selectedString) throws RemoteException;
//    	public Boolean newClientBoard() throws RemoteException;
//    	public Boolean closeClientBoard() throws RemoteException;
//    	public Boolean sendClientMessage(String message) throws RemoteException;
//    	public Boolean drawClientPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
//        public Boolean drawClientLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
//        public Boolean drawClientCircle(int x, int y, int width, int height, Color color) throws RemoteException;
//        public Boolean drawClientOval(int x, int y, int width, int height, Color color) throws RemoteException;
//        public Boolean drawClientRect(int x, int y, int width, int height, Color color) throws RemoteException;
//        public Boolean drawClientText(int startX, int startY, String text, Color color) throws RemoteException;
//    }

//    // All implementations of Method that can be used by the remote
//    public class ClientRO extends UnicastRemoteObject implements IClientRO {
//        private JoinWhiteBoard board;
//        private ByteArrayOutputStream out;
//
//        protected ClientRO(JoinWhiteBoard board) throws RemoteException {
//            this.board = board;
//        }
//
//        // Receive the message returned by the server
//        public Boolean setClientParticipants(DefaultListModel listModel) throws RemoteException {
//            try {
//            	board.setListModel(listModel);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean addClientParticipants(String selectedString) throws RemoteException {
//            try {
//            	board.getListModel().addElement(selectedString);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        
//        
//        public Boolean exitClient() throws RemoteException {
//            try {
//            	
//            	// JOptionPane.showMessageDialog(null, "You have been removed from the room by the manager", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
//            	new Thread() {
//            	    @Override
//            	    public void run() {
//            	      System.out.println("Shutting down...");
//            	      try {
//            	        sleep(1000);
//            	      } catch (InterruptedException e) {
//            	      }
//            	      System.out.println("You have been removed from the room by the manager.");
//            	      System.exit(0);
//            	    }
//
//            	  }.start();
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean kickClientParticipantsList(String selectedString) throws RemoteException {
//            try {
//            	board.getListModel().removeElement(selectedString);
//            	
//            	
//            	//JOptionPane.showMessageDialog(null, "You have been removed from the room by the manager", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
//            	//board.dispose();
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean newClientBoard() throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//                g.setColor(Color.WHITE);
//                g.fillRect(0, 0, this.board.getDrawingArea().getWidth(), this.board.getDrawingArea().getHeight());
//                
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean closeClientBoard() throws RemoteException {
//            try {
//            	JOptionPane.showMessageDialog(null, "Manager closes the room", "Leave Room", JOptionPane.INFORMATION_MESSAGE);
//            	board.dispose();
//                
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        } 
//        
//        public Boolean sendClientMessage(String message) throws RemoteException {
//            try {
//            	board.getChatWindow().append(message);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	WBPen pen = new WBPen(startX, startY, endX, endY, color);
//            	pen.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	WBLine line = new WBLine(startX, startY, endX, endY, color);
//            	line.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientCircle(int x, int y, int width, int height, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	WBCircle circle = new WBCircle(x, y, width, height, color);
//            	circle.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientOval(int x, int y, int width, int height, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	WBOval oval = new WBOval(x, y, width, height, color);
//            	oval.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientRect(int x, int y, int width, int height, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	WBRect rect = new WBRect(x, y, width, height, color);
//            	rect.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        
//        public Boolean drawClientText(int startX, int startY, String text, Color color) throws RemoteException {
//            try {
//            	Graphics g = this.board.getDrawingArea().getGraphics();
//            	if (text == null) text = "";
//            	WBText textContent = new WBText(startX, startY, text, color);
//            	textContent.addToGraphics(g);
//            	
//                return true;
//            } catch (Exception e) {
//                System.out.println("Client exception: " + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//    }

    /*************************
     * Client 主要執行區塊
     *************************/

    private String host;
    private String port;
    private String username;
    private IClientRO clientRO;
    private IServerRO serverRO;

    public Client(String host, String port, String username, JoinWhiteBoard board) throws RemoteException  {
        this.host = host;
        this.port = port;
        this.username = username;
        this.clientRO = new ClientRO(board);
    }

    public void start() {
        try {
            String url = "rmi://" + host + ":" + port + "/board";
            System.out.println(url);
            serverRO = (IServerRO) Naming.lookup(url);
            serverRO.registerClient(username, clientRO);
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // When the canvas is updated, it is necessary to notify the server to also update other people's canvases synchronously
    public void kickClientParticipantsList(String username, Boolean userKicked) throws RemoteException {
    	serverRO.kickServerParticipants(username, userKicked);
    };
    
    public void sendClientMessage(String message) throws RemoteException {
    	serverRO.sendServerMessage(message);
    };
    
    public void drawClientPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        serverRO.drawServerPen(startX, startY, endX, endY, color);
    }
    
    public void drawClientLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        serverRO.drawServerLine(startX, startY, endX, endY, color);
    }
    
    public void drawClientCircle(int x, int y, int width, int height, Color color) throws RemoteException  {
        serverRO.drawServerCircle(x, y, width, height, color);
    }
    
    public void drawClientOval(int x, int y, int width, int height, Color color) throws RemoteException  {
        serverRO.drawServerOval(x, y, width, height, color);
    }
    
    public void drawClientRect(int x, int y, int width, int height, Color color) throws RemoteException  {
        serverRO.drawServerRect(x, y, width, height, color);
    }
    
    public void drawClientText(int startX, int startY, String text, Color color) throws RemoteException  {
        serverRO.drawServerText(startX, startY, text, color);
    }
}