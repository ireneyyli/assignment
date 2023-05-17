package rmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import whiteboard.CreateWhiteBoard;
import whiteboard.JoinWhiteBoard;
import shape.*;

public class Server {
    /*************************
     * RMI 呼叫傳輸介面
     *************************/

    // 定義遠端可以使用的 Method (注意：是給 Client 端使用)
    public interface IServerRO extends Remote {
        public void registerClient(String username, Client.IClientRO client) throws RemoteException;
        
        public Boolean addServerParticipants(String selectedString) throws RemoteException;
        
        public Boolean kickServerParticipants(String selectedIndex) throws RemoteException;
        
        public Boolean sendServerMessage(String message) throws RemoteException;
        
        public Boolean drawServerPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
        
        public Boolean drawServerLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException;
        
        public Boolean drawServerCircle(int x, int y, int width, int height, Color color) throws RemoteException;
        
        public Boolean drawServerOval(int x, int y, int width, int height, Color color) throws RemoteException;
        
        public Boolean drawServerRect(int x, int y, int width, int height, Color color) throws RemoteException;
        
        public Boolean drawServerText(int startX, int startY, String text, Color color) throws RemoteException;
    }

    // 遠端可以使用的 Method 之所有實作
    public class ServerRO extends UnicastRemoteObject implements IServerRO {
        private CreateWhiteBoard board;
        private Client.IClientRO client;
        private HashMap<String, Client.IClientRO> clientName = new HashMap<String, Client.IClientRO>();
        private Stack<IShape> stack = new Stack<IShape>();

        public ServerRO(CreateWhiteBoard board) throws RemoteException {
            this.board = board;
        }
        

        // 紀錄 Client 端 RemoteObject，之後可以由 Server 端呼叫 Client 端做事
        @Override
        public void registerClient(String username, Client.IClientRO client) throws RemoteException {
            this.client = client;
            clientName.put(username, client);
            board.getListModel().addElement(username);
            
            for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                Client.IClientRO clientRO = entry.getValue();
                if (clientRO != null)
                	client.addClientParticipants(board.getListModel());
        	}
     
            JPanel serverBoard = board.getDrawingArea();
            for (IShape shape : stack) {
            	shape.sendToClient(client);
            }
        }

        // 處理由 Client 端發送的畫布更新資訊
        @Override
        public Boolean addServerParticipants(String selectedString) throws RemoteException {
            try {
            	board.getListModel().addElement(selectedString);
            	
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.addClientParticipants(selectedString);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean kickServerParticipants(String selectedString) throws RemoteException {
            try {
            	board.getListModel().removeElement(selectedString);
            	
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.kickClientParticipants(selectedString);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean sendServerMessage(String message) throws RemoteException {
            try {
            	board.getChatWindow().append(message);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.sendClientMessage(message);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBPen pen = new WBPen(startX, startY, endX, endY, color);
            	pen.addToGraphics(g);
            	stack.push(pen);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientPen(startX, startY, endX, endY, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBLine line = new WBLine(startX, startY, endX, endY, color);
            	line.addToGraphics(g);
            	stack.push(line);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientLine(startX, startY, endX, endY, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerCircle(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBCircle circle = new WBCircle(x, y, width, height, color);
            	circle.addToGraphics(g);
            	stack.push(circle);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientCircle(x, y, width, height, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerOval(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBOval oval = new WBOval(x, y, width, height, color);
            	oval.addToGraphics(g);
            	stack.push(oval);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientOval(x, y, width, height, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerRect(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBRect rect = new WBRect(x, y, width, height, color);
            	rect.addToGraphics(g);
            	stack.push(rect);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientRect(x, y, width, height, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerText(int startX, int startY, String text, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	if (text == null) text = "";
            	WBText textContent = new WBText(startX, startY, text, color);
            	textContent.addToGraphics(g);
            	stack.push(textContent);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientText(startX, startY, text, color);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

    }

    /*************************
     * Server 主要執行區塊
     *************************/

    private String host;
    private String port;
    private String username;
    private IServerRO serverRo;

    public Server(String host, String port, String username, CreateWhiteBoard board) throws RemoteException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.serverRo = new ServerRO(board);
    }

    public void start() {
        try {
            String url = "rmi://" + host + ":" + port + "/board";
            LocateRegistry.createRegistry(Integer.parseInt(port));
            System.out.println(url);
            Naming.rebind(url, serverRo);
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 當畫布更新時，要通知所有 Client 端也要同步畫布
    public void addServerParticipants(String selectedString) throws RemoteException {
    	serverRo.addServerParticipants(selectedString);
    }
    
    public void kickServerParticipants(String selectedString) throws RemoteException {
    	serverRo.kickServerParticipants(selectedString);
    }
    
    public void sendServerMessage(String message) throws RemoteException {
    	serverRo.sendServerMessage(message);
    }
    
    public void drawServerPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        serverRo.drawServerPen(startX, startY, endX, endY, color);
    }
    
    public void drawServerLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
        serverRo.drawServerLine(startX, startY, endX, endY, color);
    }
    
    public void drawServerCircle(int x, int y, int width, int height, Color color) throws RemoteException {
        serverRo.drawServerCircle(x, y, width, height, color);
    }
    
    public void drawServerOval(int x, int y, int width, int height, Color color) throws RemoteException {
        serverRo.drawServerOval(x, y, width, height, color);
    }
    
    public void drawServerRect(int x, int y, int width, int height, Color color) throws RemoteException {
        serverRo.drawServerRect(x, y, width, height, color);
    }
    
    public void drawServerText(int startX, int startY, String text, Color color) throws RemoteException {
        serverRo.drawServerText(startX, startY, text, color);
    }
}
