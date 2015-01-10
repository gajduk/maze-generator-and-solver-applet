import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.PriorityQueue;

import javax.swing.JOptionPane;


public class MazeApplet extends Applet {
	
	private static final long serialVersionUID = -1726637537274194736L;


	private class Coord implements Comparable<Coord> {
		private final int i;
		private final int k;
		public Coord(int i , int k) {
			this.i = i;
			this.k = k;
		}
		
		public double dist ( ) {
			double di = i-end_i;
			double dk = k-end_k;
			return (di*di+dk*dk);
		}
		
		@Override
		public int compareTo(Coord o) {
			return Double.compare(dist(),o.dist());
		}
		
		public boolean equals(Object o) {
			Coord c = (Coord) o;
			return i==c.i&&k==c.k;
		}
		
	}
	
	
	Label lblheight = new Label("Height:"); 
	Label lblwidth = new Label("Width: "); 
	TextField txtheight = new TextField(5); 
	TextField txtwidth = new TextField(5); 
	Button btn_generate_maze = new Button("  Generate  ");
	Button btn_solve = new Button(" S O L V E ! ");
	Label lblnosol = new Label("NO SOLUTION");
	Panel pnlw;
	
	Panel panel = new Panel();
	
	private int start_i;
	private int start_k;
	private int end_i;
	private int end_k;
	
	private boolean checkMaze() {
		start_i = -1;
		start_k = -1;
		end_i = -1;
		end_k = -1;
		for ( int i = 0 ; i < width ; ++i ) {
			for ( int k = 0 ; k < height ; ++k ) {
				if ( maze[i][k] == START ) {
					if ( start_i != -1 ) return false;
					start_i = i; start_k = k;
				}
				if ( maze[i][k] == END ) {
					if ( end_i != -1 ) return false;
					end_i = i; end_k = k;
				}
			}
		}
		if ( start_i == -1 ) return false;
		if ( end_i == -1 ) return false; 
		return true;
	}
		
	private int getIdx(int i,int k) {
		return i*height+k;
	}
	

	public void init() {
		setSize(WIDTH,HEIGHT);
		panel.setLayout(new GridLayout(8,1));
		pnlw = new Panel();
		pnlw.add(lblwidth);
		txtwidth.setText(Integer.toString(width));
		txtheight.setText(Integer.toString(height));
		pnlw.add(txtwidth);
		panel.add(pnlw);
		pnlw = new Panel();
		pnlw.add(lblheight);
		pnlw.add(txtheight);
		panel.add(pnlw);
		pnlw = new Panel();
		pnlw.add(btn_generate_maze);
		panel.add(pnlw);
		pnlw = new Panel();
		pnlw.add(btn_solve);
		panel.add(pnlw);
		pnlw = new Panel();
		Button how_to = new Button("How to?");
		how_to.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MazeApplet.this,
					    "Click the GENERATE button to generate a new maze with preset start tile (yellow) and end tile (green).\n"+
					    "Next click SOLVE and the program will display the shortest path between the start and end tiles.\n"+
					    "You can change the start tile by right click and the end tile by middle click on the desired tile.\n"+
					    "You can also create your own mazes.\nSimply left click on any tile and it will change from wall (red) to room (gray).\n"+
					    "You can change multiple tiles by dragging the mouse with the left button pressed.\n"+
					    "You can specify width and height in the input fields (must be larger then 1 and smaller then 180).\n"+
					    "Have fun!\n"+
					    "Made by Andrej Gajduk.",
					    "How to use?",
					    JOptionPane.PLAIN_MESSAGE);
			}
		});
		pnlw.add(how_to);
		panel.add(pnlw);
		pnlw = new Panel();
		lblnosol.setForeground(Color.red);
		pnlw.add(lblnosol);
		panel.add(pnlw);
		
		//panel.add(how_to);
		pnlw.setVisible(false);
		this.setLayout(new BorderLayout());
		add(panel,BorderLayout.EAST);
		      resize(WIDTH,HEIGHT);
		
		a = Math.min(MAXMWIDTH/width,MAXMHEIGHT/height);
		btn_generate_maze.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				for ( int i = 0 ; i < width ; ++i ) {
					for ( int k = 0 ; k < height ; ++k ) {
						if ( maze[i][k] == ROOM ) maze[i][k] = WALL;
						if ( maze[i][k] == ROAD ) maze[i][k] = WALL;
					}
				}
				checkMaze();
				if ( start_i == -1 ) {
					if ( end_i != -1 ) {
						start_i = end_i;
						start_k = end_k;
					}
					while ( start_i == end_i&&start_k == end_k ) {
						start_i = (int)(Math.random()*width);
						start_k = (int)(Math.random()*height);
					}
				}
				if ( end_i == -1 ) {
					if ( start_i != -1 ) {
						end_i = start_i;
						end_k = start_k;
					}
					while ( start_i == end_i&&start_k == end_k ) {
						end_i = (int)(Math.random()*width);
						end_k = (int)(Math.random()*height);
					}
				}
				maze[start_i][start_k] = START;
				maze[end_i][end_k] = END;
				int di[] = { -1,1,0,0 };
				int dk[] = { 0,0,-1,1 };
				int count = 0;
				int max = (int) (width*height*1.0*(width*height*1.0/(2100) + 0.6));
				WeightedQuickUnionFindWithPathCompression uf = new WeightedQuickUnionFindWithPathCompression(width*height);
				long start = System.currentTimeMillis();
				while ( !uf.isConneted(getIdx(start_i, start_k), getIdx(end_i, end_k)) )  {
					++count;
					int ti = 0,tk = 0;
					while ( true ) {
						ti = (int)(Math.random()*width);
						tk = (int) (Math.random()*height);
						if ( maze[ti][tk] == WALL ) break;
					}
					maze[ti][tk] = ROOM;
					for ( int w = 0 ; w < di.length ; ++w ) {
						int ni = di[w]+ti;
						int nk = dk[w]+tk;
						try {
							if ( maze[ni][nk] != WALL ) {
								uf.union(getIdx(ti, tk),getIdx(ni,nk));
							}
						}
						catch (Exception e) {}
					}
					long current = System.currentTimeMillis();
					if ( current - start > 300 ) max = width*height;
					if ( count > max ) {
						--count;
						while ( true ) {
							ti = (int)(Math.random()*width);
							tk = (int) (Math.random()*height);
							if ( maze[ti][tk] == ROOM ) break;
						}
						maze[ti][tk] = WALL;
						uf = new WeightedQuickUnionFindWithPathCompression(width*height);
						for ( ti = 0 ; ti < width ; ++ti ) {
							for ( tk = 0 ; tk < height ; ++tk ) {
								if ( maze[ti][tk] == ROOM) {
									for ( int w = 0 ; w < di.length ; ++w ) {
										int ni = di[w]+ti;
										int nk = dk[w]+tk;
										try {
											if ( maze[ni][nk] != WALL ) {
												uf.union(getIdx(ti, tk),getIdx(ni,nk));
											}
										}
										catch (Exception e) {}
									}
								}
							}
						}
						
					}
				}
				btn_solve.setEnabled(true);
				repaint();
		
			}
		});
		btn_solve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Coord start = new Coord(start_i,start_k);
				Coord end = new Coord(end_i,end_k);
				boolean visited[][] = new boolean[width][height];
				visited[start.i][start.k] = true;
				Coord parent[][] = new Coord[width][height];
				PriorityQueue<Coord> queue = new PriorityQueue<MazeApplet.Coord>();
				queue.add(start);
				int di[] = { -1,1,0,0 };
				int dk[] = { 0,0,-1,1 };
				while ( ! queue.isEmpty() ) {
					Coord current = queue.poll();
					if ( current.equals(end) ) {
						current = parent[current.i][current.k];
						while ( ! current.equals(start) ) {
							maze[current.i][current.k] = ROAD;
							current = parent[current.i][current.k];
						}
						repaint();
						return;
					}
					for ( int w = 0 ; w < di.length ; ++w ) {
						int ni = di[w]+current.i;
						int nk = dk[w]+current.k;
						Coord next = new Coord(ni,nk);
						try {
							if ( ! visited[next.i][next.k] ) {
								if ( maze[next.i][next.k] != WALL ) {
									visited[next.i][next.k] = true;
									parent[next.i][next.k] = current;
									queue.add(next);
								}
							}
						}
						catch (Exception e) {}
					}
				}
				pnlw.setVisible(true);
				repaint();
			}
		});
		
		txtwidth.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					resetMaze(Integer.parseInt(txtwidth.getText()),height);
				}
				catch ( Exception e ) {
					txtwidth.setForeground(Color.RED);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				try {
					resetMaze(Integer.parseInt(txtwidth.getText()),height);
				}
				catch ( Exception e ) {
					txtwidth.setForeground(Color.RED);
				}
			}
		});
		txtheight.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					resetMaze(width,Integer.parseInt(txtheight.getText()));
				}
				catch ( Exception e ) {
					txtheight.setForeground(Color.RED);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				try {
					resetMaze(width,Integer.parseInt(txtheight.getText()));
				}
				catch ( Exception e ) {
					txtheight.setForeground(Color.RED);
				}
			}
		});
		
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if ( right_click ) return;
				if ( dragged ) return;
				int x = arg0.getX();
				int y = arg0.getY();
				if ( x <= LEFT+4 || x >= LEFT+4+width*a ||
					 y <= TOP+4 || y >= TOP+4+height*a ) {
						return;
				}
				int i = x-(LEFT+4);
				int k = y-(TOP+4);
				k /= a;
				i /= a;
				if ( maze[i][k] == WALL || maze[i][k] == ROOM ) {
					maze[i][k] = 1-maze[i][k];
					repaint();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				if ( x <= LEFT+4 || x >= LEFT+4+width*a ||
					 y <= TOP+4 || y >= TOP+4+height*a ) {
						return;
				}
				for ( int i = 0 ; i < width ; ++i ) {
					for ( int k = 0 ; k < height ; ++k ) {
						if ( maze[i][k] == ROAD ) maze[i][k] = ROOM;
					}
				}
				repaint();
				pnlw.setVisible(false);
				int i = x-(LEFT+4);
				int k = y-(TOP+4);
				k /= a;
				i /= a;
				//left click
				if ( arg0.getButton() == 1 ) {
					if ( maze[i][k] == WALL )
						adding_walls = false;
					else
						adding_walls = true;
					dragged = false;
					right_click = false;
				}
				else {
					checkMaze();
					//right click
					if ( arg0.getButton() == 3 ) {
						maze[start_i][start_k] = ROOM;
						start_i = i; start_k = k;
						maze[start_i][start_k] = START;
						repaint();
					}
					//middle click
					else {
						maze[end_i][end_k] = ROOM;
						end_i = i;end_k = k;
						maze[end_i][end_k] = END;
					}
					right_click = true;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if ( right_click ) return;
				dragged = true;
				int x = arg0.getX();
				int y = arg0.getY();
				if ( x <= LEFT+4 || x >= LEFT+4+width*a ||
					 y <= TOP+4 || y >= TOP+4+height*a ) {
						return;
				}
				int i = x-(LEFT+4);
				int k = y-(TOP+4);
				k /= a;
				i /= a;
				if ( adding_walls ) {
					if ( maze[i][k] == ROOM ) {
						maze[i][k] = 1-maze[i][k];
						repaint();
					}
				}
				else {
					if ( maze[i][k] == WALL ) {
						maze[i][k] = 1-maze[i][k];
						repaint();
					}
				}
			}
		});
		dim = getSize();  
        offscreen = createImage(dim.width,dim.height);
        bufferGraphics = offscreen.getGraphics(); 
        start_i = 0;
        start_k = 0;
        end_i = width-1;
        end_k = height-1;
        resetMaze(width,height);
        checkMaze();
    }//init
	 

	void resetMaze(int width, int height) {
		if ( height <= 1 || height > 180 ) 
			throw new ArrayIndexOutOfBoundsException();
		if ( width <= 1 || width > 180 )
			throw new ArrayIndexOutOfBoundsException();
		maze = new int[width][height];
		start_i = Math.min(width-1,start_i);
		start_k = Math.min(height-1,start_k);
		end_i = Math.min(width-1,end_i);
		end_k = Math.min(height-1,end_k);
		if ( start_i == end_i && start_k == end_k ) 
			start_i = end_i-1<0?end_i+1:end_i-1;
		this.width = width;
		this.height = height;
		a = Math.min(MAXMWIDTH/width,MAXMHEIGHT/height);
        maze[start_i][start_k] = START;
        maze[width-1][height-1] = END;
		txtheight.setForeground(Color.black);
		txtwidth.setForeground(Color.black);
		repaint();
	}


	private final int WIDTH = 600;
	private final int HEIGHT = 400;
	private final int LEFT = 13;
	private final int TOP = 13;
	//420
	private final int MAXMWIDTH = 420;
	private final int MAXMHEIGHT = 360;
	private final int LLINE = 460;
	
	private int mwidth;
	private int mheight;
	

	private int width = 42;
	private int height = 36;
	
	private int a;
	
	private boolean adding_walls;
	private boolean dragged;
	
	private boolean right_click;
	
	private int ROOM = 0;
	private int WALL = 1;
	private int START = 2;
	private int END = 3;
	private int ROAD = 4;
	
	int maze[][] = new int[width][height];
	Graphics bufferGraphics; 
    // The image that will contain everything that has been drawn on 
    // bufferGraphics. 
    Image offscreen; 
    // To get the width and height of the applet. 
    Dimension dim; 
	
	public void paint(Graphics g) {
		  bufferGraphics.clearRect(0,0,dim.width,dim.width); 
		  int a = Math.min(MAXMWIDTH/width,MAXMHEIGHT/height);
		  mwidth = a*width+9;
		  mheight = a*height+9;
		  bufferGraphics.setColor(Color.BLACK);
	      Graphics2D g2 = (Graphics2D) bufferGraphics;
	      g2.setStroke(new BasicStroke(2));
	      bufferGraphics.drawRect(LEFT,TOP, mwidth, mheight);
	      bufferGraphics.drawLine(LLINE, 0, LLINE, HEIGHT);
	      g2.setStroke(new BasicStroke(1));
	      bufferGraphics.drawRect(LEFT+4,TOP+4, mwidth-9, mheight-9);
	      for ( int i = 0 ; i < width ; ++i ) {
	    	  for ( int k = 0 ; k < height ; ++k ) {
	    		  int x = LEFT+4+i*a;
	    		  int y = TOP+4+k*a;
	    		  if ( maze[i][k] == ROOM )
	    			  bufferGraphics.setColor(Color.GRAY);
	    		  if ( maze[i][k] == WALL )
	    			  bufferGraphics.setColor(Color.RED);
	    		  if ( maze[i][k] == START )
	    			  bufferGraphics.setColor(Color.YELLOW);
	    		  if ( maze[i][k] == END )
	    			  bufferGraphics.setColor(Color.GREEN);
	    		  if ( maze[i][k] == ROAD )
	    			  bufferGraphics.setColor(Color.BLUE);
	    		  bufferGraphics.fillRect(x, y, a, a);
	    		  bufferGraphics.setColor(Color.black);
	    		  bufferGraphics.drawRect(x, y, a, a);
	    	  }
	      }
	      g.drawImage(offscreen,0,0,this); 
	}//paint
	 

}
