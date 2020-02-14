import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3f;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener, FrameData {

    //BASIC VARIABLES
    private final int WIDTH=1180, HEIGHT=(int)(WIDTH*1f);
    ArrayList<Object> objects=new ArrayList<>();
    double rad=-500f;
    Vec3d pos=new Vec3d(-rad,0,0);
    Vec2d orient=new Vec2d(0,0);
    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;
    boolean gravon=false;
    Object frame=new Object(1,null,null,0,0,this);
    boolean magon=false;
    //COLORS
    Color background=new Color(255, 255, 255);
    double rld=((WIDTH/2f)/2f);
    String fov="";
    double universalgrav=0;
    double[][][] dmap;
    CellRenderer cr=new CellRenderer();
    double maxE=0;
    boolean renderparticles=true;
    boolean running=true;
    boolean saving=false;
    double totalE=0;
    boolean renderbounds=true;
    double gravpot=0;
    double loste=0;
    double lostp=0;

    boolean bounded=true;
    int pathlength=10;
    double graconstant=.2;
    double electricconstant=20000;
    int cs=5;
    double rad1=10f;
    boolean efield=false;
    boolean nucleusforming=false;
    boolean esim=false;



    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        //objects.add(new Object(0,new Vec3d(50,0,20),new Vec3d(0,0,0)));
        //objects.add(new Object(0,new Vec3d(50,20,0),new Vec3d(0,0,0)));
        double hd=(double)(2*(180f/3.1415f)*Math.atan((HEIGHT/2f)/rld));
        double wd=(double)(2*(180f/3.1415f)*Math.atan((WIDTH/2f)/rld));
        fov="fov : "+hd+", "+wd;
        dmap=new double[BOUNDS[0]*2/cs][BOUNDS[1]*2/cs][BOUNDS[2]*2/cs];
        thread=new Thread(this);
        thread.start();
    }

    public void addRandParticle(double velmin, double velmax,double rad){
        Vec3d loc=new Vec3d((double)(Math.random()*.95*BOUNDS[0]-(.475*(BOUNDS[0]))),(double)(Math.random()*.95*BOUNDS[1]-(.475*(BOUNDS[1]))),(double)(Math.random()*.95*BOUNDS[2]-(.475*(BOUNDS[2]))));
        Vec3d vel=new Vec3d((double)((velmax-velmin)*Math.random()+velmin),(double)((velmax-velmin)*Math.random()+velmin),(double)((velmax-velmin)*Math.random()+velmin));
        for (Object o: objects){
            if (o.collidesWith(o.loc,loc,o.rad,rad)){
                addRandParticle(velmin,velmax,rad);
                return;
            }
        }
        objects.add(new Object(0,loc,vel,rad,(int)(Math.random()*3)-1,this));
    }

    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(background);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        int pSize=3;
        int pw=WIDTH/pSize;
        int ph=HEIGHT/pSize;
        //double fovx=3.14f*2/3;
        //double fovy=3.14f/2;
        int cx=pw/2;
        int cy=ph/2;
        //int lensdist=cy;
        //System.out.println("fov : "+hd+", "+wd);


        /*for (int x=0; x<pw; x++){
            double xor=(double)(Math.atan2(x-cx,lensdist));
            for(int y=0; y<ph; y++){
                double yor=(double)(Math.atan2(y-cy,lensdist));
                gfx.setColor(getColorInDir(xor,yor));
                gfx.fillRect(pSize*x,pSize*y,pSize,pSize);
            }
        }*/
        gfx.setColor(Color.BLUE);
        int sx=50, sy=50;
        gfx.drawString(fov,sx,sy);
        gfx.drawString((int)(orient.x*180/3.14f)+", "+(int)(orient.y*180/3.14f),sx,sy+30);
        gfx.drawString("rad = "+(int)(rad),sx,sy+60);
        gfx.drawString("ugrav = "+(int)(universalgrav),sx,sy+90);
        gfx.drawString("pathlgth = "+(int)(pathlength),sx,sy+120);
        gfx.drawString("KE = "+(int)(totalE)+" E = "+(int)(gravpot),sx,sy+150);
        gfx.drawString("LE = "+(int)(loste)+" LP = "+(int)(lostp),sx,sy+180);

        Vec2d or1=new Vec2d(orient.x,0);//TODO REMOVE THIS LATER
        or1=new Vec2d(orient.x,0);//TODO REMOVE THIS LATER
        //gfx.drawString(frame.getType(),sx,sy+90);
        if (renderparticles) {

            sortObjects();
            for (Object o : objects) {
                o.render(gfx, WIDTH, HEIGHT, rld, pos, or1, orient.y);
            }
        }else {

            for (int x = 0; x < dmap.length; x++) {
                for (int y = 0; y < dmap[0].length; y++) {
                    for (int z = 0; z < dmap[0][0].length; z++) {
                        cr.render(gfx, WIDTH, HEIGHT, rld, pos, or1, cs, x - (BOUNDS[0] / cs), y - (BOUNDS[1] / cs), z - (BOUNDS[2] / cs), dmap[x][y][z], maxE);
                    }
                }
            }
        }
        gfx.setColor(Color.BLACK);
        if (renderbounds) { frame.render(gfx, WIDTH, HEIGHT, rld, pos, or1, orient.y); }
        String msg="particles : "+objects.size();
        gfx.setColor(Color.BLACK);
        gfx.setFont(gfx.getFont().deriveFont(30f));
        gfx.drawString(msg,20,20);
        //FINAL
        g.drawImage(img,0,0,this);
        if(saving)storeImg();
    }


    public void addRandCluster(double velmin, double velmax,double rad, int num, double sr){
        Vec3d loc=new Vec3d((double)(Math.random()*.95*BOUNDS[0]-(.475*(BOUNDS[0]))),(double)(Math.random()*.95*BOUNDS[1]-(.475*(BOUNDS[1]))),(double)(Math.random()*.95*BOUNDS[2]-(.475*(BOUNDS[2]))));
        Vec3d vel=new Vec3d((double)((velmax-velmin)*Math.random()+velmin),(double)((velmax-velmin)*Math.random()+velmin),(double)((velmax-velmin)*Math.random()+velmin));
        for (int i=0; i<num; i++){
            Vec3d p=new Vec3d(loc.x-(rad)+(double)(rad*2*Math.random()),loc.y-(rad)+(double)(rad*2*Math.random()),loc.z-(rad)+(double)(rad*2*Math.random()));
            Vec3d v=new Vec3d(vel.x*(double)(.5+Math.random()),vel.y*(double)(.5+Math.random()),vel.z*(double)(.5+Math.random()));
            Object o=new Object(0,p,v,sr,(int)(Math.random()*3)-1,this);
            objects.add(o);

        }
    }

    public void sortObjects(){
        ArrayList<Object> o1=new ArrayList<>();
        double[] dists=new double[objects.size()];
        int i1=0;
        for (Object o: objects){
            Vec3d vd=new Vec3d(pos.x-o.loc.x,pos.y-o.loc.y,pos.z-o.loc.z);
            dists[i1]=vd.length();
            i1++;
        }
        ArrayList<Integer> s=new ArrayList<>();
        for (int i=0; i<dists.length; i++){
            if(s.size()==0){s.add(i);}else {
                int addin = s.size() - 1;
                for (Integer f : s) {
                    if (dists[f] > dists[i]) {
                        addin=s.indexOf(f);
                        break;
                    }
                }
                s.add(addin,i);
            }
        }
        for (Integer i:s){
            o1.add(0,objects.get(i));
        }
        objects=o1;
    }

    public Color getColorInDir(double xor,double yor){
        Vec2d o1=new Vec2d(orient.x+xor,orient.y+yor);
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
    int paint=0;
    int paintevery=1;//frames
    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME
            Vec2d field=new Vec2d(0,3.1415f/2);
            //Vec3d f=new Vec3d();
            //UPDATES
            int refspeed=20;
            double speedfactor=2;
            double dt=.05f;
            long s=System.nanoTime();
            if (running) {
                dmap = new double[BOUNDS[0] * 2 / cs][BOUNDS[1] * 2 / cs][BOUNDS[2] * 2 / cs];
                maxE = 0;
                totalE=0;
                gravpot=0;
                for (int i = 0; i < objects.size(); i++) {
                    Object o = objects.get(i);
                    o.update(dt, objects, pathlength, this,bounded,0);
                    if (magon) {
                        o.applyField(field, dt);
                    }
                    if (Math.abs(universalgrav) > 1) {
                        o.vel.z += universalgrav * dt;
                    }
                    for (int z = 0; z < objects.size(); z++) {
                        if (i == z || !objects.contains(o)) {
                            continue;
                        }
                        Object o1 = objects.get(z);
                        //if(objects.indexOf(o)==objects.indexOf(o1)){continue;}
                        try {
                            if (gravon) {
                                o.attractTo(o1.loc, graconstant * o1.vol*o.vol*dt);
                            }
                        } catch (NullPointerException e) {
                            System.out.println("ERROR");
                        }
                        try {
                            if (esim&&efield&&o.charge!=0&&o1.charge!=0) {
                                o.attractTo(o1.loc, electricconstant*dt*((o.charge==-o1.charge)?1:-1));
                            }
                        } catch (NullPointerException e) {
                            System.out.println("ERROR");
                        }
                        //if(magon){o.magnetize(magc,o1);}
                    }
                }
            }
            double rdt1=(double)((System.nanoTime()-s)/1000000000f);
            //System.out.println(rdt);
            String times=""+rdt1;
            //paint-=1;
            //if (paint<0){

            //}//UPDATES FRAME
        s=System.nanoTime();
        //paint=paintevery;
        repaint();

        if (saving) {
            double rdt=(double)((System.nanoTime()-s)/1000000000f);
            times+=" "+rdt;
            refspeed=(int)(1000*rdt1);
            System.out.println(times);
            rotateAround(orient.x + .02f, orient.y);
        }

        try{ Thread.sleep(refspeed); } //ADDS TIME BETWEEN FRAMES (FPS)
            catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    public void rotateAround(double xor, double yor){
        boolean up=(orient.y==yor);
        double r=rad;
        //System.out.println(pos+", "+orient);
        orient.x=xor;
        orient.y=yor;
        yor=0;
        if (orient.x>6.28){orient.x-=6.28f;}else if (orient.x<-6.28f){orient.x+=6.28f;}
        if (orient.y>6.28){orient.y-=6.28f;}else if (orient.y<-6.28f){orient.y+=6.28f;}
        if (up) {
            //System.out.println("not u[");
            //orient.y=0;
            double r1 = r * (double) (Math.cos(-yor));
            pos.z = 0;//r * (double) (Math.sin(yor));
            pos.x = -r1 * (double) (Math.cos(-xor));
            pos.y = r1 * (double) (Math.sin(-xor));
        }
        //System.out.println(pos+", "+orient);
        //System.out.println("->");
        //printPosData();
        //printPosData();


    }

    public void printPosData(){
        System.out.println("");
        String s="pos["+(int)pos.x+", "+(int)pos.y+", "+(int)pos.z+"] o["+(int)(orient.x*180/3.1415)+", "+(int)(orient.y*180/3.1415)+"]";
        System.out.println(s);
    }

    //INPUT
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode()==KeyEvent.VK_RIGHT){
            rotateAround(orient.x+.02f,orient.y);
        }else if (e.getKeyCode()==KeyEvent.VK_LEFT){
            rotateAround(orient.x-.02f,orient.y);
        }if (e.getKeyCode()==KeyEvent.VK_UP){
            //orient.y+=.2;
            rotateAround(orient.x,orient.y+.05f);
        }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
            //orient.y-=.2;
            rotateAround(orient.x,orient.y-.05f);
        }else if (e.getKeyCode()==KeyEvent.VK_EQUALS){
            rad*=.9f;
            rotateAround(orient.x,orient.y);

        }else if (e.getKeyCode()==KeyEvent.VK_MINUS){
            rad*=1.1f;
            rotateAround(orient.x,orient.y);
        }else if(e.getKeyCode()==KeyEvent.VK_SPACE){
            addRandParticle(-10,10,rad1);
        }else if(e.getKeyCode()==KeyEvent.VK_C){
            addRandCluster(-10,10,rad1*4,40,rad1);
        }else if(e.getKeyCode()==KeyEvent.VK_T){
            for (int i=0; i<50; i++){ addRandParticle(-10,10,rad1);}
        }else if(e.getKeyCode()==KeyEvent.VK_B){
            addRandParticle(-10,10,10);
        }else if(e.getKeyCode()==KeyEvent.VK_N){
            addRandParticle(0,0,10);
        }else if (e.getKeyCode()==KeyEvent.VK_Y){

            pos=new Vec3d(0,0,rad);
            orient=new Vec2d(0,3.14159f/2f);
        }else if (e.getKeyCode()==KeyEvent.VK_A){

            pos=new Vec3d(0,0,rad);
            orient=new Vec2d(0,3.14159f/2f);
        }else if (e.getKeyCode()==KeyEvent.VK_V){

            pos=new Vec3d(-rad,0,0);
            orient=new Vec2d(0,0);
        }else if (e.getKeyCode()==KeyEvent.VK_OPEN_BRACKET){
            universalgrav+=1;
        }else if (e.getKeyCode()==KeyEvent.VK_CLOSE_BRACKET){
            universalgrav-=1;
            if (universalgrav<0)universalgrav=0;
        }else if (e.getKeyCode()==KeyEvent.VK_COMMA){
            pathlength--;
        }else if (e.getKeyCode()==KeyEvent.VK_PERIOD){
            pathlength++;
        }else if (e.getKeyCode()==KeyEvent.VK_P){
           renderparticles=!renderparticles;
        }else if (e.getKeyCode()==KeyEvent.VK_S){
           running=!running;
        }else if (e.getKeyCode()==KeyEvent.VK_O){
           saving=!saving;
        }else if (e.getKeyCode()==KeyEvent.VK_E){
            renderbounds=!renderbounds;
        }else if (e.getKeyCode()==KeyEvent.VK_J){
            efield=!efield;
        }else if (e.getKeyCode()==KeyEvent.VK_K){
            nucleusforming=!nucleusforming;
        }else if (e.getKeyCode()==KeyEvent.VK_H){
            bounded=false;
            renderbounds=false;
        }
        if(e.getKeyCode()==KeyEvent.VK_L) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x*=.7f;
                o.vel.y*=.7f;
                o.vel.z*=.7f;
            }
        }if(e.getKeyCode()==KeyEvent.VK_0) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x=0;
                o.vel.y=0;
                o.vel.z=0;
            }
        }if(e.getKeyCode()==KeyEvent.VK_F) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x*=1.2f;
                o.vel.y*=1.2f;
                o.vel.z*=1.2f;
            }
        }if (e.getKeyCode()==KeyEvent.VK_R){
            if (objects.size()>0){
                objects.remove(0);
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_G){
            gravon=!gravon;
            System.out.println("grav = "+gravon);
        }if (e.getKeyCode()==KeyEvent.VK_M){
            magon=!magon;
            System.out.println("mag = "+magon);
            //System.out.println("grav = "+gravon);
        }
    }
    public void keyTyped(KeyEvent e) { }
    public void addE(double v,double s, Vec3d pos){
        if (!bounded&&(Math.abs(pos.x-BOUNDS[0])>BOUNDS[0]||Math.abs(pos.y-BOUNDS[1])>BOUNDS[1]||Math.abs(pos.z-BOUNDS[2])>BOUNDS[2])){return;}
        int x=BOUNDS[0]+(int)pos.x;
        int y=(BOUNDS[1]+(int)pos.y);
        int z=(BOUNDS[2]*2)-(BOUNDS[2]+(int)pos.z);
        double e=(v*v*s/2.0);
        double gp=(universalgrav*s*z);

        dmap[x/cs][y/cs][z/cs]+=e;
        if (dmap[x/cs][y/cs][z/cs]>maxE){
            maxE=dmap[x/cs][y/cs][z/cs];
        }
        gravpot+=(gp+e)/100.0;
        totalE+=(e/100.0);

    }

    int num=0;
    public void storeImg(){
        // Initialize Color[][] however you were already doing so.
        /*Color[][] image=new Color[map.length][map[0].length];

        for (int x=0; x<map.length; x++){
            for (int y=0; y<map[0].length; y++){
                if (map[x][y] == 0) {
                    image[x][y]=new Color(100, 00, 00);
                } else {
                    image[x][y]=new Color((int) (map[x][y] * 255) / 2, (int) (map[x][y] * 255), (int) (map[x][y] * 255) / 2);
                }
            }
        }*/

        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = (BufferedImage)(img);
                //new BufferedImage(image.length, image[0].length, BufferedImage.TYPE_INT_RGB);
        //img.getGraphics().getP
        //bufferedImage=new BufferedImage(img);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        /*for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                bufferedImage.setRGB(x, y, image[x][y].getRGB());
            }
        }*/
        String filename = "C:\\Users\\Mike\\Documents\\GitHub\\3D-Sim\\imgs\\"+num+".jpg";
        //String filename = "B:\\Libraries\\Programming\\3D-Sim\\imgs\\"+num+".jpg";

        File outputfile = new File(filename);
        try {
            ImageIO.write(bufferedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        num++;
    }

    public void remove(Object o){
        if (objects.contains(o)) objects.remove(o);
    }

    //QUICK METHOD I MADE TO DISPLAY A COORDINATE GRID

}