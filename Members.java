package test4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

// voice
class AePlayWave extends Thread {

	private String filename;
	public AePlayWave(String wavfile) {
		filename = wavfile;
	}
	public void run() {
		File soundFile = new File(filename);
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		auline.start();
		int nBytesRead = 0;
		byte[] abData = new byte[1024];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}
	}
}

class Node {
	int x;
	int y;
	int direct;
	public Node(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
}

class Recorder {
	// record number of enemy tank in each stage
	private static int enNum = 20;
	// number of my hero
	private static int myLife = 3;
	// record the number of enemy tank been killed
	private static int allEnNum = 0;
	// recover the node from file
	static Vector<Node> nodes = new Vector<>();
	
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	
	private static FileReader fr = null;
	private static BufferedReader br = null;
	private Vector<EnemyTank> ets = new Vector<>();
	
	public Vector<Node> getNodesAndEnNums() {
		try {
			fr = new FileReader("d:\\myRecording.txt");
			br = new BufferedReader(fr);
			String n = "";
			// read the first line
			n = br.readLine();
			allEnNum = Integer.parseInt(n);
			while ((n = br.readLine()) != null) {
				String[] xyz = n.split(" ");				
				Node node = new Node(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
				nodes.add(node);				
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nodes;
	}
	
	public Vector<EnemyTank> getEts() {
		return ets;
	}
	public void setEts(Vector<EnemyTank> ets) {
		this.ets = ets;
	}
	// save number of enemy tank, coordination and direction
	public void keepRecAndEnemyTank() {
		try {
			fw = new FileWriter("d:\\myRecording.txt");
			bw = new BufferedWriter(fw);
			
			bw.write(allEnNum + "\r\n");
			// save live enemy tank's coordination and direction
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				if (et.isAlive) {
					String record = et.x + " " + et.y + " " + et.direct;
					// write to file
					bw.write(record + "\r\n"); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// read from file
	public static void getRecording() {
		try {
			fr = new FileReader("d:\\myRecording.txt");
			br = new BufferedReader(fr);
			String n = br.readLine();
			allEnNum = Integer.parseInt(n);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void keepRecording() {
		try {
			fw = new FileWriter("d:\\myRecording.txt");
			bw = new BufferedWriter(fw);
			
			bw.write(allEnNum + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getAllEnNum() {
		return allEnNum;
	}
	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMuLife(int myLife) {
		Recorder.myLife = myLife;
	}
	// enemy tank decrease
	public static void reduceEnNum() {
		enNum--;
	}
	public static void addEnNumRec() {
		allEnNum++;
	}
}

class Bomb {
	int x;
	int y;
	int life = 9;
	boolean isAlive = true;
	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;
	}
	// life decreases
	public void lifeDown() {
		if (life > 0) {
			life--;
		} else {
			this.isAlive = false;
		}
	}
}

class Bullet implements Runnable {
	int x;
	int y;
	int direct;
	int speed = 1;
	boolean isAlive = true;
	public Bullet(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(50);
			} catch(Exception e) {
				e.printStackTrace();
			}
			switch(direct) {
			case 0:
				y -= speed;
				break;
			case 1:
				x += speed;
				break;
			case 2:
				y += speed;
				break;
			case 3:
				x -= speed;
				break;
			}
			// when the bullet will die?
			// whether the bullet is at the edge of panel
			if (x < 0 || x > 400 || y < 0 || y > 300) {
				this.isAlive = false;
				break;
			}
		}
	}
}

class Tank {
	int x = 0;
	int y = 0;
	// 0 - up, 1 - right, 2 - down, 3 - left
	int direct = 0;
	int speed = 1;
	int color;
	boolean isAlive = true;
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
// Enemy Tank, thread
class EnemyTank extends Tank implements Runnable {
	int times = 0;
	// define a vector which can visit all enemy tanks on the panel
	Vector<EnemyTank> ets = new Vector<>();
	Vector<Bullet> bb = new Vector<>();
	// enemy bullet 
	public EnemyTank(int x, int y) {
		super(x, y);
	}
	// get tank vector on MyPanel
	public void setEts(Vector<EnemyTank> vv) {
		this.ets = vv;
	}
	// whether meet the other enemy tank
	public boolean isTouchOtherEnemy() {
		boolean touch = false;
		switch (this.direct) {
		case 0:
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				if (et != this) {
					// other enemy tank direction is up or down
					if (et.direct == 0 || et.direct == 2) {
						if (this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if (this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x >= et.x && this.x <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if (this.x + 20 >= et.x && this.x + 20 <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
					}
				}
			}
			break;
		case 1:
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				if (et != this) {
					// other enemy tank direction is up or down
					if (et.direct == 0 || et.direct == 2) {
						if (this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if (this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x + 30 >= et.x && this.x + 30 <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if (this.x + 30 >= et.x && this.x + 30 <= et.x + 30 && this.y + 20 >= et.y && this.y + 20 <= et.y + 20) {
							return true;
						}
					}
				}
			}
			break;
		case 2:
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				if (et != this) {
					// other enemy tank direction is up or down
					if (et.direct == 0 || et.direct == 2) {
						if (this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if (this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x >= et.x && this.x <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if (this.x + 20 >= et.x && this.x + 20 <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
					}
				}
			}
			break;
		case 3:
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				if (et != this) {
					// other enemy tank direction is up or down
					if (et.direct == 0 || et.direct == 2) {
						if (this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if (this.x >= et.x && this.x <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x >= et.x && this.x <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if (this.x >= et.x && this.x <= et.x + 30 && this.y + 20 >= et.y && this.y + 20 <= et.y + 20) {
							return true;
						}
					}
				}
			}
			break;
		}
		return touch;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {			
			switch (this.direct) {
			case 0:
				for (int i = 0; i < 30; i++) {
					if (y > 0 && !this.isTouchOtherEnemy()) {
						y -= speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				for (int i = 0; i < 30; i++) {
					if (x < 400 && !this.isTouchOtherEnemy()) {
						x += speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				for (int i = 0; i < 30; i++) {
					if (y < 300 && !this.isTouchOtherEnemy()) {
						y += speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				for (int i = 0; i < 30; i++) {
					if (x > 0 && !this.isTouchOtherEnemy()) {
						x -= speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
			this.times++;
			if (times % 2 == 0) {			
				if (isAlive) {
					if (bb.size() < 5) {
						Bullet b = null;
						switch (direct) {
						case 0:
							b = new Bullet(x + 10, y, 0);
							bb.add(b);
							break;
						case 1:
							b = new Bullet(x + 30, y + 10, 1);
							bb.add(b);
							break;
						case 2:
							b = new Bullet(x + 10, y + 30, 2);
							bb.add(b);
							break;
						case 3:
							b = new Bullet(x, y + 10, 3);
							bb.add(b);
							break;
						}
						Thread t = new Thread(b);
						t.start();
					}
				}
			}
			// create a random direction
			this.direct = (int)(Math.random() * 4);
			// if the enemy tank is dead, quit the thread
			if (!this.isAlive) {
				break;
			}
		}
	}
}
// My Tank name: Hero
class Hero extends Tank {
	//Bullet b = null;
	Vector<Bullet> bb = new Vector<>();
	Bullet b = null;
	public Hero(int x, int y) {
		super(x, y);
	}
	// fire
	public void shotEnemy() {
		
		switch(this.direct) {
		case 0:
			b = new Bullet(x + 10, y, 0); 
			bb.add(b);
			break;
		case 1:
			b = new Bullet(x + 30, y + 10, 1);
			bb.add(b);
			break;
		case 2:
			b = new Bullet(x + 10, y + 30, 2);
			bb.add(b);
			break;
		case 3:
			b = new Bullet(x, y + 10, 3);
			bb.add(b);
			break;
		}
		Thread t = new Thread(b);
		t.start();
	}
	public void moveUp() {
		y -= speed;
	}
	public void moveRight() {
		x += speed;
	}
	public void moveDown() {
		y += speed;
	}
	public void moveLeft() {
		x -= speed;
	}
}