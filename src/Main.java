import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener, FrameData {

    //BASIC VARIABLES
    private final int WIDTH=1280, HEIGHT=900;
    ArrayList<Object> objects=new ArrayList<>();
    Vec3f pos=new Vec3f(-130,0,0);
    Vec2f orient=new Vec2f(0,0);
    float orientfromcenter=0;
    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;
    boolean gravon=true;
    Object frame=new Object(1,null,null,0);

    //COLORS
    Color background=new Color(255, 255, 255);
    Color gridColor=new Color(150, 150,150);


    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        //objects.add(new Object(0,new Vec3f(50,0,20),new Vec3f(0,0,0)));
        //objects.add(new Object(0,new Vec3f(50,20,0),new Vec3f(0,0,0)));
        thread=new Thread(this);
        thread.start();
    }

    public void addRandParticle(float velmin, float velmax,float rad){
        Vec3f loc=new Vec3f((float)(Math.random()*.8*BOUNDS[0]-(.4*(BOUNDS[0]))),(float)(Math.random()*.8*BOUNDS[1]-(.4*(BOUNDS[1]))),(float)(Math.random()*.8*BOUNDS[2]-(.4*(BOUNDS[2]))));
        Vec3f vel=new Vec3f((float)(velmax*Math.random()+velmin),(float)(velmax*Math.random()+velmin),(float)(velmax*Math.random()+velmin));
        objects.add(new Object(0,loc,vel,rad));
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
        float rld=(HEIGHT/2);

        for (int x=0; x<pw; x++){
            float xor=(float)(Math.atan2(x-cx,lensdist));
            for(int y=0; y<ph; y++){
                float yor=(float)(Math.atan2(y-cy,lensdist));
                gfx.setColor(getColorInDir(xor,yor));
                gfx.fillRect(pSize*x,pSize*y,pSize,pSize);
            }
        }
        gfx.setColor(Color.GREEN);

        for (Object o : objects){
            o.render(gfx,WIDTH,HEIGHT,rld, pos,orient);
        }
        gfx.setColor(Color.BLACK);
        frame.render(gfx,WIDTH,HEIGHT,rld, pos,orient);

        //FINAL
        g.drawImage(img,0,0,this);
    }

    public Color getColorInDir(float xor,float yor){
        Vec2f o1=new Vec2f(orient.x+xor,orient.y+yor);
        for (int i=0; i<objects.size(); i++){
            Object o = objects.get(i);
            Color c=o.doesLineCross(o1,pos);
            if (c!=null){
                return c;
            }
        }
        return Color.WHITE;
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME

            //UPDATES
            for (int i=0; i<objects.size(); i++){
                Object o = objects.get(i);
                o.update(.03f,objects);
                if (!gravon){continue;}
                for (int z=0; z<objects.size(); z++){
                    if(i==z){continue;}
                    Object o1 = objects.get(z);
                    if(objects.indexOf(o)==objects.indexOf(o1)){continue;}
                    o.attractTo(o1.loc,1*o1.getVolume());
                }
            }

            repaint();//UPDATES FRAME
            try{ Thread.sleep(30); } //ADDS TIME BETWEEN FRAMES (FPS)
            catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    public void rotateAround(float xor, float yor){
        float r=200;
        System.out.println(pos+", "+orient);
        orient.x=xor;
        orient.y=yor;
        if (orient.x>6.28){orient.x-=6.28f;}else if (orient.x<-6.28f){orient.x+=6.28f;}
        if (orient.y>6.28){orient.y-=6.28f;}else if (orient.y<-6.28f){orient.y+=6.28f;}
        float r1=r*(float)(Math.cos(-yor));
        pos.z=r*(float)(Math.sin(-yor));
        pos.x=-r1*(float)(Math.cos(-xor));
        pos.y=r1*(float)(Math.sin(-xor));
        System.out.println(pos+", "+orient);


    }
    //INPUT
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_RIGHT){
            rotateAround(orient.x+.2f,orient.y);
            //orient.x+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_LEFT){
            rotateAround(orient.x-.2f,orient.y);

            //orient.x-=.2f;
        }if (e.getKeyCode()==KeyEvent.VK_UP){
            rotateAround(orient.x,orient.y+.2f);

            //orient.y+=.2f;
        }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
            rotateAround(orient.x,orient.y-.2f);

            //orient.y-=.2f;
        }/*
        if (e.getKeyCode()==KeyEvent.VK_A){
            pos.y-=.2f*Math.cos(orient.x);
        }else if (e.getKeyCode()==KeyEvent.VK_D){
            pos.y+=.2f*Math.cos(orient.x);
        }if (e.getKeyCode()==KeyEvent.VK_W){
            pos.x+=.2f*Math.cos(orient.x);
        }else if (e.getKeyCode()==KeyEvent.VK_S){
            pos.x-=.2f*Math.cos(orient.x);
        }*/
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            addRandParticle(5,10,2);
        }if(e.getKeyCode()==KeyEvent.VK_B){
            addRandParticle(5,10,10);
        }if(e.getKeyCode()==KeyEvent.VK_N){
            addRandParticle(0,0,10);
        }
        if(e.getKeyCode()==KeyEvent.VK_L) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x*=.7f;
                o.vel.y*=.7f;
                o.vel.z*=.7f;
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_G){
            gravon=!gravon;
        }if (e.getKeyCode()==KeyEvent.VK_R){
            if (objects.size()>0){
                objects.remove(0);
            }
        }
    }
    public void keyTyped(KeyEvent e) { }

    //QUICK METHOD I MADE TO DISPLAY A COORDINATE GRID

}