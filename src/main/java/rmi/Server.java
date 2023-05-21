package rmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import rmi.Client.ClientRO;
import whiteboard.CreateWhiteBoard;
import whiteboard.JoinWhiteBoard;
import shape.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class Server {
    /*************************
     * RMI 呼叫傳輸介面
     *************************/

    // 定義遠端可以使用的 Method (注意：是給 Client 端使用)
    public interface IServerRO extends Remote {
        public void registerClient(String username, Client.IClientRO client) throws RemoteException;
        
        public Boolean addServerParticipants(String selectedString) throws RemoteException;
        
        public Boolean kickServerParticipants(String selectedIndex) throws RemoteException;
        
        public Boolean newServerBoard() throws RemoteException;
        
        public Boolean saveServerBoard() throws RemoteException;
        
        public Boolean saveAsServerBoard() throws RemoteException;
        
        public Boolean openServerBoard() throws RemoteException;
        
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
        private Stack<IShape> shapes = new Stack<IShape>();

        public ServerRO(CreateWhiteBoard board) throws RemoteException {
            this.board = board;
        }
        

        // 紀錄 Client 端 RemoteObject，之後可以由 Server 端呼叫 Client 端做事
        public void registerClient(String username, Client.IClientRO client) throws RemoteException {
            this.client = client;
            
            int input = JOptionPane.showConfirmDialog(null, "Allow the user?");
            
            if (input != 0) { 
            	client.exitClient();
            	return;
            }
            
            clientName.put(username, client);
            board.getListModel().addElement(username);
            
            for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                Client.IClientRO clientRO = entry.getValue();
                if (clientRO != null)
                	client.setClientParticipants(board.getListModel());
        	}
     
            JPanel serverBoard = board.getDrawingArea();
            for (IShape shape : shapes) {
            	shape.sendToClient(client);
            }
        }

        // 處理由 Client 端發送的畫布更新資訊
        public Boolean addServerParticipants(String selectedString) throws RemoteException {
            try {
            	board.getListModel().addElement(selectedString);
            	
            	// TODO: 這部分要思考一下是不是每個 client 都要呼叫 kickClientParticipants 這個 method 唷
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
        
        public Boolean kickServerParticipants(String selectedString) throws RemoteException {
            try {
            	board.getListModel().removeElement(selectedString);
            	
            	for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.kickClientParticipantsList(selectedString);
            	}
            	
            	if (clientName.get(selectedString) != null) {
            		clientName.get(selectedString).exitClient();
            		clientName.remove(selectedString);
            	}
            	
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        public Boolean newServerBoard() throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, this.board.getDrawingArea().getWidth(), this.board.getDrawingArea().getHeight());
                
                shapes.clear();
                
                for (HashMap.Entry<String, Client.IClientRO> entry : clientName.entrySet()) {
                    String key = entry.getKey();
                    Client.IClientRO clientRO = entry.getValue();
                    if (clientRO != null)
                    	clientRO.newClientBoard();
            	}
            	
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        public Boolean saveServerBoard() throws RemoteException {
            try {
            	JSONArray arr = new JSONArray();
            	for (IShape shape : shapes) {
            		arr.add(shape.toJSON());
            	}
            	// 存檔
            	String path = "./shapes.json";
            	
                try (FileWriter out = new FileWriter(path)) {
                    out.write(arr.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            	
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        public Boolean saveAsServerBoard() throws RemoteException {
            try {
            	JSONArray arr = new JSONArray();
            	for (IShape shape : shapes) {
            		arr.add(shape.toJSON());
            	}
            	// 存檔
            	FileDialog savedialog = new FileDialog(board, "save image", FileDialog.SAVE);
                savedialog.setVisible(true);
                if (savedialog.getFile() == null) {
                	return false;
                }
            	
                try (FileWriter out = new FileWriter(savedialog.getDirectory() + savedialog.getFile() + ".json")) {
                    out.write(arr.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            	
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        public Boolean openServerBoard() throws RemoteException {
            try {
            	FileDialog opendialog = new FileDialog(board, "open an image", FileDialog.LOAD);
                opendialog.setVisible(true);
                if (opendialog.getFile() == null) {
                    return false;
                }
            	
            	JSONParser parser = new JSONParser();
            	JSONArray arr = (JSONArray)parser.parse(new FileReader(opendialog.getDirectory() + opendialog.getFile()));
            	newServerBoard();
            	
            	for (Object obj : arr) {
            		JSONObject jobj = (JSONObject) obj;
            		switch(jobj.get("name").toString()) {
            		case "Circle":
            			WBCircle circle = WBCircle.loadFromJSON(jobj);
            			drawServerCircle(circle.getX(), circle.getY(), circle.getWidth(), circle.getWidth(), circle.getColor());
            			break;
            		default:
            			// System.out.print(jobj.get("name").toString());
            		}
            	}
                
            	
                return true;
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
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
        
        public Boolean drawServerPen(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBPen pen = new WBPen(startX, startY, endX, endY, color);
            	pen.addToGraphics(g);
            	shapes.push(pen);
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
        
        public Boolean drawServerLine(int startX, int startY, int endX, int endY, Color color) throws RemoteException {
            try {
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBLine line = new WBLine(startX, startY, endX, endY, color);
            	line.addToGraphics(g);
            	shapes.push(line);
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
        
        public Boolean drawServerCircle(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBCircle circle = new WBCircle(x, y, width, height, color);
            	circle.addToGraphics(g);
            	shapes.push(circle);
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
        
        public Boolean drawServerOval(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBOval oval = new WBOval(x, y, width, height, color);
            	oval.addToGraphics(g);
            	shapes.push(oval);
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
        
        public Boolean drawServerRect(int x, int y, int width, int height, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	WBRect rect = new WBRect(x, y, width, height, color);
            	rect.addToGraphics(g);
            	shapes.push(rect);
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
        
        public Boolean drawServerText(int startX, int startY, String text, Color color) throws RemoteException {
            try {
                
            	Graphics g = this.board.getDrawingArea().getGraphics();
            	if (text == null) text = "";
            	WBText textContent = new WBText(startX, startY, text, color);
            	textContent.addToGraphics(g);
            	shapes.push(textContent);
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
    
    public void newServerBoard() throws RemoteException {
    	serverRo.newServerBoard();
    }
    
    public void saveServerBoard() throws RemoteException {
    	serverRo.saveServerBoard();
    }
    
    public void saveAsServerBoard() throws RemoteException {
    	serverRo.saveAsServerBoard();
    }
    
    public void openServerBoard() throws RemoteException {
    	serverRo.openServerBoard();
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
