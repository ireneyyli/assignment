package rmi;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.io.FileReader;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import shape.IShape;
import shape.WBCircle;
import shape.WBLine;
import shape.WBOval;
import shape.WBPen;
import shape.WBRect;
import shape.WBText;
import whiteboard.CreateWhiteBoard;

//All implementations of Method that can be used by the remote
public class ServerRO extends UnicastRemoteObject implements IServerRO {
    private CreateWhiteBoard board;
    private IClientRO client;
    private HashMap<String, IClientRO> clientName = new HashMap<String, IClientRO>();
    private Stack<IShape> shapes = new Stack<IShape>();

    public ServerRO(CreateWhiteBoard board) throws RemoteException {
        this.board = board;
    }
    

    // Record the Client-side RemoteObject, and then the Server-side can call the Client-side to do things
    public void registerClient(String username, IClientRO client) throws RemoteException {
        this.client = client;
        
        if (clientName.get(username) != null) {
        	JOptionPane.showMessageDialog(null, "The username is used. Please enter another one", "Join the Room", JOptionPane.INFORMATION_MESSAGE);
        	client.exitClient(false);
        	return;
        }
        
        int input = JOptionPane.showConfirmDialog(null, "Do you want to allow the user to join the room");
        
        if (input != 0) { 
        	client.exitClient(false);
        	return;
        }
        
        clientName.put(username, client);
        board.getListModel().addElement(username);
        
        for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
            String key = entry.getKey();
            IClientRO clientRO = entry.getValue();
            if (clientRO != null)
            	client.setClientParticipants(board.getListModel());
    	}
 
        JPanel serverBoard = board.getDrawingArea();
        for (IShape shape : shapes) {
        	shape.sendToClient(client);
        }
    }

    // Handle canvas update information sent by the client
    public Boolean addServerParticipants(String selectedString) throws RemoteException {
        try {
        	board.getListModel().addElement(selectedString);
        	
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
    
    public Boolean kickServerParticipants(String selectedString, Boolean userKicked) throws RemoteException {
        try {
        	board.getListModel().removeElement(selectedString);
        	
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
                if (clientRO != null)
                	clientRO.kickClientParticipantsList(selectedString, userKicked);
        	}
        	
        	if (clientName.get(selectedString) != null) {
        		clientName.get(selectedString).exitClient(userKicked);
        		clientName.remove(selectedString);
        	}
        	
            return true;
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean newServerBoard(Boolean fileCreated) throws RemoteException {
        try {
        	if (fileCreated) {
        		int input = JOptionPane.showConfirmDialog(null, "Do you want to create a new board?");
                
                if (input != 0) { 
                	return false;
                }
        	}
        	
        	Graphics g = this.board.getDrawingArea().getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.board.getDrawingArea().getWidth(), this.board.getDrawingArea().getHeight());
            
            shapes.clear();
            
            for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	// save file into Desktop
        	String path = System.getProperty("user.home") + "/Desktop/shapes.json";
        	
            try (FileWriter out = new FileWriter(path)) {
                out.write(arr.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JOptionPane.showMessageDialog(null, "Save image into Desktop called shapes.json successfully", "Save Image", JOptionPane.INFORMATION_MESSAGE);
        	
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
        	// save file
        	FileDialog savedialog = new FileDialog(board, "Save image", FileDialog.SAVE);
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
        	FileDialog opendialog = new FileDialog(board, "Open an image", FileDialog.LOAD);
            opendialog.setVisible(true);
            if (opendialog.getFile() == null) {
                return false;
            }
        	
        	JSONParser parser = new JSONParser();
        	JSONArray arr = (JSONArray)parser.parse(new FileReader(opendialog.getDirectory() + opendialog.getFile()));
        	newServerBoard(false);
        	
        	for (Object obj : arr) {
        		JSONObject jobj = (JSONObject) obj;
        		switch(jobj.get("name").toString()) {
        		case "Pen":
        			WBPen pen = WBPen.loadFromJSON(jobj);
        			drawServerPen(pen.getStartX(), pen.getStartY(), pen.getEndX(), pen.getEndY(), pen.getColor());
        			break;
        		case "Line":
        			WBLine line = WBLine.loadFromJSON(jobj);
        			drawServerLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), line.getColor());
        			break;
        		case "Circle":
        			WBCircle circle = WBCircle.loadFromJSON(jobj);
        			drawServerCircle(circle.getX(), circle.getY(), circle.getWidth(), circle.getWidth(), circle.getColor());
        			break;
        		case "Oval":
        			WBOval oval = WBOval.loadFromJSON(jobj);
        			drawServerOval(oval.getX(), oval.getY(), oval.getWidth(), oval.getWidth(), oval.getColor());
        			break;
        		case "Rect":
        			WBRect rect = WBRect.loadFromJSON(jobj);
        			drawServerRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getWidth(), rect.getColor());
        			break;
        		case "Text":
        			WBText text = WBText.loadFromJSON(jobj);
        			drawServerText(text.getStartX(), text.getStartY(), text.getText(), text.getColor());
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
    
    public Boolean closeServerBoard() throws RemoteException {
        try {
        	int input = JOptionPane.showConfirmDialog(null, "Do you want to close the room?");
            
            if (input != 0) { 
            	return false;
            }
            
            board.dispose();
        	
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
                if (clientRO != null) 
                	client.closeClientBoard();
                	
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
        	for (HashMap.Entry<String, IClientRO> entry : clientName.entrySet()) {
                String key = entry.getKey();
                IClientRO clientRO = entry.getValue();
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
