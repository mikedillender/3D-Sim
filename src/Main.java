import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener {

    //BASIC VARIABLES
    private final int WIDTH=1280, HEIGHT=900;
    ArrayList<Object> objects=new ArrayList<>();
    Vec3f pos=new Vec3f(0,0,0);
    Vec2f orient=new Vec2f(0,0);
    float orientfromcenter=0;
    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;

    //COLORS
    Color background=new Color(255, 255, 255);
    Color gridColor=new Color(150, 150,150);


    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        objects.add(new Object(0,new Vec3f(50,0,0),new Vec3f(0,0,0)));
        objects.add(new Object(0,new Vec3f(50,20,0),new Vec3f(0,0,0)));
        thread=new Thread(this);
        thread.start();
    }

    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(background);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        int pSize=3;
        int pw=WIDTH/pSize;
        int ph=HEIGHT/pSize;
        //float fovx=3.14f*2/3;
        //float fovy=3.14f/2;
        int cx=pw/2;
        int cy=ph/2;
        int lensdist=cy;
        for (int x=0; x<pw; x++){
            float xor=(float)(Math.atan2(x-cx,lensdist));
            for(int y=0; y<ph; y++){
                float yor=(float)(Math.atan2(y-cy,lensdist));
                gfx.setColor(getColorInDir(xor,yor));
                gfx.fillRect(pSize*x,pSize*y,pSize,pSize);
            }
        }

        //FINAL
        g.drawImage(img,0,0,this);
    }

    public Color getColorInDir(float xor,float yor){
        Vec2f o1=new Vec2f(orient.x+xor,orient.y+yor);
        for (Object o : objects){
            if (o.doesLineCross(o1,pos)){
                return Color.BLUE;
            }
        }
        return Color.WHITE;
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME

            //UPDATES


            repaint();//UPDATES FRAME
            try{ Thread.sleep(15); } //ADDS TIME BETWEEN FRAMES (FPS)
            catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    //INPUT
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_RIGHT){
            orient.x+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_LEFT){
            orient.x-=.2f;
        }if (e.getKeyCode()==KeyEvent.VK_UP){
            orient.y+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
            orient.y-=.2f;
        }if (e.getKeyCode()==KeyEvent.VK_A){
            pos.x+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_D){
            pos.x-=.2f;
        }if (e.getKeyCode()==KeyEvent.VK_W){
            pos.y+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_S){
            pos.y-=.2f;
        }
    }
    public void keyReleased(KeyEvent e) {

    }
    public void keyTyped(KeyEvent e) { }

    //QUICK METHOD I MADE TO DISPLAY A COORDINATE GRID

}