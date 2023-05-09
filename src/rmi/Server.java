package rmi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import whiteboard.CreateWhiteBoard;
import whiteboard.JoinWhiteBoard;

public class Server {
    /*************************
     * RMI 呼叫傳輸介面
     *************************/

    // 定義遠端可以使用的 Method (注意：是給 Client 端使用)
    public interface IServerRO extends Remote {
        public void registerClient(String username, Client.IClientRO client) throws RemoteException;
        
        public Boolean drawServerPen(int startX, int startY, int endX, int endY) throws RemoteException;
        
        public Boolean drawServerLine(int startX, int startY, int endX, int endY) throws RemoteException;
        
        public Boolean drawServerCircle(int x, int y, int width, int height) throws RemoteException;
        
        public Boolean drawServerOval(int x, int y, int width, int height) throws RemoteException;
        
        public Boolean drawServerRect(int x, int y, int width, int height) throws RemoteException;
    }

    // 遠端可以使用的 Method 之所有實作
    public class ServerRO extends UnicastRemoteObject implements IServerRO {
        private CreateWhiteBoard board;
        private Client.IClientRO client;
        private HashMap<String, Client.IClientRO> clientName = new HashMap<String, Client.IClientRO>();

        public ServerRO(CreateWhiteBoard board) throws RemoteException {
            this.board = board;
        }

        // 紀錄 Client 端 RemoteObject，之後可以由 Server 端呼叫 Client 端做事
        @Override
        public void registerClient(String username, Client.IClientRO client) throws RemoteException {
            this.client = client;
            clientName.put(username, client);
            
            JPanel serverBoard = board.getDrawingArea();
        }

        // 處理由 Client 端發送的畫布更新資訊
        @Override
        public Boolean drawServerPen(int startX, int startY, int endX, int endY) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawLine(startX, startY, endX, endY);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientPen(startX, startY, endX, endY);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerLine(int startX, int startY, int endX, int endY) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawLine(startX, startY, endX, endY);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientLine(startX, startY, endX, endY);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerCircle(int x, int y, int width, int height) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawOval(x, y, width, height);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientCircle(x, y, width, height);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerOval(int x, int y, int width, int height) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawOval(x, y, width, height);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientOval(x, y, width, height);
            	}
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawServerRect(int x, int y, int width, int height) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawRect(x, y, width, height);
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.drawClientRect(x, y, width, height);
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
    public void drawServerPen(int startX, int startY, int endX, int endY) throws RemoteException {
        serverRo.drawServerPen(startX, startY, endX, endY);
    }
    
    public void drawServerLine(int startX, int startY, int endX, int endY) throws RemoteException {
        serverRo.drawServerLine(startX, startY, endX, endY);
    }
    
    public void drawServerCircle(int x, int y, int width, int height) throws RemoteException {
        serverRo.drawServerCircle(x, y, width, height);
    }
    
    public void drawServerOval(int x, int y, int width, int height) throws RemoteException {
        serverRo.drawServerOval(x, y, width, height);
    }
    
    public void drawServerRect(int x, int y, int width, int height) throws RemoteException {
        serverRo.drawServerRect(x, y, width, height);
    }
}
