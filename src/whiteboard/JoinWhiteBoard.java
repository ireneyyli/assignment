package whiteboard;

import javax.swing.*;

import rmi.Client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;

public class JoinWhiteBoard extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private JButton pointBtn, lineBtn, circleBtn, rectBtn, clearBtn;
    private JPanel drawingArea;
    private int startX, startY, endX, endY;
    private String currentAction = "Pen";
    private Color currentColor = Color.BLACK;
    private Graphics2D graph;
    private BufferedImage image;

    private Client client;
    
    public JPanel getDrawingArea() {
    	return drawingArea;
    }
    
    public void setDrawingArea(JPanel drawingArea) {
    	this.drawingArea = drawingArea;
    }

    public JoinWhiteBoard(String host, String port, String username) throws RemoteException {
        super("ClientBoard");

        // 設定視窗大小及其他屬性
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 建立畫面上方的工具列
        JPanel toolBar = new JPanel();
        lineBtn = new JButton("Pen");
        lineBtn.addActionListener(this);
        pointBtn = new JButton("Line");
        pointBtn.addActionListener(this);
        circleBtn = new JButton("Circle");
        circleBtn.addActionListener(this);
        rectBtn = new JButton("Rectangle");
        rectBtn.addActionListener(this);
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(this);
        toolBar.add(pointBtn);
        toolBar.add(lineBtn);
        toolBar.add(circleBtn);
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

        // 建立一個可以跟其他畫布同步的 Client 端
        client = new Client(host, port, username, this);
        client.start();
        // client.onBoardChanged(image, this);
    }

    public void actionPerformed(ActionEvent e) {
        // 根據使用者選擇的按鈕，設定當前動作
    	if (e.getSource() == pointBtn) {
            currentAction = "Pen";
        }
    	else if (e.getSource() == lineBtn) {
            currentAction = "Line";
        } 
        else if (e.getSource() == circleBtn) {
        	currentAction = "Circle";
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
            try {
            	client.drawClientPen(startX, startY, endX, endY);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Line")) {
            try {
            	client.drawClientLine(startX, startY, endX, endY);
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
            	client.drawClientCircle(x, y, width, height);
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
            	client.drawClientRect(x, y, width, height);
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
            g.drawLine(startX, startY, endX, endY);
            startX = endX;
            startY = endY;
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) throws RemoteException {
        new JoinWhiteBoard("localhost", "9999", args[0]);
        System.out.print(args[0]);
    }
}