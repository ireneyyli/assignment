package rmi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import whiteboard.CreateWhiteBoard;
import whiteboard.JoinWhiteBoard;

public class Client {
    /*************************
     * RMI 呼叫傳輸介面
     *************************/

    // 定義遠端可以使用的 Method (注意：是給 Server 端使用)
    public interface IClientRO extends Remote {
    	public Boolean drawClientPen(int startX, int startY, int endX, int endY) throws RemoteException;
        public Boolean drawClientLine(int startX, int startY, int endX, int endY) throws RemoteException;
        public Boolean drawClientCircle(int x, int y, int width, int height) throws RemoteException;
        public Boolean drawClientRect(int x, int y, int width, int height) throws RemoteException;
    }

    // 遠端可以使用的 Method 之所有實作
    public class ClientRO extends UnicastRemoteObject implements IClientRO {
        private JoinWhiteBoard board;
        private ByteArrayOutputStream out;

        protected ClientRO(JoinWhiteBoard board) throws RemoteException {
            this.board = board;
        }

        // 接收由 Server 端傳回之訊息
        @Override
        public Boolean drawClientPen(int startX, int startY, int endX, int endY) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawLine(startX, startY, endX, endY);
            	
                return true;
            } catch (Exception e) {
                System.out.println("Client exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawClientLine(int startX, int startY, int endX, int endY) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawLine(startX, startY, endX, endY);
            	
                return true;
            } catch (Exception e) {
                System.out.println("Client exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawClientCircle(int x, int y, int width, int height) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawOval(x, y, width, height);
            	
                return true;
            } catch (Exception e) {
                System.out.println("Client exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        public Boolean drawClientRect(int x, int y, int width, int height) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	g.drawRect(x, y, width, height);
            	
                return true;
            } catch (Exception e) {
                System.out.println("Client exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    /*************************
     * Client 主要執行區塊
     *************************/

    private String host;
    private String port;
    private String username;
    private IClientRO clientRO;
    private Server.IServerRO serverRO;

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
            serverRO = (Server.IServerRO) Naming.lookup(url);
            serverRO.registerClient(username, clientRO);
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 當畫布更新時，要通知 Server 端也要同步更新其他人的畫布
    public void drawClientPen(int startX, int startY, int endX, int endY) throws RemoteException {
        serverRO.drawServerPen(startX, startY, endX, endY);
    }
    
    public void drawClientLine(int startX, int startY, int endX, int endY) throws RemoteException {
        serverRO.drawServerLine(startX, startY, endX, endY);
    }
    
    public void drawClientCircle(int x, int y, int width, int height) throws RemoteException  {
        serverRO.drawServerCircle(x, y, width, height);
    }
    
    public void drawClientRect(int x, int y, int width, int height) throws RemoteException  {
        serverRO.drawServerRect(x, y, width, height);
    }
}