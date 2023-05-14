package whiteboard;

import javax.swing.*;

import rmi.Client;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class JoinWhiteBoard extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	private JButton penBtn, lineBtn, circleBtn, ovalBtn, rectBtn, textBtn, eraserBtn, clearBtn;
    private ImageIcon penIcon, lineIcon, circleIcon, ovalIcon, rectIcon, textIcon, eraserIcon;
    private JMenuBar menuBar;
    private JMenu propertiesMenu;
    private JMenuItem colourMenuItem;
    private JPanel toolBox, drawingArea, chatArea;
    private JLabel userLabel, chatLabel, toLabel;
    private JTextArea nameList, chatWindow, chatText;
    private JScrollPane listScrollPane, chatScrollPane, chatTextScrollPane;
    private JButton sendBtn;
    private int startX, startY, endX, endY;
    private String currentAction = "Pen";
    private Color currentColor = Color.BLACK;
    private String inputText = "";
    private DefaultListModel listModel;
    private String message = "";

    private Client client;
    
    public JPanel getDrawingArea() {
    	return drawingArea;
    }
    
    public JTextArea getChatWindow() {
    	return chatWindow;
    }
    
    public void setListModel(DefaultListModel listModel) {
    	this.listModel.removeAllElements();
    	for (Object name: listModel.toArray())
    		this.listModel.addElement(name);
    }
   

    public JoinWhiteBoard(String host, String port, String username) throws RemoteException {
        super("User: " + username + "'s Board");

        // 設定視窗大小及其他屬性
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Build the menu above
        menuBar = new JMenuBar();
        propertiesMenu = new JMenu("Properties");
        colourMenuItem = new JMenuItem("Colours");
        colourMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	Color color = JColorChooser.showDialog(null, "Choose a Colour", currentColor);
    			if (color != null) {
    				currentColor = color;
    			}
            }
        });
        
        menuBar.add(propertiesMenu);
        propertiesMenu.add(colourMenuItem);


        // 建立畫面上方的工具列
        toolBox = new JPanel();
        penIcon = new ImageIcon(new ImageIcon("src/icon/penIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        penBtn = new JButton(penIcon);
        penBtn.addActionListener(this);
        penBtn.setBorderPainted(false);
        penBtn.setContentAreaFilled(false);
        
        lineIcon = new ImageIcon(new ImageIcon("src/icon/lineIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        lineBtn = new JButton(lineIcon);
        lineBtn.addActionListener(this);
        lineBtn.setBorderPainted(false);
        lineBtn.setContentAreaFilled(false);
        
        circleIcon = new ImageIcon(new ImageIcon("src/icon/circleIcon.png").getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        circleBtn = new JButton(circleIcon);
        circleBtn.addActionListener(this);
        circleBtn.setBorderPainted(false);
        circleBtn.setContentAreaFilled(false);
        
        ovalIcon = new ImageIcon(new ImageIcon("src/icon/ovalIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        ovalBtn = new JButton(ovalIcon);
        ovalBtn.addActionListener(this);
        ovalBtn.setBorderPainted(false);
        ovalBtn.setContentAreaFilled(false);
        
        rectIcon = new ImageIcon(new ImageIcon("src/icon/rectangleIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        rectBtn = new JButton(rectIcon);
        rectBtn.addActionListener(this);
        rectBtn.setBorderPainted(false);
        rectBtn.setContentAreaFilled(false);
        
        textIcon = new ImageIcon(new ImageIcon("src/icon/textIcon.png").getImage().getScaledInstance(29, 23, Image.SCALE_SMOOTH));
        textBtn = new JButton(textIcon);
        textBtn.addActionListener(this);
        textBtn.setBorderPainted(false);
        textBtn.setContentAreaFilled(false);
        
        eraserIcon = new ImageIcon(new ImageIcon("src/icon/eraserIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        eraserBtn = new JButton(eraserIcon);
        eraserBtn.addActionListener(this);
        eraserBtn.setBorderPainted(false);
        eraserBtn.setContentAreaFilled(false);
        
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(this);
        
        toolBox.setLayout(new GridLayout(13, 1, 0, 0));
        toolBox.add(penBtn);
        toolBox.add(lineBtn);
        toolBox.add(circleBtn);
        toolBox.add(ovalBtn);
        toolBox.add(rectBtn);
        toolBox.add(textBtn);
        toolBox.add(eraserBtn);
        //toolBox.add(clearBtn)

        // 建立畫布區域
        drawingArea = new JPanel();
        drawingArea.setBackground(Color.WHITE);
        drawingArea.addMouseListener(this);
        drawingArea.addMouseMotionListener(this);
        
        // 建立聊天區域
        chatArea = new JPanel();
        chatArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        userLabel = new JLabel("Participants");
        
        listModel = new DefaultListModel();
        
        JList nameList = new JList(listModel);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        nameList.setLayoutOrientation(JList.VERTICAL);
        nameList.setVisibleRowCount(-1);
        listScrollPane = new JScrollPane(nameList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setPreferredSize(new Dimension(150, 130));
        
        chatLabel = new JLabel("Chat");
        
        chatWindow = new JTextArea();
        chatScrollPane = new JScrollPane(chatWindow, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setPreferredSize(new Dimension(150, 250));
        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        
        toLabel = new JLabel("To: Everyone");
        
        chatText = new JTextArea();
        chatTextScrollPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatTextScrollPane.setPreferredSize(new Dimension(150, 100));
        chatText.setLineWrap(true);
        
        sendBtn = new JButton("Send");
        sendBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendBtn.getPreferredSize().height));
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		message = chatText.getText();
            		String sendMessage = username + ": " + message + "\n";
					client.sendClientMessage(sendMessage);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                chatText.setText("");
            }
        });
        
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.add(userLabel);
        chatArea.add(listScrollPane);
        chatArea.add(chatLabel);
        chatArea.add(chatScrollPane);
        chatArea.add(toLabel);
        chatArea.add(chatTextScrollPane);
        chatArea.add(sendBtn);

        // 把工具列和畫布區域加到視窗中
        Container content = getContentPane();
        content.add(menuBar, BorderLayout.NORTH);
        content.add(toolBox, BorderLayout.WEST);
        content.add(drawingArea, BorderLayout.CENTER); // 1. OL
        content.add(chatArea, BorderLayout.EAST);

        setVisible(true);
        setResizable(false);

        // 建立一個可以跟其他畫布同步的 Client 端
        client = new Client(host, port, username, this);
        client.start();
        // client.onBoardChanged(image, this);
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
        else if (e.getSource() == textBtn) {
        	currentAction = "Text";
        }
        else if (e.getSource() == eraserBtn) {
        	currentAction = "Eraser";
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
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // 設置筆觸寬度為3個像素
            	g2.drawLine(startX, startY, endX, endY);
            	client.drawClientPen(startX, startY, endX, endY, currentColor);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Line")) {
            try {
            	client.drawClientLine(startX, startY, endX, endY, currentColor);
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
            	client.drawClientCircle(x, y, width, width, currentColor);
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
            	client.drawClientOval(x, y, width, height, currentColor);
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
            	client.drawClientRect(x, y, width, height, currentColor);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Text")) {
            try {
            	inputText = JOptionPane.showInputDialog("Please input the text:");
            	client.drawClientText(startX, startY, inputText, currentColor);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Eraser")) {
            try {
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // 設置筆觸寬度為3個像素
            	g2.drawLine(startX, startY, endX, endY);
            	client.drawClientPen(startX, startY, endX, endY, Color.WHITE);
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
            startX = endX;
            startY = endY;
            try {
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // 設置筆觸寬度為3個像素
            	g2.drawLine(startX, startY, endX, endY);
            	client.drawClientPen(startX, startY, endX, endY, currentColor);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
        else if (currentAction.equals("Eraser")) {
            startX = endX;
            startY = endY;
            try {
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // 設置筆觸寬度為3個像素
            	g2.drawLine(startX, startY, endX, endY);
            	client.drawClientPen(startX, startY, endX, endY, Color.WHITE);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) throws RemoteException {
        new JoinWhiteBoard("localhost", "9999", "client");
    }
}