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
	private JTextArea chatWindow, chatText;
    private JScrollPane listScrollPane, chatScrollPane, chatTextScrollPane;
    private JButton sendBtn;
    private int startX, startY, endX, endY;
    private String currentAction = "Pen";
    private Color currentColor = Color.BLACK;
    private String inputText = "";
    private DefaultListModel listModel;
    private String message = "";
    private JList nameList;

    private Client client;
    
    public JPanel getDrawingArea() {
    	return drawingArea;
    }
    
    public JList getNameList() {
    	return nameList;
    }
    
    public DefaultListModel getListModel() {
    	return listModel;
    }
    
    public JTextArea getChatWindow() {
    	return chatWindow;
    }
    
    public void setListModel(DefaultListModel listModel) {
    	this.listModel.removeAllElements();
    	for (Object name: listModel.toArray())
    		this.listModel.addElement(name);
    }
   

    public JoinWhiteBoard(String host, String port, final String username) throws RemoteException {
        super("User: " + username + "'s Board");

        // Set window size and other properties
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Build the menu above
        menuBar = new JMenuBar();
        propertiesMenu = new JMenu("Properties");
        colourMenuItem = new JMenuItem("Colours");
        colourMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Color color = JColorChooser.showDialog(null, "Choose a Colour", currentColor);
    			if (color != null) {
    				currentColor = color;
    			}
            }
        });
        
        menuBar.add(propertiesMenu);
        propertiesMenu.add(colourMenuItem);


        // Create a toolbox at the left of the screen
        toolBox = new JPanel();
        penIcon = new ImageIcon(new ImageIcon("src/main/java/icon/penIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        penBtn = new JButton(penIcon);
        penBtn.addActionListener(this);
        penBtn.setBorderPainted(false);
        penBtn.setContentAreaFilled(false);
        
        lineIcon = new ImageIcon(new ImageIcon("src/main/java/icon/lineIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        lineBtn = new JButton(lineIcon);
        lineBtn.addActionListener(this);
        lineBtn.setBorderPainted(false);
        lineBtn.setContentAreaFilled(false);
        
        circleIcon = new ImageIcon(new ImageIcon("src/main/java/icon/circleIcon.png").getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        circleBtn = new JButton(circleIcon);
        circleBtn.addActionListener(this);
        circleBtn.setBorderPainted(false);
        circleBtn.setContentAreaFilled(false);
        
        ovalIcon = new ImageIcon(new ImageIcon("src/main/java/icon/ovalIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        ovalBtn = new JButton(ovalIcon);
        ovalBtn.addActionListener(this);
        ovalBtn.setBorderPainted(false);
        ovalBtn.setContentAreaFilled(false);
        
        rectIcon = new ImageIcon(new ImageIcon("src/main/java/icon/rectangleIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
        rectBtn = new JButton(rectIcon);
        rectBtn.addActionListener(this);
        rectBtn.setBorderPainted(false);
        rectBtn.setContentAreaFilled(false);
        
        textIcon = new ImageIcon(new ImageIcon("src/main/java/icon/textIcon.png").getImage().getScaledInstance(29, 23, Image.SCALE_SMOOTH));
        textBtn = new JButton(textIcon);
        textBtn.addActionListener(this);
        textBtn.setBorderPainted(false);
        textBtn.setContentAreaFilled(false);
        
        eraserIcon = new ImageIcon(new ImageIcon("src/main/java/icon/eraserIcon.png").getImage().getScaledInstance(30, 25, Image.SCALE_SMOOTH));
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

        // Create the drawing area
        drawingArea = new JPanel();
        drawingArea.setBackground(Color.WHITE);
        drawingArea.addMouseListener(this);
        drawingArea.addMouseMotionListener(this);
        
        // Create the chatting area
        chatArea = new JPanel();
        chatArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        userLabel = new JLabel("Participants");
        
        listModel = new DefaultListModel();
        
        nameList = new JList(listModel);
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

        // Add toolbox and canvas area to windows
        Container content = getContentPane();
        content.add(menuBar, BorderLayout.NORTH);
        content.add(toolBox, BorderLayout.WEST);
        content.add(drawingArea, BorderLayout.CENTER);
        content.add(chatArea, BorderLayout.EAST);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	try {
					client.kickClientParticipantsList(username, false);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

        setVisible(true);
        setResizable(false);

        // Create a client that can synchronize with other canvases
        client = new Client(host, port, username, this);
        client.start();
        
        // client.onBoardChanged(image, this);
    }
    
    public void actionPerformed(ActionEvent e) {
        // According to the button selected by the user, set the current action
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

        // According to the current action, draw the corresponding graphics on the canvas
        Graphics g = drawingArea.getGraphics();
        g.setColor(currentColor);
        if (currentAction.equals("Pen")) {
            try {
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // Set stroke width to 3 pixels
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
                g2.setStroke(new BasicStroke(3)); // Set stroke width to 3 pixels
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

        // According to the current action, draw the corresponding graphics on the canvas
        Graphics g = drawingArea.getGraphics();
        g.setColor(currentColor);
        if (currentAction.equals("Pen")) {
            startX = endX;
            startY = endY;
            try {
            	Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3)); // Set stroke width to 3 pixels
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
                g2.setStroke(new BasicStroke(3)); // Set stroke width to 3 pixels
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
        new JoinWhiteBoard("localhost", "9999", "client1");
    }
}