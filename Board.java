import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class Board extends JPanel implements Runnable, MouseListener
{
    boolean ingame = false;
    private Dimension d;
    int BOARD_WIDTH=500;
    int BOARD_HEIGHT=500;
    int x = 0;
    BufferedImage alienImage;
    BufferedImage shotImage;
    BufferedImage playerImage;
    BufferedImage bombImage;
    String message = "Click Board to Start";
    private Thread animator;
    
    gameChar pl = new Player(BOARD_WIDTH/2 - 50,BOARD_HEIGHT - 100);
    gameChar shot = new Shot(pl.x + 50,pl.y);
    gameChar bomb = new Shot(pl.x + 50,pl.y);
    Alien[][] al = new Alien[3][10];
    int count = 0;
    int bombCount = 4;
    String bombString = "Missiles: " + bombCount;
    int key = 0;
    ArrayList<Integer> shotsx = new ArrayList<Integer>();
    ArrayList<Integer> shotsy = new ArrayList<Integer>();
    
    ArrayList<Integer> bombx = new ArrayList<Integer>();
    ArrayList<Integer> bomby = new ArrayList<Integer>();
    
    private boolean moveRight = false;
    private boolean moveLeft = false;
    boolean isVis = true;
    double timer;
    double secondTimer;
    boolean normalFire = true;
    boolean tripleFire = false;
    boolean drawStart;
    int score = 0;
    String scoreString = "Score: " + score;
    int alienX;
    
    boolean click = false;
    
    public Board()
    {
        addKeyListener(new TAdapter());
        addMouseListener(this);
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.gray);
        
        moveRight = false;
        moveLeft = false;
        isVis = true;
        normalFire = true;
        tripleFire = false;
        drawStart = true;
        message= "Play";
        
        int alx = 10;
        int aly = 10;
        
        
        for(int r=0; r<al.length; r++){
            for(int c = 0; c<al[0].length; c++){
                al[r][c] = new Alien(alx, aly);
                alx += 30;
            }
            alx = 10;
            aly += 30;
        }
        
        try {
            alienImage = ImageIO.read(this.getClass().getResource("alien.png"));    
            shotImage = ImageIO.read(this.getClass().getResource("shot.png"));
            bombImage = ImageIO.read(this.getClass().getResource("missile.png"));
            playerImage = ImageIO.read(this.getClass().getResource("player.png"));
        } catch (IOException e) {
            System.out.println("Image could not be read");
            // System.exit(1);
        }
            
        if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }
                   
        setDoubleBuffered(true);
    }
    
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(Color.gray);
        g.fillRect(0, 0, d.width, d.height);
        
        Font small = new Font("Helvetica", Font.BOLD,50);
        FontMetrics metr = this.getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, d.width/2 - ((message.length()) * 12), d.height-230);
        
            int missileCount = bombCount - 1;
            if(missileCount <= 0){
                missileCount = 0;
            }
            String missileString = "Missiles: " + missileCount;
            
            Font scoreFont = new Font("Helvetica", Font.BOLD,20);
            FontMetrics metrics = this.getFontMetrics(small);
            g.setColor(Color.white);
            g.setFont(scoreFont);
            g.drawString(scoreString, 20, d.height- 35);
            g.drawString(missileString, 380, d.height- 35);
        
        if (ingame) {
            
            g.drawImage(playerImage,(int)pl.x,(int)pl.y,100,50 ,null);
            timer ++;
            secondTimer= timer/150;
            message = "";
            
            for(int r=0; r<al.length; r++){
                for(int c = 0; c<al[0].length; c++){
                    alienX = (int)al[r][c].x;
                    g.drawImage(alienImage,alienX, (int)al[r][c].y, 30, 25, null);                    
                }
            }   
                    
            //stars
            g.setColor(Color.white);
            g.fillOval(10,10,5,5);
            g.fillOval(50,30,5,5);
            g.fillOval(90,120,5,5);
            g.fillOval(10,360,5,5);
            g.fillOval(30,280,5,5);
            g.fillOval(100,20,5,5);
            g.fillOval(100,10,5,5);
            g.fillOval(250,30,5,5);
            g.fillOval(390,120,5,5);
            g.fillOval(410,360,5,5);
            g.fillOval(330,280,5,5);
            g.fillOval(100,200,5,5);
            g.fillOval(160,30,5,5);
            g.fillOval(290,100,5,5);
            g.fillOval(340,390,5,5);
            g.fillOval(130,180,5,5);
            g.fillOval(430,400,5,5);
            g.fillOval(290,300,5,5);
            g.fillOval(320,410,5,5);
            g.fillOval(240,330,5,5);
            g.fillOval(390,440,5,5);
            
            for(int r = 0; r < al.length; r++){
                for(int c = 0; c < al[0].length; c++){
                    if((al[r][c].y-10)%60 ==0){
                        if((al[r][c].x + 14)>(BOARD_WIDTH-13)){
                            al[r][c].y += 30; 
                        }else
                            al[r][c].x += 1; 
                    }else{
                        if((al[r][c].x - 14)<10){
                            al[r][c].y += 30; 
                        }else
                            al[r][c].x -= 1; 
                    }
                    
                    if(al[r][c].y >= pl.y && al[r][c].y < 5000){
                          message= "You Lose";
                          ingame = false;   
                    }
                    
                }
            }
            
            
            for(int r = 0; r < al.length; r++){
                for(int c = 0; c < al[0].length; c++){
                    for(int i=0; i<shotsx.size(); i++){
                        if(al[r][c].y >= shotsy.get(i) - 10 && al[r][c].y <= shotsy.get(i) + 10 && al[r][c].x >= shotsx.get(i) - 15 && al[r][c].x <= shotsx.get(i) + 15 ){
                           shotsx.remove(i);
                           shotsy.remove(i);
                           al[r][c].y = 5000;
                           count ++;
                           score += 5;
                           scoreString = "Score: " + score;
                        }
                    }
                }
            }
            
            if(bombCount >= 0){
            for(int r = 0; r < al.length; r++){
                for(int c = 0; c < al[0].length; c++){
                    for(int i=0; i<bombx.size(); i++){
                        if(al[r][c].y >= bomby.get(i) - 10 && al[r][c].y <= bomby.get(i) + 10 && al[r][c].x >= bombx.get(i) - 18 && al[r][c].x <= bombx.get(i) + 18){
                           bombx.remove(i);
                           bomby.remove(i);
                           
                           al[r][c].y = 5000;
                           count ++;
                           score += 5;
                           scoreString = "Score: " + score;
                           
                           
                           if(al[r][c].y != 5000){
                               al[r][c].y = 5000;
                               count ++;
                               score += 5;
                           }
                           
                           if(c < 10){
                               if(al[r][c+1].y != 5000){
                                   al[r][c+1].y = 5000;
                                   count ++;
                                }
                               if(r < 3){
                                   if(al[r+1][c+1].y != 5000){
                                       al[r][c+1].y = 5000;
                                       count ++;
                                    }
                               }
                               if(r > 1){
                                   if(al[r-1][c+1].y != 5000){
                                       al[r][c].y = 5000;
                                       count ++;
                                       score += 5;                           
                                    }  
                                }
                           }
                           
                           if(c > 1){
                               if(al[r][c-1].y != 5000){
                                   al[r][c-1].y = 5000;
                                   count ++;
                                   score += 5;
                               }  
                               if(r < 3){
                                   if(al[r+1][c-1].y != 5000){
                                       al[r][c-1].y = 5000;
                                       count ++;
                                       score += 5;
                                    }
                                }
                               if(r > 1){
                                   if(al[r-1][c-1].y != 5000){
                                       al[r+1][c].y = 5000;
                                       count ++;
                                       score += 5;
                                    }
                                }
                           }
                           
                           if(r < 3){
                               if(al[r+1][c].y != 5000){
                                   al[r][c].y = 5000;
                                   count ++;
                                   score += 5;
                                }   
                           }
                           if(r > 1){
                               if(al[r-1][c].y != 5000){
                                   al[r][c].y = 5000;
                                   count ++;
                                   score += 5;                           
                                } 
                            }
                           scoreString = "Score: " + score;
                           
                        }
                    }
                }
            }
                bombString = "Missiles: " + bombCount;
            
            } else {
                bombCount = 0;
            }
            
            if (moveRight && pl.x <= 450){
                pl.x += 1.25;
            } 
            if (moveLeft && pl.x >= -50){
                pl.x -= 1.25;
            }
            for(int i=0; i<shotsx.size(); i++){
                g.drawImage(shotImage,shotsx.get(i),shotsy.get(i),10,18 ,null);  
            }
            for(int i=0; i<shotsy.size(); i++){
                shotsy.set(i, shotsy.get(i) - 1); 
            }
                  
            if(bombCount > 0){
            for(int i=0; i<bombx.size(); i++){
                g.drawImage(bombImage,bombx.get(i) - 10,bomby.get(i),25,20 ,null);  
            }
            for(int i=0; i<bomby.size(); i++){
                bomby.set(i, bomby.get(i) - 1); 
            }
            }
            
            if(count >= 30){
                message = "You Win!";
                ingame = false;
            }
            
        }
                
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if(key==37){
                moveLeft = false;
            }
            if(key==39){
                moveRight = false;
            }    
            if(key == 32){
               
            }
        }

        public void keyPressed(KeyEvent e) {
            //System.out.println( e.getKeyCode());
            // message = "Key Pressed: " + e.getKeyCode();
            
            int key = e.getKeyCode();
            if(key==37){
                moveLeft = true;
            }
            if(key==39){
                moveRight = true;
            }    
            if(key==32){
                shotsx.add((int)pl.x + 50);
                shotsy.add((int)pl.y);; 
            }
            if(key==10){
                bombx.add((int)pl.x + 50);
                bomby.add((int)pl.y);; 
                bombCount --;
            }
        }
    }
    
    
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        ingame = true; 
    }

    public void mouseReleased(MouseEvent e) {
        //click = false;
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
        int animationDelay = 1;
        long time = 
            System.currentTimeMillis();
            while (true) {//infinite loop
                // spriteManager.update();
                repaint();
                try {
                    time += animationDelay;
                    Thread.sleep(Math.max(0,time - 
                    System.currentTimeMillis()));
                }catch (InterruptedException e) {
                    System.out.println(e);
                }//end catch
            }//end while loop

        }//end of run
}//end of class
