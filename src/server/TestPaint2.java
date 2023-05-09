package server;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class TestPaint2 extends Frame implements MouseListener,MouseMotionListener {

	static TestPaint2 frm = new TestPaint2();
	int ori_x,ori_y , after_x,after_y;
	int posX,posY ,disX,disY;//for draw circle
	static int f_w=800,f_h=600;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BorderLayout br = new BorderLayout();
		Label label_status = new Label("line" + Integer.toString(status));
		CheckboxGroup cbg = new CheckboxGroup();
		Checkbox checkBox1 = new Checkbox("1", cbg, status == 1 ? true : false);
		checkBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				status = 1;
				label_status.setText("��e�Ҧ�:" + Integer.toString(status));
			}
		});
		Checkbox checkBox2 = new Checkbox("2", cbg, status == 2 ? true : false);
		checkBox2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				status = 2;
				label_status.setText("��e�Ҧ�:" + Integer.toString(status));
			}
		});
		Checkbox checkBox3 = new Checkbox("3", cbg, status == 3 ? true : false);
		checkBox3.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				status = 3;
				label_status.setText("��e�Ҧ�:" + Integer.toString(status));
			}
		});
		Checkbox checkBox4 = new Checkbox("4", cbg, status == 4 ? true : false);
		checkBox4.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				status = 4;
				label_status.setText("��e�Ҧ�:" + Integer.toString(status));
			}
		});
		
		JPanel panel_bottom = new JPanel();
		panel_bottom.setLayout(new GridLayout(1,4));
		panel_bottom.add(checkBox1);
		panel_bottom.add(checkBox2);
		panel_bottom.add(checkBox3);
		panel_bottom.add(checkBox4);
		
		frm.setLayout(br);
		frm.setTitle("Drawing example");
		frm.setResizable(false);
		frm.setSize(f_w,f_h); 
		frm.add(label_status,br.NORTH);
		frm.add(panel_bottom,br.SOUTH);
		frm.addMouseListener(frm);
		frm.addMouseMotionListener(frm);
		frm.setVisible(true);
		
	}
	
	
	public void paint(Graphics g) {
		if(status ==3) {
			g.drawOval(ori_x, ori_y, 100, 100);
			posX = ori_x;
			posY = ori_y;
			//g.setColor(Color.red);
			//g.drawPolygon(new int[] {10,20,30}, new int[] {100,20,100}, 3);//�T����
			//g.drawLine(20, 15, 70, 50);
			//g.drawOval(30, 50, 60, 60);
			//g.drawRect(30, 50, 120, 40);
		}
	}

	static int status = 2;//�H��ø�ϼҦ�,2:�즲�ͦ��x��,3:�즲�ͦ����,4:�즲�ͦ��T����
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(status ==1) {
			after_x = e.getX();
			after_y = e.getY();
			Graphics g = getGraphics();
			g.drawLine(ori_x, ori_y, after_x, after_y);
			ori_x = after_x;
			ori_y = after_y;
		}else if(status ==2) {
			
		}else if(status ==3) {
			
			ori_x = e.getX() - disX;
			ori_y = e.getY() -disY;
			if(disX > 0 && disX < 100 && disY > 0 && disY < 100) {
				Graphics g = getGraphics();
				update(g); //�M�ŵe�����I���C��A��call paint()
			}
			//g.drawOval(ori_x, ori_y, w, h);
		}else if(status ==4) {
			
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(status==1) {
			ori_x = e.getX();
			ori_y = e.getY();	
		}else if(status ==2) {
			
		}
		else if(status ==3){
			disX = e.getX() - posX;
			disY = e.getY() - posY;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(status ==2) {
			int width = Math.abs(ori_x - e.getX());
			int height = Math.abs(ori_y - e.getY());
			int x = e.getX() - width / 2;
			int y = e.getY() - height / 2;
			if(x < 0)
				x = 0;
			if(y<0)
				y = 0;
			
			Graphics g = getGraphics();
			g.drawRect(x, y, width, height);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
