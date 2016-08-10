package test4;

/**
 * function: Tank Game 2.0
 * 1, Draw tanks
 * 2, Tank can move
 * 3, Tank can fire bullets, at most five 
 * 4, when my hero's bullet hit the enemy, enemy tank will disappear(explosion)
 * 5, my hero was hit, need explosion
 * 6, enemy tank cannot appear over each other
 * 	6.1, put the collision function to the class of EnemTank 
 * 7, different game levels
 * 	7.1, an empty panel at the beginning of game
 * 	7.2, flicker
 * 8, pause and continue a game
 * 	8.1, when pause, set the speed of bullet and tank to 0, and not change the direction
 * 9, recode the score of player
 * 	9.1, file
 * 	9.2, another class of record
 * 	9.3, save the number of enemy tanks been killed
 * 	9.4, save an quit, coordination of tank and continue the game
 * 10, java need to add voice file to the game
 * 	10.1, operations on voice file
 * 11, web application(to be continued)
 */

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;


public class MyTankGame5 extends JFrame implements ActionListener {
	
	MyPanel mp = null;
	MyStartPanel msp = null;
	// menu
	JMenuBar jmb = null;
	JMenu jm1 = null;
	JMenuItem jmi1 = null;
	// quit
	JMenuItem jmi2 = null;
	// save and quit
	JMenuItem jmi3 = null;
	// continue
	JMenuItem jmi4 = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyTankGame5 myg = new MyTankGame5();
	}
	// construct function
	public MyTankGame5() {
//		mp = new MyPanel();
//		Thread t = new Thread(mp);
//		t.start();
//		this.add(mp);
//		this.addKeyListener(mp);
		jmb = new JMenuBar();
		jm1 = new JMenu("Game(G)");
		jm1.setMnemonic('G');
		jmi1 = new JMenuItem("start a new game(N)");
		jmi2 = new JMenuItem("quit the game(E)");
		jmi3 = new JMenuItem("save and quit the game(C)");
		jmi4 = new JMenuItem("continue the game(S)");
		
		jmi4.addActionListener(this);
		jmi4.setActionCommand("continue");		
		jmi3.addActionListener(this);
		jmi3.setActionCommand("saveQuit");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jmi1.setMnemonic('N');
		// respond to jmi1
		jmi1.addActionListener(this);
		jmi1.setActionCommand("newgame");
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jm1.add(jmi4);
		
		jmb.add(jm1);
		
		msp = new MyStartPanel();
		Thread t = new Thread(msp);
		t.start();
		this.setJMenuBar(jmb);
		this.add(msp);
		this.setSize(600, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals("newgame")) {
			// create battle field panel
			mp = new MyPanel("newGame");
			Thread t = new Thread(mp);
			t.start();
			// need to remove the old panel first
			this.remove(msp);
			this.add(mp);
			this.addKeyListener(mp);
			// refresh the panel
			this.setVisible(true);
		} else if (arg0.getActionCommand().equals("exit")) {
			// save the number of enemy tank been killed
			Recorder.keepRecording();
			System.exit(0);
		} else if (arg0.getActionCommand().equals("saveQuit")) {
			Recorder rd = new Recorder();
			rd.setEts(mp.ets);
			rd.keepRecAndEnemyTank();;
			System.exit(0);
		} else if (arg0.getActionCommand().equals("continue")) {
			// create battle field panel
			mp = new MyPanel("continue");
			
			Thread t = new Thread(mp);
			t.start();
			// need to remove the old panel first
			this.remove(msp);
			this.add(mp);
			this.addKeyListener(mp);
			// refresh the panel
			this.setVisible(true);
		}
	}

}
class MyStartPanel extends JPanel implements Runnable {
	int times = 0;
	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		if (times % 2 == 0) {
			g.setColor(Color.yellow);		
			g.drawString("Stage: 1", 150, 150);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			times++;
			this.repaint();
		}
	}
}
// my panel
class MyPanel extends JPanel implements KeyListener, Runnable {
	// define my tank - hero
	Hero hero = null;
	
	// the enemy tank vector
	Vector<EnemyTank> ets = new Vector<>();
	Vector<Node> nodes = new Vector<>();
	// define bomb vector
	Vector<Bomb> bombs = new Vector<>();
	int enSize = 3;
	// define three pictures, one bomb
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;
	// construct function
	public MyPanel(String flag) {
		Recorder.getRecording();
		
		hero = new Hero(100, 100);
		if (flag.equals("newGame")) {
			// initialize enemy tank
			for (int i = 0; i < enSize; i++) {
				EnemyTank et = new EnemyTank((i + 1) * 50, 0);
				et.setColor(0);
				et.setDirect(2);
				// 
				et.setEts(ets);
				Thread t = new Thread(et);
				t.start();
				Bullet b = new Bullet(et.x + 10, et.y + 30, 2);
				et.bb.add(b);
				Thread t2 = new Thread(b);
				t2.start();
				ets.add(et);
			}
		} else {
			nodes = new Recorder().getNodesAndEnNums();
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				EnemyTank et = new EnemyTank(node.x, node.y);
				et.setColor(0);
				et.setDirect(node.direct);
				// 
				et.setEts(ets);
				Thread t = new Thread(et);
				t.start();
				Bullet b = new Bullet(et.x + 10, et.y + 30, 2);
				et.bb.add(b);
				Thread t2 = new Thread(b);
				t2.start();
				ets.add(et);
			}
		}
		try {
			image1 = ImageIO.read(new File("bomb_1.gif"));
			image2 = ImageIO.read(new File("bomb_2.gif"));
			image3 = ImageIO.read(new File("bomb_3.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// voice
		AePlayWave apw = new AePlayWave("d:\\cartoon005.wav");
		apw.start();
		// initialize pictures, it's not good
//		image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
//		image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
//		image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
	}
	// draw the information about the game
	public void showInfo(Graphics g) {
		
		this.drawTank(80, 330, g, 0, 0);
		g.setColor(Color.BLACK);
		g.drawString(Recorder.getEnNum() + "", 110, 350);
		this.drawTank(130, 330, g, 0, 1);
		g.setColor(Color.BLACK);
		g.drawString(Recorder.getMyLife() + "", 165, 350);
		// draw the score 
		g.setColor(Color.black);
		g.drawString("Your score:", 420, 30);
		g.setColor(Color.black);
		g.drawString(Recorder.getAllEnNum() + "", 460, 80);
		
		this.drawTank(420, 60, g, 0, 0);
	}
	// re-paint
	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		// draw information
		this.showInfo(g);
		// draw my hero tank
		if (hero.isAlive) {
			this.drawTank(hero.getX(), hero.getY(), g, this.hero.direct, 1);
		}
		for (int i = 0; i < hero.bb.size(); i++) {
			Bullet myBullet = hero.bb.get(i);
			// draw one bullet
			if (myBullet != null && myBullet.isAlive) {
				g.draw3DRect(myBullet.x, myBullet.y, 1, 1, false);
			}
			if (!myBullet.isAlive) {
				hero.bb.remove(myBullet);
			}
		}
		// draw the bomb
		for (int i = 0; i < bombs.size(); i++) {
			Bomb bomb = bombs.get(i);
			if (bomb.life > 6) {
				g.drawImage(image1, bomb.x, bomb.y, 30, 30, this);
			} else if (bomb.life > 4) {
				g.drawImage(image2, bomb.x, bomb.y, 30, 30, this);
			} else {
				g.drawImage(image3, bomb.x, bomb.y, 30, 30, this);
			}
			bomb.lifeDown();
			if (bomb.life == 0) {
				bombs.remove(bomb);
			}
		}
		// draw the enemy tank
		for (int i = 0; i < ets.size(); i++) {
			EnemyTank et = ets.get(i);
			if (et.isAlive) {
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), 0);	
				for (int j = 0; j < et.bb.size(); j++) {
					Bullet enemyBullet = et.bb.get(j);
					if (enemyBullet.isAlive) {
						g.draw3DRect(enemyBullet.x, enemyBullet.y, 1, 1, false);
					} else {
						et.bb.remove(enemyBullet);
					}
				}
			}
		}
	} 
	// whether enemy's bullet hit my hero
	public void hitMe() {
		// for every enemy tank
		for (int i = 0; i < this.ets.size(); i++) {
			EnemyTank et = ets.get(i);
			for (int j = 0; j < et.bb.size(); j++) {
				Bullet enemyBullet = et.bb.get(j);
				if (hero.isAlive) {
					if (this.hitTank(enemyBullet, hero)) {
						
					}
				}
			}
		}
	}
	// whether my hero's bullet hit enemy tank
	public void hitEnemyTank() {
		// whether hit
		for (int i = 0; i < hero.bb.size(); i++) {
			Bullet myBullet = hero.bb.get(i);
			// whether the bullet is alive
			if (myBullet.isAlive) {
				// for each enemy tank, whether the bullet hit the tank
				for (int j = 0; j < ets.size(); j++) {
					// get each enemy tank
					EnemyTank et = ets.get(j);
					if (et.isAlive) {
						if (this.hitTank(myBullet, et)) {
							Recorder.reduceEnNum();
							Recorder.addEnNumRec();
						}
					}
				}
			}
		}
	}
	// whether the bullet hits enemy tank
	public boolean hitTank(Bullet b, Tank et) {
		boolean flag = false;
		// the direction of enemy tank
		switch (et.direct) {
		// the direction of enemy tank is up or down
		case 0:
		case 2:
			if (b.x > et.x && b.x < et.x + 20 && b.y > et.y && b.y < et.y + 30) {
				// bullet will die
				b.isAlive = false;
				// enemy tank will die
				et.isAlive = false;
				flag = true;
				// create a bomb, add to Vector<Bomb>
				Bomb bomb = new Bomb(et.x, et.y);
				bombs.add(bomb);
			}
			break;
		// the direction of enemy tank is left or right
		case 1:
		case 3:
			if (b.x > et.x && b.x < et.x + 30 && b.y > et.y && b.y < et.y + 20) {
				b.isAlive = false;
				et.isAlive = false;
				flag = true;
				Bomb bomb = new Bomb(et.x, et.y);
				bombs.add(bomb);
			}
			break;
		}
		return flag;
	}
	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		// type of tanks, my hero or enemy
		switch(type) {
		case 0:
			g.setColor(Color.cyan);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		switch(direct) {
		case 0:
			// the left rectangle
			g.fill3DRect(x, y, 5, 30, false);
			// the right rectangle
			g.fill3DRect(x + 15, y, 5, 30, false);
			// the central rectangle
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			// the circle
			g.fillOval(x + 5, y + 10, 10, 10);
			// the line
			g.drawLine(x + 10, y + 15, x + 10, y);
			break;
		case 1:
			// the top rectangle
			g.fill3DRect(x, y, 30, 5, false);
			// the bottom rectangle
			g.fill3DRect(x, y + 15, 30, 5, false);
			// the central rectangle
			g.fill3DRect(x + 5, y + 5, 20, 10, false);
			// the circle
			g.fillOval(x + 10, y + 5, 10, 10);
			// the line
			g.drawLine(x + 15, y + 10, x + 30, y + 10);
			break;
		case 2:
			// the left rectangle
			g.fill3DRect(x, y, 5, 30, false);
			// the right rectangle
			g.fill3DRect(x + 15, y, 5, 30, false);
			// the central rectangle
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			// the circle
			g.fillOval(x + 5, y + 10, 10, 10);
			// the line
			g.drawLine(x + 10, y + 15, x + 10, y + 30);
			break;
		case 3:
			// the top rectangle
			g.fill3DRect(x, y, 30, 5, false);
			// the bottom rectangle
			g.fill3DRect(x, y + 15, 30, 5, false);
			// the central rectangle
			g.fill3DRect(x + 5, y + 5, 20, 10, false);
			// the circle
			g.fillOval(x + 10, y + 5, 10, 10);
			// the line
			g.drawLine(x + 15, y + 10, x, y + 10);
			break;
		}
	}
	// a - left, s - down, w - up, d - right
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == KeyEvent.VK_W) {
			// My hero's direct
			this.hero.setDirect(0);
			this.hero.moveUp();
		} else if (arg0.getKeyCode() == KeyEvent.VK_D) {
			// My hero's direct
			this.hero.setDirect(1);
			this.hero.moveRight();
		} else if (arg0.getKeyCode() == KeyEvent.VK_S) {
			// My hero's direct
			this.hero.setDirect(2);
			this.hero.moveDown();
		} else if (arg0.getKeyCode() == KeyEvent.VK_A) {
			// My hero's direct
			this.hero.setDirect(3);
			this.hero.moveLeft();
		} 
		// press key j, fire
		if (arg0.getKeyCode() == KeyEvent.VK_J) { 
			if (this.hero.bb.size() < 5) {
				this.hero.shotEnemy();
			}
		} 
		// must repaint panel
		this.repaint();
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// repaint for every 100 milliseconds
		while (true) {
			try {
				Thread.sleep(100);
			} catch(Exception e) {
				e.printStackTrace();
			}
			this.hitEnemyTank();
			this.hitMe();
			this.repaint();
		}
	}
}
