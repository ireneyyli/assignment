package server;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestPaint extends Frame implements ActionListener {

	static TestPaint frm = new TestPaint();
	static Button btn = new Button("Draw");
	boolean clicked = false;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BorderLayout br = new BorderLayout();
		frm.setTitle("Drawing example");
		frm.setLayout(br);
		frm.setSize(200,150); 
		frm.add(btn,br.SOUTH);
		btn.addActionListener(frm); 
		frm.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//frm.setBackground(Color.blue);
		Graphics g = getGraphics();
		clicked = true;
		//g.drawRect(100, 50, 70, 55);
		paint(g);
	}
	

	public void paint(Graphics g) {
		if(clicked)
			g.drawRect(100, 50, 70, 55);
	}


}
