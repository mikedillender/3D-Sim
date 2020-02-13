import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec3d;

import java.awt.*;
import java.util.ArrayList;

public class CellRenderer {

    public CellRenderer(){

    }
    public void render(Graphics g, int WIDTH, int HEIGHT, double lensd, Vec3d pos, Vec2d or, double cs, int x1, int y1, int z1,double e,double maxE){
        if (e==0){return;}
        ArrayList<Vec3d> points=new ArrayList<>();
        for (int x=-1; x<=1;x+=2){
            for (int y=-1; y<=1; y+=2) {
                for (int z = -1; z <= 1; z += 2) {
                    points.add(new Vec3d(((x1+.5f)*cs)+x * cs/2f, ((y1+.5f)*cs)+y * cs/2f, ((z1+.5f)*cs)+z * cs/2f));
                }
            }
        }
        //g.setColor(Color.BLACK);
        g.setColor(new Color(0,0,0,(float)(e/maxE)));
        boolean f=true;
        for (Vec3d p: points){
            Vec3d dv = getDeltaVecBetween( p,pos);
            Vec2d dor = getDeltaOrient(dv);
            float x=(float)(lensd*(Math.tan(dor.x-or.x)))+(WIDTH/2);
            float y=(float)(lensd*(Math.tan(dor.y+or.y)))+(HEIGHT/2);
            if (f){
                //System.out.println(dv+" | from p : "+dor+" | p : "+or);//TODO dor seems to be whats messed up
                f=false;
            }
            //g.setColor(Color.BLACK);
            //g.fillOval((int)x-5,(int)y-5,10,10);
            for (Vec3d p2: points){
                int sim=0;if(p2.x==p.x){sim++;}if(p2.y==p.y){sim++;}if(p2.z==p.z){sim++;}
                if (sim!=2){continue;}
                //if (!(p2.x==p.x || p.y==p2.y || p.z==p2.z)){continue;}
                Vec3d dv1 = getDeltaVecBetween(p2,pos);
                Vec2d dor1 = getDeltaOrient(dv1);
                if (getOrientDif(dor,or)>3.14){continue;}
                float x2=(float)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                float y2=(float)(lensd*(Math.tan(dor1.y+or.y)))+(HEIGHT/2);
                g.drawLine((int)x,(int)y,(int)x2,(int)y2);

            }
            //System.out.println("rendering at "+x+", "+y);
        }



    }
    public Vec3d getDeltaVecBetween(Vec3d p1, Vec3d p2){
        return new Vec3d(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);
    }
    public float getOrientDif(Vec2d o1, Vec2d o2){
        float d0=getAngDif((float)o1.x,(float)o2.x);
        float d1=getAngDif((float)o1.y,(float)o2.y);
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

    public Vec2d getDeltaOrient(Vec3d dp){
        float r1=(float)(Math.sqrt((dp.x*dp.x)+(dp.y*dp.y)));
        float th1=(float)Math.atan2(dp.z,r1);
        float th0=(float)Math.atan2(dp.y,dp.x);
        return new Vec2d(th0,th1);
    }
}
