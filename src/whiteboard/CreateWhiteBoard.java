package whiteboard;

import javax.swing.*;

import rmi.Server;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class CreateWhiteBoard extends JFrame implements Serializable, ActionListener, MouseListener, MouseMotionListener {
    private JButton penBtn, lineBtn, circleBtn, ovalBtn, rectBtn, clearBtn;
    private JPanel drawingArea;
    private int startX, startY, endX, endY;
    private String currentAction = "Pen";
    private Color currentColor = Color.BLACK;
    private Server server;
    
    public JPanel getDrawingArea() {
    	return drawingArea;
    }
    
    public void setDrawingArea(JPanel drawingArea) {
    	this.drawingArea = drawingArea;
    }

    public CreateWhiteBoard(String host, String port, String username) throws RemoteException {
        super("ServerBoard");

        // 設定視窗大小及其他屬性
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 建立畫面上方的工具列
        JPanel toolBar = new JPanel();
        penBtn = new JButton("Pen");
        penBtn.addActionListener(this);
        lineBtn = new JButton("Line");
        lineBtn.addActionListener(this);
        circleBtn = new JButton("Circle");
        circleBtn.addActionListener(this);
        ovalBtn = new JButton("Oval");
        ovalBtn.addActionListener(this);
        rectBtn = new JButton("Rectangle");
        rectBtn.addActionListener(this);
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(this);
        toolBar.add(penBtn);
        toolBar.add(lineBtn);
        toolBar.add(circleBtn);
        toolBar.add(ovalBtn);
        toolBar.add(rectBtn);
        toolBar.add(clearBtn);

        // 建立畫布區域
        drawingArea = new JPanel();
        drawingArea.setBackground(Color.WHITE);
        drawingArea.addMouseListener(this);
        drawingArea.addMouseMotionListener(this);

        // 把工具列和畫布區域加到視窗中
        Container content = getContentPane();
        content.add(toolBar, BorderLayout.NORTH);
        content.add(drawingArea, BorderLayout.CENTER);

        setVisible(true);

        // 建立一個可以跟其他畫布同步的 Server 端
        server = new Server(host, port, username, this);
        server.start();
        // server.onBoardChanged(image, this);
    }

    public void actionPerformed(ActionEvent e) {
        // 根據使用者選擇的按鈕，設定當前動作
    	if (e.getSource() == penBtn) {
            currentAction = "Pen";
        } 
    	else if (e.getSource() == lineBtn) {
            currentAction = "Line";
        } 
        else if (e.getSource() == circleBtn) {
            currentAction = "Circle";
        } 
        else if (e.getSource() == ovalBtn) {
            currentAction = "Oval";
        } 
        else if (e.getSource() == rectBtn) {
            currentAction = "Rectangle";
        } 
        else if (e.getSource() == clearBtn) {
            Graphics g = drawingArea.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, drawingArea.getWidth(), drawingArea.getHeight());
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();

        // 根據當前動作，在畫布上畫出對應的圖形
        Graphics g = drawingArea.getGraphics();
        g.setColor(currentColor);
        if (currentAction.equals("Pen")) {
            g.drawLine(startX, startY, endX, endY);
            try {
            	server.drawServerPen(startX, startY, endX, endY);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Line")) {
            try {
            	server.drawServerLine(startX, startY, endX, endY);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Circle")) {
            try {
            	int x = Math.min(startX, endX);
        		int y= Math.min(startY, endY);
        		int width = Math.abs(startX - endX);
        		int height = Math.abs(startY - endY);
        		width = Math.max(width, height);
            	server.drawServerCircle(x, y, width, width);
            	
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Oval")) {
            try {
            	int x = Math.min(startX, endX);
        		int y= Math.min(startY, endY);
        		int width = Math.abs(startX - endX);
        		int height = Math.abs(startY - endY);
            	server.drawServerOval(x, y, width, height);
            	
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Rectangle")) {
            try {
            	int x = Math.min(startX, endX);
        		int y= Math.min(startY, endY);
        		int width = Math.abs(startX - endX);
        		int height = Math.abs(startY - endY);
            	server.drawServerRect(x, y, width, height);
            	
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {

		endX = e.getX();
        endY = e.getY();

        // 根據當前動作，在畫布上畫出對應的圖形
        Graphics g = drawingArea.getGraphics();
        g.setColor(currentColor);
        if (currentAction.equals("Pen")) {
            // g.drawLine(startX, startY, endX, endY);
            startX = endX;
            startY = endY;
            try {
            	g.drawLine(startX, startY, endX, endY);
            	server.drawServerPen(startX, startY, endX, endY);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) throws RemoteException {
        new CreateWhiteBoard("localhost", "9999", "server");
    }
}