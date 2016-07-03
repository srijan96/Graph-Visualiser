import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
class Point
{
	public int x;
	public int y;
	public boolean focus;
	public int visible;
	Point(int x,int y){	this.x = x;this.y = y;focus = false;visible = 2;	}
}
class Edge
{
	public int s;
	public int e;
	public int w;
	Edge(int x,int y){	s = x;e = y;w=1;	}
	Edge(int x,int y,int wt){	s = x;e = y;w=wt;	}
}
public class GraphDraw extends JPanel
{
	private static int n;
	private int maxDegree;
	private static ArrayList<Edge> edges = new ArrayList();
	private static Point points[];
	private static int degrees[];
	private int mx,my;
	public double scale = 1.0;
	private static int width = 800;
	private static int height = 600;
	private Point centre = new Point(400,300);
	private Point offCentre = new Point(0,0);
	private static int numIcons = 10;
	private static BufferedImage icons[] = new BufferedImage[numIcons];
	private boolean enablePan,enableDrag;
	private int showHidden;
	private int focused;
	GraphDraw(int mD)
	{
		for(int i=0;i<numIcons;i++)
		{
			try{
				icons[i] = ImageIO.read(new File("icons/"+i+".png"));
			}catch(IOException e){}
		}
		maxDegree = mD;
		enablePan = false;
		enableDrag = true;
		showHidden = 0;
		focused = -1;
		addMouseListener(new MouseAdapter() { 
			public void mousePressed(MouseEvent me) { 
				if(me.getX() <= numIcons*30 && me.getY() <= 30)
				{
					int action = (me.getX()-5)/30;
					if(action == 0){	enablePan = true;	enableDrag = false;		setCursor(new Cursor(Cursor.MOVE_CURSOR));	}
					else if(action == 1){	if(scale < 5)	scale += 0.25; 	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	}
					else if(action == 2){	scale = 1; 	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	}					
					else if(action == 3){	if(scale > 0.25)	scale -= 0.25; 	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	}
					else if(action == 4){	enablePan = false;	enableDrag = true;	setCursor(new Cursor(Cursor.HAND_CURSOR));	}
					else if(action == 5){	offCentre.x = 0;	offCentre.y = 0;	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	}
					else if(action == 6){	reset();	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	}					
					else if(action == 7){	if(focused >= 0)	points[focused].visible = showHidden;		}	
					else if(action == 8){	if(focused >= 0)	points[focused].visible = 2;		}	
					else if(action == 9)
					{
						for(int i=0;i<n;i++)
						{
							if(points[i].visible == showHidden)	points[i].visible = 1-showHidden;
						}
						showHidden = 1- showHidden;
					}
					focused = -1;
				}
				int o = findOval(me.getX(),me.getY());
				mx = me.getX();
				my = me.getY();
				for(int i=0;i<n;i++)	points[i].focus = false;
				if(o >= 0)
				{
					points[o].focus = true;
					focused = o;
				}
				repaint();
			} 
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int dx = e.getX() - mx;
				int dy = e.getY() - my;
				int o = findOval(mx,my);
				//System.out.println(dx + " "+dy);
				if(o >= 0 && enableDrag)
				{
					mx = mx+dx;
					my = my+dy;
					points[o].x += dx;
					points[o].y += dy;
				}	
				else if(enablePan)
				{
					offCentre.x += dx/25;
					offCentre.y += dy/25;
				}			
				repaint();
			}
		});
        }
        int findOval(int x,int y)
	{
		x = centre.x + (int)((x - offCentre.x-centre.x)/scale);
		y = centre.y - (int)((centre.y + offCentre.y - y)/scale);
		for(int i=0;i<n;i++)
		{
			if ((points[i].x - x)*(points[i].x - x)+(points[i].y - y)*(points[i].y - y) <= 100+50*(5-scale))	return i;
		}
		return -1;
	}
	private static void fetch()
	{
		File file = new File("edges.csv");
		 try 
		 {
			Scanner sc = new Scanner(file);
			if (sc.hasNextLine()) n = sc.nextInt();
			while (sc.hasNextLine()) 
			{
				String s = sc.next();
				if(s.equals(""))	break;
				String[] v = s.split(",");
				if(v.length >= 3)	edges.add(new Edge(Integer.parseInt(v[0]),Integer.parseInt(v[1]),Integer.parseInt(v[2])));
				else	edges.add(new Edge(Integer.parseInt(v[0]),Integer.parseInt(v[1])));
			}
			sc.close();
		} 
		catch (Exception e) {
//		e.printStackTrace();
		}
	}
	public void reset()
	{
		int diameter = 10;
		int x=0,y=0;
		int r = 100;
		for(int i=0;i<n;i++)
		{
			if(degrees[i] >= maxDegree*4/5)	r = 50;
			else if(degrees[i] >= maxDegree*3/5)	r = 100;
			else	r = 150;
			x = (int) (r * Math.cos(Math.toRadians(i*360/n)));
			y = (int) (r * Math.sin(Math.toRadians(i*360/n)));	
			x = x + centre.x;
			y =  centre.y - y;
			diameter = 10;
			points[i] = new Point(x,y);
		}
	}
	public void paint(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
	        g2d.fillRect(0, 0, getWidth(), getHeight());
	        for(int i=0;i<numIcons;i++)	g2d.drawImage(icons[i], 10+i*30, 10, null);
		int diameter = 10;
		int x=0,y=0;
		int r = 100;
		for(int i=0;i<n;i++)
		{
			if(degrees[i] >= maxDegree*4/5)	r = 50;
			else if(degrees[i] >= maxDegree*3/5)	r = 100;
			else	r = 150;
			x = (int) (r * Math.cos(Math.toRadians(i*360/n)));
			y = (int) (r * Math.sin(Math.toRadians(i*360/n)));	
			x = x + centre.x;
			y =  centre.y - y;
			g2d.setPaint(new Color(210,200,10));
			diameter = 10;
			if(degrees[i] >= maxDegree*3/5)	g2d.setPaint(new Color(20,20,210));
			if(degrees[i] >= maxDegree*4/5)	g2d.setPaint(new Color(210,20,10));
			if(points[i] == null)	points[i] = new Point(x,y);
			else{
				x = points[i].x ;
				y = points[i].y;
			}
			x = (int)((x-centre.x)*scale) + centre.x+offCentre.x;
			y = centre.y - (int)((centre.y-y)*scale)+offCentre.y;
			if(points[i].focus)
			{
				if(points[i].visible == 2)
				{
					g2d.setPaint(new Color(20,20,20));
					diameter = 15;
					g2d.setFont(new Font("TimesRoman", Font.BOLD, 15)); 				
					g2d.drawString("Degree = "+degrees[i],x,y); 
					g2d.setPaint(new Color(220,10,240));	
				}
				else if(points[i].visible == 1)	g2d.setPaint(new Color(220,210,240));		
			}
			if(points[i].visible != 0)	g2d.fillOval(x ,y , diameter,diameter);	
		}
		for(Edge ed : edges)
		{
			g2d.setPaint(new Color(10,200,150));
			g2d.setStroke(new BasicStroke(ed.w));
			if(points[ed.s].focus || points[ed.e].focus)	g2d.setStroke(new BasicStroke(ed.w*2));
			if(points[ed.s].focus || points[ed.e].focus)			g2d.setPaint(new Color(255,0,0));
			if(points[ed.s].visible == 1 || points[ed.e].visible == 1)			g2d.setPaint(new Color(220,210,240));
			int x1 = (int)((points[ed.s].x-centre.x)*scale) + centre.x+offCentre.x;
			int y1 = centre.y - (int)((centre.y-points[ed.s].y)*scale)+offCentre.y;
			int x2 = (int)((points[ed.e].x-centre.x)*scale) + centre.x+offCentre.x;
			int y2 = centre.y - (int)((centre.y-points[ed.e].y)*scale)+offCentre.y;
			if(points[ed.s].visible != 0 && points[ed.e].visible != 0)	g2d.drawLine(x1,y1,x2,y2);
		}
	}
	public static void main(String args[])
	{
		fetch();
		points = new Point[n];
		degrees = new int[n];
		int maxDeg = 0;
		for(Edge ed : edges)
		{
			//System.out.println(ed.s+"-->"+ed.e);
			degrees[ed.s]++;
			degrees[ed.e]++;
			if(degrees[ed.s] > maxDeg)	maxDeg = degrees[ed.s];
			if(degrees[ed.e] > maxDeg)	maxDeg = degrees[ed.e];
		}
		JPanel panel = new GraphDraw(maxDeg);
		JFrame f = new JFrame("Graph Visualiser");
		f.setBounds(0, 0, 800,600);
		f.add(panel);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
