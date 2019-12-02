import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

import java.awt.*;
import java.util.ArrayList;

public class Object implements FrameData{
    int shape=0;//sphere
    Vec3f loc;
    Vec3f vel;
    float rad=2;
    Color color;
    ArrayList<Vec3f> points;

    float timer=0;
    public Object(int shape, Vec3f loc, Vec3f vel,float rad){
        points=new ArrayList<>();
        this.color=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        this.vel=vel;
        this.shape=shape;
        this.loc=loc;
        this.rad=rad;
        if (shape==1){createPoints();}
    }

    public void createPoints(){
        if (shape==1){
            points=new ArrayList<>();
            for (int x=-1; x<=1;x+=2){
                for (int y=-1; y<=1; y+=2) {
                    for (int z = -1; z <= 1; z += 2) {
                        points.add(new Vec3f(x * BOUNDS[0], y * BOUNDS[1], z * BOUNDS[2]));
                    }
                }
            }
        }
    }

    public float getVolume(){ return (rad*rad*rad*3.14f*4/3); }

    public void addVolume(float v){
        float nv=getVolume()+v;
        float r3=(nv/(3.14f*4f/3f));
        rad=(float)(Math.pow(r3,.33));
    }

    public void update(float dt, ArrayList<Object> objects){
        Vec3f newv=new Vec3f(loc.x+(vel.x*dt),loc.y+(vel.y*dt),loc.z+(vel.z*dt));
        timer-=dt;
        if(timer<0){
            timer=.1f;
            points.add(new Vec3f(loc.x,loc.y,loc.z));
            if (points.size()>50){
                points.remove(0);
            }
        }
        int cind=objects.indexOf(this);
        for (int i=0; i<objects.size(); i++){
            Object o=objects.get(i);
            if(i!=cind){
            //if(o!=this&&o!=null){
                if (collidesWith(newv,o.loc,rad,o.rad)){
                    /*vel.x=0;
                    vel.y=0;
                    vel.z=0;
                    return;*/
                    System.out.println("collides "+newv+" "+o.loc);
                    vel.x=-vel.x;
                    vel.y=-vel.y;
                    vel.z=-vel.z;
                    return;
                }
                /*if(collidesWith(newv,o.loc,rad,o.rad)){
                    float ov=getVolume();
                    float othvol=o.getVolume();
                    addVolume(othvol);
                    float newvo=getVolume();
                    float mult=(othvol/newvo);
                    float mult1=(ov/newvo);
                    vel.x*=mult1;
                    vel.y*=mult1;
                    vel.z*=mult1;
                    vel.x+=o.vel.x*mult;
                    vel.y+=o.vel.y*mult;
                    vel.z+=o.vel.z*mult;
                    objects.remove(i);
                    i--;
                    //return;
                }*/
            }
        }
        //if(newv.)
        if (Math.abs(newv.x)+rad<BOUNDS[0]){
            loc.x=newv.x;
        }else {
            vel.x=-vel.x;
        }if (Math.abs(newv.y)+rad<BOUNDS[1]){
            loc.y=newv.y;
        }else {
            vel.y=-vel.y;
        }if (Math.abs(newv.z)+rad<BOUNDS[2]){
            loc.z=newv.z;
        }else {
            vel.z=-vel.z;
        }
    }

    public void applyField(Vec2f o, float B){
        Vec3f accelv=new Vec3f();
        Vec3f nv=new Vec3f();// v = vin + vnot
        Vec2f dirOfVel=getDeltaOrient(vel);
        float mag=B*(float)Math.sin(o.y-dirOfVel.y)*vel.length();
        Vec2f forceDir=new Vec2f(dirOfVel.x+(3.14f/2),o.y+3.14f/2);

        float r1=(float)(mag*Math.cos(forceDir.y));
        accelv.z=(float)(mag*Math.sin(forceDir.y));
        accelv.x=(float)(r1*Math.cos(forceDir.x));
        accelv.y=(float)(r1*Math.sin(forceDir.x));
        System.out.println("applying "+accelv);
        //accelv.normalize();
        //System.out.println("applying "+accelv+" now");
        vel.x+=accelv.x;
        vel.y+=accelv.y;
        vel.z+=accelv.z;
        //vel.add(accelv);
    }



    public boolean collidesWith(Vec3f tp, Vec3f p1, float r1, float r2){
        float d=getDistOfDelta(getDeltaVecBetween(p1,tp));
        return d<r1+r2;
    }

    public void magnetize(float c, Object o){
        Vec3f dvec=getDeltaVecBetween(this.loc,o.loc);
        float r=getDistOfDelta(dvec);
        float velmag=getDistOfDelta(vel);
        Vec2f velor=getDeltaOrient(dvec);
        velor.x+=3.14f/2;
        velor.y+=3.14f/2;
        Vec3f BVec=getVecFromMag(velor,velmag);

        //Vec3f a=new Vec3f();
        Vec3f a=BVec;
        float invsq=c/(r*r);
        //System.out.println(a);
        o.vel.x+=a.x*invsq;
        o.vel.y+=a.y*invsq;
        o.vel.z+=a.z*invsq;

        //Vec3f Bor=new Vec3f(vel.x)
    }

    public Vec3f getVecFromMag(Vec2f or, float mag){
        Vec3f newv=new Vec3f();
        float r1=(float)(mag*Math.cos(or.y));
        newv.z=(float)(mag*Math.sin(or.y));
        newv.x=(float)(r1*Math.cos(or.x));
        newv.y=(float)(r1*Math.sin(or.x));
        return newv;
    }

    public void render(Graphics g, int WIDTH, int HEIGHT, float lensd, Vec3f pos, Vec2f or){
        if (shape==0) {
            if (points.size()<2){return;}
            g.setColor(color);
            Vec2f last=null;
            for (int i=0; i<points.size(); i++) {
                Vec3f dv = getDeltaVecBetween(pos, points.get(i));
                Vec2f dor = getDeltaOrient(dv);
                if (getOrientDif(dor, or) > 3.14) {
                    return;
                }
                float x = (float) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                float y = (float) (lensd * (Math.tan(dor.y - or.y))) + (HEIGHT / 2);
                if (last!=null){
                    g.drawLine((int)(last.x),(int)(last.y),(int)(x),(int)(y));
                }
                last=new Vec2f(x,y);
                //System.out.println(x+", "+y+" | "+dor);
                //g.fillOval((int) x-5, (int) y-5, 10, 10);
            }
            Vec3f dv = getDeltaVecBetween(pos, loc);
            Vec2f dor = getDeltaOrient(dv);
            if (getOrientDif(dor, or) > 3.14) {
                return;
            }
            float dist=getDistOfDelta(dv);
            float x = (float) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
            float y = (float) (lensd * (Math.tan(dor.y - or.y))) + (HEIGHT / 2);
            //System.out.println(x+", "+y+" | "+dor);
            float arad=(float)(lensd*Math.tan((rad)/(dist)));
            g.fillOval((int) x-(int)Math.ceil(arad), (int) y-(int)Math.ceil(arad), (int)Math.ceil(2*arad), (int)Math.ceil(2*arad));

        }else if (shape==1){
            boolean f=true;
            for (Vec3f p: points){
                Vec3f dv = getDeltaVecBetween( p,pos);
                Vec2f dor = getDeltaOrient(dv);

                /*if (getOrientDif(dor,or)>3.14159){
                    System.out.println(getOrientDif(dor,or)+">3.14");
                    return;}*/

                float x=(float)(lensd*(Math.tan(dor.x-or.x)))+(WIDTH/2);
                float y=(float)(lensd*(Math.tan(dor.y+or.y)))+(HEIGHT/2);
                if (f){
                    System.out.println(dv+" | from p : "+dor+" | p : "+or);//TODO dor seems to be whats messed up
                    f=false;
                }
                /*float fovx=3.14f*65/180f;
                float fovy=3.14f*65/180f;
                g.setColor(Color.BLUE);
                System.out.println(dor.x+"-"+or.x+"="+(dor.x-or.x));
                float x0=(float)((WIDTH/2f)*((dor.x-or.x)/fovx))+(WIDTH/2);
                float y0=(float)((HEIGHT/2f)*((dor.y-or.y)/fovy))+(HEIGHT/2);
                System.out.println(x0+", "+y0);
                g.fillOval((int)x0-5,(int)y0-5,15,15);*/
                g.setColor(Color.BLACK);
                g.fillOval((int)x-5,(int)y-5,10,10);
                for (Vec3f p2: points){
                    int sim=0;if(p2.x==p.x){sim++;}if(p2.y==p.y){sim++;}if(p2.z==p.z){sim++;}
                    if (sim!=2&&sim!=1){continue;}
                    //if (!(p2.x==p.x || p.y==p2.y || p.z==p2.z)){continue;}
                    Vec3f dv1 = getDeltaVecBetween(pos, p2);
                    Vec2f dor1 = getDeltaOrient(dv1);
                    if (getOrientDif(dor,or)>3.14){continue;}
                    float x1=(float)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                    float y1=(float)(lensd*(Math.tan(dor1.y-or.y)))+(HEIGHT/2);
                    //g.drawLine((int)x,(int)y,(int)x1,(int)y1);

                }
                //System.out.println("rendering at "+x+", "+y);
            }
        }


    }

    public Color doesLineCross(Vec2f orient, Vec3f pos){
        //d=dist from source
        //x^2+y*2+z^2=r
        if (shape==0){
            Vec3f dv=getDeltaVecBetween(pos,loc);
            Vec2f dor=getDeltaOrient(dv);
            //System.out.println("looking in "+orient+", at "+dor);
            float odif=getOrientDif(orient,dor);
            float arclength=(odif*getDistOfDelta(dv));
            if(arclength<rad){
                float cm=1-(arclength/rad);
                float cr=(rad<=4)?rad/4:1;
                Color c=new Color(cr*cm,0,cm);
                return c;
            }
            //float cr=getDistOfDelta(dv);
        }
        return null;
    }
    public float getDistOfDelta(Vec3f dp){
        return (float)(Math.sqrt((dp.x*dp.x)+(dp.y*dp.y)+(dp.z*dp.z)));
    }

    public void attractTo(Vec3f p1,float amt){
        Vec3f dv=getDeltaVecBetween(loc,p1);
        float r= getDistOfDelta(dv);
        float acc=amt/(r*r);
        //Vec3f newv=new Vec3f();
        if(Math.abs(dv.x)>rad){ vel.x=(dv.x>0) ?vel.x+acc : vel.x-acc;}
        if(Math.abs(dv.y)>rad){vel.y=(dv.y>0) ?vel.y+acc : vel.y-acc;}
        if(Math.abs(dv.z)>rad){vel.z=(dv.z>0) ?vel.z+acc : vel.z-acc;}
    }

    public Vec2f getDeltaOrient(Vec3f dp){
        float r1=(float)(Math.sqrt((dp.x*dp.x)+(dp.y*dp.y)));
        float th1=(float)Math.atan2(dp.z,r1);
        float th0=(float)Math.atan2(dp.y,dp.x);
        return new Vec2f(th0,th1);
    }
    public Vec3f getDeltaVecBetween(Vec3f p1, Vec3f p2){
        return new Vec3f(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);
    }
    public float getOrientDif(Vec2f o1, Vec2f o2){
        float d0=getAngDif(o1.x,o2.x);
        float d1=getAngDif(o1.y,o2.y);
        return (float)Math.sqrt((d0*d0)+(d1*d1));
    }
    public float getAngDif(float a1, float a2){
        float ad=a1-a2;
        if(ad<-3.14159){
            ad+=6.28318;
        }else if(ad>3.14159){
            ad-=6.28318;
        }
        return ad;
    }
    public Vec3f getLocOnLine(Vec2f orient, Vec3f pos, float r) {
        float r1=(float)(r*Math.cos(orient.y));
        float x=(float)(r1*Math.cos(orient.x));
        float y=(float)(r1*Math.sin(orient.x));
        float z=(float)(r1*Math.tan(orient.y));
        return new Vec3f(x+pos.x,y+pos.y,z+pos.z);
    }
}
