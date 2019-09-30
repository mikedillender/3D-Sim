import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

import java.awt.*;
import java.util.ArrayList;

public class Object implements FrameData{
    int shape=0;//sphere
    Vec3f loc;
    Vec3f vel;
    float rad=2;

    public Object(int shape, Vec3f loc, Vec3f vel){
        this.vel=vel;
        this.shape=shape;
        this.loc=loc;
    }

    public float getVolume(){
        return (rad*rad*rad*3.14f*4/3);
    }
    public void addVolume(float v){
        float nv=getVolume()+v;
        float r3=(nv/(3.14f*4f/3f));
        rad=(float)(Math.pow(r3,.33));
    }

    public void update(float dt, ArrayList<Object> objects){
        Vec3f newv=new Vec3f(loc.x+(vel.x*dt),loc.y+(vel.y*dt),loc.z+(vel.z*dt));
        for (int i=0; i<objects.size(); i++){
            Object o=objects.get(i);
            if(o!=this){
                if(collidesWith(o.loc,rad,o.rad)){
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
                }
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
    public boolean collidesWith(Vec3f p1, float r1, float r2){
        float d=getDistOfDelta(getDeltaVecBetween(p1,loc));
        return d<r1+r2;
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
        if(ad<-3.15){
            ad+=6.28;
        }else if(ad>3.15){
            ad-=6.28;
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
