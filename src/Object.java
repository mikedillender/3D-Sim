import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3d;

import java.awt.*;
import java.util.ArrayList;

public class Object implements FrameData{
    int shape=0;//sphere
    Vec3d loc;
    Vec3d vel;
    double rad=2;
    Color color;
    ArrayList<Vec3d> points;
    Vec3d[][] pointmap;
    double timer=0;
    boolean side=false;
    double avg=0;
    boolean polar=false;
    boolean land=false;
    boolean parametric=false;
    double[][] vrngs=new double[3][3];
    int wd=100;
    int ht=6;
    boolean slopeField=false;
    double vol;
    int charge=0;

    public Object(int shape, Vec3d loc, Vec3d vel,double rad,int charge, Main m){
        points=new ArrayList<>();
        if (charge==0){charge=(Math.random()<.5)?-1:1;}
        if (!m.esim) {
            charge = 0;
            this.color=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        }else {
            this.color=new Color((charge==-1?(int)(Math.random()*155+100):(int)(Math.random()*55)),(charge==0?(int)(Math.random()*155+100):(int)(Math.random()*55)),(charge==1?(int)(Math.random()*155+100):(int)(Math.random()*55)));
        }
        this.vel=vel;
        this.shape=shape;
        this.loc=loc;
        this.rad=rad;
        this.charge=charge;
        vol=rad*rad*rad;
        if (charge==-1){vol*=(.0005455);this.rad*=.5f;}
        if (shape==1||shape==2){createPoints();}
    }

    public void createPoints(){
        int numAdded=0;
        double sum=0;
        double minr=0;
        double maxr=0;
        if (shape==1){
            points=new ArrayList<>();
            for (int x=-1; x<=1;x+=2){
                for (int y=-1; y<=1; y+=2) {
                    for (int z = -1; z <= 1; z += 2) {
                        points.add(new Vec3d(x * BOUNDS[0], y * BOUNDS[1], z * BOUNDS[2]));
                    }
                }
            }
        }else if (shape==2){
            double sep=30;
            //int size=42;
            //wd=30;
            //ht=30;
            wd=10;
            ht=10;
            //land=true;
            int maxv=7;
            int v=(int)(Math.floor(Math.random()*(maxv+.99)));
            v=8;

            double vr=25;
            int vs=(int)(Math.random()*5)+5;
            double[][] vecs=new double[vs][];

            for (int i=0; i<vs; i++) {
                vecs[i]=new double[]{(double)(Math.random()*vr-(vr/2)),(double)(Math.random()*vr-(vr/2)),(double)(Math.random()*vr-(vr/2)),(double)(Math.random()*vr-(vr/2))};
            }
            for (double[] vc:vecs) {
                if (Math.abs(vc[1])<1){ vc[1]=(vc[1]<0)?-1:1; }
                if (Math.abs(vc[3])<1){ vc[3]=(vc[3]<0)?-1:1; }
            }
            slopeField=v==10;
            parametric=(v==10||v==11);
            polar=(v==7||v==8||v==9);
            double step=2;
            if (!parametric){ht=wd;}

            int xmin=(!parametric)?-wd:0;
            int ymin=(!parametric)?-ht:1;
            int xmax=(!parametric)?wd:wd*2-1;
            int ymax=(!parametric)?ht:ht*2-1;
            if (parametric&&!slopeField){ymin=0;}

            pointmap=new Vec3d[wd*2+1][ht*2+1];
            double linesep=.8f;
            if (parametric&&slopeField){
                for (int x=0; x<wd*2+1; x++){
                    double[] v1=new double[]{0,0,linesep*x};
                    pointmap[x][0]=new Vec3d(v1[0],v1[1],v1[2]);
                    for (int i=0; i<3; i++){
                        if (v1[i]>vrngs[i][1]){ vrngs[i][1]=(double)Math.ceil(v1[i]); }
                        if (v1[i]<vrngs[i][0]){ vrngs[i][0]=(double)Math.floor(v1[i]); }
                    }
                }
            }

            for (int x=xmin; x<=xmax;x+=1){
                for (int y=ymin; y<=ymax; y+=1) {
                    double[] v1=new double[]{x*sep,y*sep,0};
                    switch (v){
                        case 0:
                            v1[2]=ht*((wd-Math.abs(x))*(ht-Math.abs(y))/(double)(ht*ht));
                            break;
                        case 1:
                            v1[2]=(double)(Math.sin(x/4f)*wd)/(Math.abs(y)+.5f);
                            break;
                        case 2:
                            for (double[] vc:vecs){
                                v1[2]=v1[2]+(double)(Math.sin(((x+vc[0])/vc[1])+((y+vc[2])/vc[3])));
                            }
                            v1[2]=v1[2]/vecs.length;
                            //z=sep*sep/8f*(double)(Math.sin(x/3f)+Math.sin(2.1+x/12f+y/4f)-Math.cos(1.21+(x+y)/8f));
                            v1[2]*=sep*sep/4f;
                            land=true;
                            break;
                        case 3:
                            double c=(wd*wd)-(x*x)-(y*y);//DOme
                            v1[2]=(c>0)?(double)(Math.sqrt(c)*sep):0;
                            break;
                        case 4:
                            double c1=(double)Math.sqrt((x*x)+(y*y));
                            double c2=25-(double)Math.pow(Math.abs((14-c1)),2);
                            if (c2<-20){continue;}
                            v1[2]=(c2>0)?(sep*(double)Math.sqrt(c2)):0;
                            break;
                        case 5:
                            v1[2]=(double)(sep*sep*Math.sin(x+y));
                            break;
                        case 6:
                            double c3=(double)Math.sqrt((x*x)+(y*y));
                            v1[2]=(double)(sep*sep/4f*Math.cos(c3/2f));
                            break;
                        case 7://polar attempt
                            //double r=(double)(200*Math.sin((y)/(size*2f)));
                            double xor=((3.14159f)*((x+wd)/(double)wd));
                            double yor=((3.14159f/2f)*((y)/(double)ht));
                            double r=200*(double)(Math.sin(xor*2));

                            //if (double.isNaN(r)||double.isInfinite(r)){r=200;}}//TODO CHANGE
                            double r1 = r * (double) (Math.cos(-yor));
                            v1[2] = r * (double) (Math.sin(yor));
                            v1[0] = -r1 * (double) (Math.cos(-xor));
                            v1[1] = r1 * (double) (Math.sin(-xor));
                            polar=true;
                            break;
                        case 8:
                            xor=((3.14159f)*((x+wd)/(double)wd));
                            yor=((3.14159f/2f)*((y)/(double)ht));
                            //if (Math.abs(xor)%3.1415>.2){continue;}
                            double s=100;
                            //double
                            //r=(double)Math.sqrt((Math.pow((100/Math.cos(Math.abs((xor%(3.14159f/2))-(3.14159f/4)))),2)+Math.pow(100/Math.cos(Math.abs(((yor+(3.14159f/4))%(3.14159f/2))-(3.14159f/4))),2)));
                            //r=(double)(((100/((Math.cos(Math.abs((xor%(3.14159f/2))-(3.14159f/4))))*Math.cos(Math.abs(((yor+(3.14159f/4))%(3.14159f/2))-(3.14159f/4)))))));
                            r=100;
                            double cornerx=(double)Math.abs(((yor+(Math.PI/4))%(Math.PI/2))-(Math.PI/4));
                            double cornery=(double)Math.abs(((xor+(Math.PI/4))%(Math.PI/2))-(Math.PI/4));
                            double r3=(double)(s/Math.cos(xor));

                            r=(double)((100/(Math.cos(cornery)))/Math.cos(cornerx));
                            if (r>190){
                                System.out.println(r+" at "+(int)(cornerx*180/3.14)+", "+(int)(cornery*180/3.14));
                            }
                            //r=(double)(100/Math.cos(Math.abs((yor%(3.14159f/2)))));
                            //if (yor>Math.PI/4){
                                //r=(double)(100/Math.cos(Math.abs((xor%(3.14159f/2)))));
                            r=100;
                            //}
                            //if (r>Math.sqrt(30000)){r=(double)(Math.sqrt(30000));}
                            //if (Math.abs((yor%(3.14159f)))>(Math.PI/4f)){r=0;}
                            //r=(double)(((100/(Math.cos(xor)*Math.sin(yor)))));
                            //r=(double)((100f/Math.cos(Math.abs(((xor%(Math.PI/2))-(Math.PI/4))))));
                            //r=200*xor;
                            //if (xor>Math.PI/4||yor>Math.PI/4){ r=10;}
                            //if (double.isNaN(r)||double.isInfinite(r)){continue;}//TODO CHANGE
                            if (r<minr){minr=r;}
                            if (r>maxr){maxr=r;}
                            r1 = r * (double) (Math.cos(yor));
                            v1[2] = r * (double) (Math.sin(yor));
                            v1[0] = r1 * (double) (Math.cos(xor));
                            v1[1] = r1 * (double) (Math.sin(xor));
                            polar=true;
                            break;
                        case 9://polar attempt
                            //double r=(double)(200*Math.sin((y)/(size*2f)));
                            xor=((3.14159f)*((x+wd)/(double)wd));
                            yor=((3.14159f/2f)*((y)/(double)ht));
                            //System.out.println(xor+", "+yor);
                            r=200*(double)(Math.sin(xor*2)*Math.cos(yor*2));

                            //if (r<30){r=30;}
                            //r=200;
                            //r=200*(double)(xor*yor/36);
                            //r=200*(double)Math.sqrt(xor/(yor+1));
                            //r=200*(double)((Math.abs(yor-Math.PI)));
                            //r=(double)(200*(xor));
                            //if (double.isNaN(r)||Float.isInfinite((float)r)){r=200;}}//TODO CHANGE
                            r1 = r * (double) (Math.cos(yor));
                            v1[2] = r * (double) (Math.sin(yor));
                            v1[0] = r1 * (double) (Math.cos(xor));
                            v1[1] = r1 * (double) (Math.sin(xor));
                            polar=true;
                            break;
                        case 10://Parametric
                            Vec3d lp=pointmap[x][y-1];
                            double[] ddt=new double[]{
                                    (double)(Math.sin(lp.y/19f)+Math.cos(lp.z/13f)),
                                    (double)(Math.cos(lp.x/12f+14)*(Math.sqrt(Math.abs(lp.x*Math.sin(lp.z/19f+5))))),
                                    (double)(((x/30)*Math.sin(lp.y*(lp.x-lp.y)/(sep*14)+1.3))+1)
                            };
                            double dfromc=lp.length();
                            /*double[] ddt=new double[]{
                                    (double)(5*Math.cos(dfromc)),
                                    (double)1,
                                    (double)(Math.sin(dfromc/25f))
                            };*/
                            v1[0]=lp.x+(double)(step*ddt[0]);
                            v1[1]=lp.y+(double)(step*ddt[1]);
                            v1[2]=lp.z+(double)(step*ddt[2]);
                            break;
                        case 11://Parametric
                            if (y==0){lp=new Vec3d(0,0,0);}else {lp=pointmap[x][y-1];}


                            /*v1[0]=(y*linesep)+200*(double)(Math.cos(x/6f+13));
                            v1[1]=200*(double)(Math.cos(x/6f+2));
                            v1[2]=200*(double)(Math.sin(x/4f+43));*/
                            v1[0]=(double)((y*linesep*Math.sin(Math.atan2(lp.y,lp.x))))+(double)(((250+(50*Math.cos(x*.12))))*(Math.cos(x/12f)));
                            v1[1]=(double)((y*linesep*Math.cos(Math.atan2(lp.y,lp.x))))+(double)(((250+(50*Math.sin(x*.32))))*(double)(Math.sin(x/12f)));
                            v1[2]=100*(double)(Math.sin(x*.64+1.34));
                            break;
                    }

                    for (int i=0; i<3; i++){
                        if (v1[i]>vrngs[i][1]){ vrngs[i][1]=(double)Math.ceil(v1[i]); }
                        if (v1[i]<vrngs[i][0]){ vrngs[i][0]=(double)Math.floor(v1[i]); }
                    }
                    sum+=v1[2];
                    numAdded++;
                    if (!parametric) {
                        pointmap[wd + x][ht + y] = (new Vec3d(v1[0], (side) ? v1[2] : (v1[1]), (side) ? v1[1] : v1[2]));
                    }else {
                        pointmap[x][y] = (new Vec3d(v1[0], (side) ? v1[2] : (v1[1]), (side) ? v1[1] : v1[2]));

                    }

                }
            }
        }
        System.out.println("radius:"+minr+"-"+maxr);
        System.out.println("parametric : "+parametric);

        avg=sum/numAdded;
        for (int i=0; i<3; i++) {
            vrngs[i][2]=vrngs[i][1]-vrngs[i][0];
            System.out.println(i+" | "+vrngs[i][0]+" - "+vrngs[i][1]+" ("+vrngs[i][2]+")");
        }
    }

    public void combine(Object p, Object e, Main m){
        e.vel.mul(e.vol/(e.vol+p.vol));
        p.vel.add(e.vel);
        p.charge=0;
        p.color=new Color((p.charge==-1?(int)(Math.random()*155+100):(int)(Math.random()*55)),(p.charge==0?(int)(Math.random()*155+100):(int)(Math.random()*55)),(p.charge==1?(int)(Math.random()*155+100):(int)(Math.random()*55)));
        p.vol+=e.vol;
        m.remove(e);

    }
    //public double getVolume(){ return (rad*rad*rad*3.14f*4/3); }

    /*public void addVolume(double v){
        double nv=getVolume()+v;
        double r3=(nv/(3.14f*4f/3f));
        rad=(double)(Math.pow(r3,.33));
    }*/

    public void update(double dt, ArrayList<Object> objects,int pathlength, Main m, boolean bounded,int iter){
        if (iter>0){ /*System.out.println("iter"+iter);*/}
        if (iter>5){return;}
        Vec3d newv=new Vec3d(loc.x+(vel.x*dt),loc.y+(vel.y*dt),loc.z+(vel.z*dt));
        timer-=dt;
        if(timer<0){
            timer=.2f;
            points.add(new Vec3d(loc.x,loc.y,loc.z));
            if (points.size()>pathlength){
                points.remove(0);
            }
        }

        double v=vel.length();
        double vl=vol;
        m.addE(v,vl,loc);
        int cind=objects.indexOf(this);
        for (int i=0; i<objects.size(); i++){
            Object o=objects.get(i);
            if(i!=cind){
            //if(o!=this&&o!=null){
                if (collidesWith(newv,o.loc,rad,o.rad)&&!collidesWith(loc,o.loc,rad,o.rad)){

                    if (o.charge==-charge&&o.charge!=0&&charge!=0&&m.nucleusforming){
                        combine((o.charge==-1)?this:o,(o.charge==-1)?o:this,m);
                    }else {
                        //Momentum is always conserved. energy is always conserved
                        //Energy is always conserved
                        Vec3d p = new Vec3d(vol * vel.x + o.vol * o.vel.x, vol * vel.y + o.vol * o.vel.y, vol * vel.z + o.vol * o.vel.z);
                        double e1 = getE();
                        double e2 = o.getE();
                        double e = (e1 + e2) / 2f;
                        double te = (e1 + e2);
                        Vec3d o2t = getDeltaVecBetween(o.loc, loc);
                        o2t.normalize();
                        double theta = 2*((o2t.x * ((o.vel.x / o.vol) - (vel.x / vol))) + (o2t.y * ((o.vel.y / o.vol) - vel.y / vol)) + (o2t.z * (o.vel.z / o.vol - vel.z / vol))) * (o.vol * vol * vol * o.vol) / (o.vol * o.vol + vol * vol);//TODO FIND HOW TO CONSIDER MASS
                        double theta1=(o2t.x*(o.vel.x-vel.x))+(o2t.y*(o.vel.y-vel.y))+(o2t.z*(o.vel.z-vel.z));
                        //System.out.println(theta+" vs "+theta1);
                        vel = applyF(vel, theta / vol, o2t);
                        o.vel = applyF(o.vel, -theta / o.vol, o2t);
                        double nte = (o.getE() + getE());
                        double dte = nte - te;
                        Vec3d p2 = new Vec3d(vol * vel.x + o.vol * o.vel.x, vol * vel.y + o.vol * o.vel.y, vol * vel.z + o.vol * o.vel.z);
                        double dtp=Math.abs(p2.x-p.x)+Math.abs(p2.y-p.y)+Math.abs(p2.z-p.z);
                        m.loste+=(Math.abs(dte));
                        m.lostp+=(Math.abs(dtp));
                        //System.out.println("p : "+p.toString()+" > "+p2.toString()+" , energy change : "+dte+" ("+(Math.round((dte/te)*100000)/1000)+"%)");
                        if (iter<10) {
                        //    o.update(dt, objects, pathlength, m, bounded, iter + 1);
                        }
                        return;
                    }
                }
                /*if(collidesWith(newv,o.loc,rad,o.rad)){
                    double ov=getVolume();
                    double othvol=o.getVolume();
                    addVolume(othvol);
                    double newvo=getVolume();
                    double mult=(othvol/newvo);
                    double mult1=(ov/newvo);
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
        if (bounded) {
            if (Math.abs(newv.x) + rad < BOUNDS[0]) {
                loc.x = newv.x;
            } else {
                vel.x = -vel.x;
                update(dt, objects, pathlength, m, bounded, iter+1);
            }
            if (Math.abs(newv.y) + rad < BOUNDS[1]) {
                loc.y = newv.y;
            } else {
                vel.y = -vel.y;
                update(dt, objects, pathlength, m, bounded, iter+1);
            }
            if (Math.abs(newv.z) + rad < BOUNDS[2]) {
                loc.z = newv.z;
            } else {
                vel.z = -vel.z;
                update(dt, objects, pathlength, m, bounded, iter+1);
            }
        }else {
            loc=newv;
        }
    }

    public Vec3d applyF(Vec3d v1, double str, Vec3d d){
        //d.normalize();
        v1.x+=(str*d.x);
        v1.y+=(str*d.y);
        v1.z+=(str*d.z);
        return v1;
    }

    public void applyField(Vec2d o, double B){
        Vec3d accelv=new Vec3d();
        Vec3d nv=new Vec3d();// v = vin + vnot
        Vec2d dirOfVel=getDeltaOrient(vel);
        double mag=B*(double)Math.sin(o.y-dirOfVel.y)*vel.length();
        Vec2d forceDir=new Vec2d(dirOfVel.x+(3.1415f/2),o.y+3.1415f/2);

        double r1=(double)(mag*Math.cos(forceDir.y));
        accelv.z=(double)(mag*Math.sin(forceDir.y));
        accelv.x=(double)(r1*Math.cos(forceDir.x));
        accelv.y=(double)(r1*Math.sin(forceDir.x));
        //System.out.println("applying "+accelv);
        //accelv.normalize();
        //System.out.println("applying "+accelv+" now");
        vel.x+=accelv.x;
        vel.y+=accelv.y;
        vel.z+=accelv.z;
        //vel.add(accelv);
    }


    public String getType(){
        String s="";
        if (polar){
            s+="Polar Function";
        }else if (parametric){
            s+="Parametric Function";
            if (slopeField){s+=" (Slope Field Approx.)";}
        }else {
            s+="z = f(x,y) Function";
        }
        return s;
    }

    public double getE(){
        return (double)(vol*Math.pow(vel.length(),2));
    }

    public boolean collidesWith(Vec3d tp, Vec3d p1, double r1, double r2){
        double d=getDistOfDelta(getDeltaVecBetween(p1,tp));
        return d<r1+r2;
    }

    public void magnetize(double c, Object o){
        Vec3d dvec=getDeltaVecBetween(this.loc,o.loc);
        double r=getDistOfDelta(dvec);
        double velmag=getDistOfDelta(vel);
        Vec2d velor=getDeltaOrient(dvec);
        velor.x+=3.14f/2;
        velor.y+=3.14f/2;
        Vec3d BVec=getVecFromMag(velor,velmag);

        //Vec3d a=new Vec3d();
        Vec3d a=BVec;
        double invsq=c/(r*r);
        //System.out.println(a);
        o.vel.x+=a.x*invsq;
        o.vel.y+=a.y*invsq;
        o.vel.z+=a.z*invsq;

        //Vec3d Bor=new Vec3d(vel.x)
    }

    public Vec3d getVecFromMag(Vec2d or, double mag){
        Vec3d newv=new Vec3d();
        double r1=(double)(mag*Math.cos(or.y));
        newv.z=(double)(mag*Math.sin(or.y));
        newv.x=(double)(r1*Math.cos(or.x));
        newv.y=(double)(r1*Math.sin(or.x));
        return newv;
    }

    public int[] getColorAt(int x, int y, Vec3d p){
        double zmult=(((!side)?((p.z-vrngs[2][0])/vrngs[2][2]):((p.y-vrngs[1][0])/vrngs[1][2])));
        int mw=pointmap.length;
        int mh=pointmap[0].length;
        if (land){
            boolean abvavg=(((!side)?p.z:p.y)>avg);
            //if (!abvavg){rn++;x++;if (aim!=0){ if(x%aim==0){ y+=(im<0)?-1:1; }}continue;}
            return new int[]{(int)(100*zmult),(int)(zmult*255),(int)((abvavg)?80:(int)((zmult*500>255)?255:(zmult*500)))};
        }else {
            //return new int[]{(int) (255 * x * zmult / (double) msize), (int) (zmult * (255 - (255 * x / (double) msize))), (int) (zmult * 255 * y / (double) msize)};
            if (!parametric) {
                /*int r = (int) (zmult * ((x > (mw / 2)) ? 240 : 0));
                int g = (int) (zmult * ((y > (mh / 2)) ? 240 : 0));
                int b = (int) (zmult * 150);*/
                int r = 50+(int)(150*((double)x/mw));
                //r=(int)(r*zmult);
                int g = (int) (50+(int)(150*((double)y/mh)));
                //g=(int)(g*zmult);
                int b = (int) (zmult * 150);

                return new int[]{r,g,b};
            }else {
                int r = 50+(int)(150*((double)x/mw));
                int g = (int) (50+(int)(150*((double)y/mh)));
                int b = (int) (zmult * 150);
                return new int[]{r,g,b};
            }

            //return new int[]{(int)(255*x/(double)msize),(int)(255*y/(double)msize),100};
        }
    }

    public void render(Graphics g, int WIDTH, int HEIGHT, double lensd, Vec3d pos, Vec2d or,double roty){
        if (shape==0) {
            if (points.size()<2){return;}
            g.setColor(color);
            Vec2d last=null;
            for (int i=0; i<points.size(); i++) {
                Vec3d dv = getDeltaVecBetween(pos, points.get(i));
                Vec2d dor = getDeltaOrient(dv);
                if (getOrientDif(dor, or) > 3.14159) {
                    //continue;
                }
                double x = (double) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                double y = (double) (lensd * (Math.tan(dor.y - or.y))) + (HEIGHT / 2);
                if (last!=null){
                    g.drawLine((int)(last.x),(int)(last.y),(int)(x),(int)(y));
                }
                last=new Vec2d(x,y);
                //System.out.println(x+", "+y+" | "+dor);
                //g.fillOval((int) x-5, (int) y-5, 10, 10);
            }
            Vec3d dv = getDeltaVecBetween(pos, loc);
            Vec2d dor = getDeltaOrient(dv);
            if (getOrientDif(dor, or) > 3.14159) {
                //System.out.println("");
                //return;
            }
            double dist=getDistOfDelta(dv);
            double x = (double) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
            double y = (double) (lensd * (Math.tan(dor.y - or.y))) + (HEIGHT / 2);
            //System.out.println(x+", "+y+" | "+dor);
            double arad=(double)(lensd*Math.tan((rad)/(dist)));
            g.fillOval((int) x-(int)Math.ceil(arad), (int) y-(int)Math.ceil(arad), (int)Math.ceil(2*arad), (int)Math.ceil(2*arad));

        }else if (shape==1){
            boolean f=true;
            for (Vec3d p: points){
                Vec3d dv = getDeltaVecBetween( p,pos);
                Vec2d dor = getDeltaOrient(dv);

                /*if (getOrientDif(dor,or)>3.14159){
                    System.out.println(getOrientDif(dor,or)+">3.14");
                    return;}*/

                double x=(double)(lensd*(Math.tan(dor.x-or.x)))+(WIDTH/2);
                double y=(double)(lensd*(Math.tan(dor.y+or.y)))+(HEIGHT/2);
                if (f){
                    //System.out.println(dv+" | from p : "+dor+" | p : "+or);//TODO dor seems to be whats messed up
                    f=false;
                }
                g.setColor(Color.BLACK);
                g.fillOval((int)x-5,(int)y-5,10,10);
                for (Vec3d p2: points){
                    int sim=0;if(p2.x==p.x){sim++;}if(p2.y==p.y){sim++;}if(p2.z==p.z){sim++;}
                    if (sim!=2){continue;}
                    //if (p2.x==p.x&&p.y==p2.y){continue;}
                    //if (!(p2.x==p.x || p.y==p2.y || p.z==p2.z)){continue;}
                    Vec3d dv1 = getDeltaVecBetween(p2,pos);
                    Vec2d dor1 = getDeltaOrient(dv1);
                    //if (Math.abs(getOrientDif(dor,or))>3.14f/2){continue;}
                    double x1=(double)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                    double y1=(double)(lensd*(Math.tan(dor1.y+or.y)))+(HEIGHT/2);
                    g.drawLine((int)x,(int)y,(int)x1,(int)y1);

                }
                //System.out.println("rendering at "+x+", "+y);
            }
        }else if (shape==2){
            boolean f=true;
            int mw=pointmap.length;
            int mh=pointmap[0].length;
            //System.out.println("in quadrant "+q+" | p : "+pos);
            //System.out.println("in quad "+q+", angle small? =  "+small+" |  m = "+aim);
            boolean net=false;
            if (polar){net=true;}
            boolean panels=false;if (polar||parametric){panels=true;};
            if (parametric){panels=false;net=true;}
            //boolean lines
            //panels=true;
            //panels=true;
            panels=false;
            net=f;
            net=true;
            ArrayList<int[][]> pls=new ArrayList<>();
            int rn=0;
            //land=false;

            if (polar){land=false;}
            if (!polar&&!parametric) {
                int msize=mw;
                double m=-(Math.abs(pos.x)/Math.abs(pos.y));
                boolean small=Math.abs(m)<1;
                //boolean small=true;
                if (small){m=1f/m;}
                if (Math.abs(m)>msize){small=!small;m=0;}
                int im=Math.round((float)m);
                int aim=Math.abs(im);
                int q=(pos.x<0)?((pos.y>0)?2:3):((pos.y>0)?1:4);
                for (int c = 0; c < msize; c++) {
                    int x = 0;
                    int y = c;
                    while (x >= 0 && x < msize && y >= 0 && y < msize) {
                        //Vec3d p=(!small)?pointmap[x][y]:pointmap[y][x];
                        int x2 = (small) ? x : y;
                        int y2 = (small) ? y : x;
                        if (q == 2) {
                            x2 = msize - 1 - x2;
                        } else if (q == 3) {
                            x2 = msize - 1 - x2;
                            y2 = msize - 1 - y2;
                        } else if (q == 4) {
                            y2 = msize - 1 - y2;
                        }
                        Vec3d p = pointmap[x2][y2];
                        if (p == null) {
                            rn++;
                            x++;
                            if (aim != 0) {
                                if (x % aim == 0) {
                                    y += (im < 0) ? -1 : 1;
                                }
                            }
                            continue;
                        }
                        //Vec3d dv = getDeltaVecBetween(p, pos);
                        Vec3d dv = getDeltaVecBetween(rotate(p, roty), pos);
                        int dist = (int) (dv.length() * 10);
                        Vec2d dor = getDeltaOrient(dv);
                        if (getOrientDif(dor, or) > 3.14159) {
                            rn++;
                            x++;
                            if (aim != 0) {
                                if (x % aim == 0) {
                                    y += (im < 0) ? -1 : 1;
                                }
                            }
                            continue;
                        }

                        double x1 = (double) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                        double y1 = (double) (lensd * (Math.tan(dor.y + or.y))) + (HEIGHT / 2);

                        int[] col = getColorAt(x2, y2, p);
                        if (!panels) {
                            g.setColor(new Color(col[0], col[1], col[2]));
                        }
                        int[] t1x = new int[]{(int) x1, 0, 0};
                        int[] t1y = new int[]{(int) y1, 0, 0};
                        int[] t2x = new int[]{(int) x1, 0, 0};
                        int[] t2y = new int[]{(int) y1, 0, 0};
                        boolean[] cancel = new boolean[2];
                        for (int d = 0; d < 4; d++) {
                            int x4 = x2 + ((d % 2 == 0) ? 0 : ((d == 1) ? 1 : -1));
                            int y4 = y2 + ((d % 2 == 1) ? 0 : ((d == 0) ? 1 : -1));
                            if (net && parametric) {
                                if (d % 2 == 1) {
                                    continue;
                                }
                            }

                            if (x4 >= 0 && y4 >= 0 && y4 < pointmap[0].length && x4 < pointmap.length) {
                                Vec3d p2 = pointmap[x4][y4];
                                if (p2 == null) {
                                    cancel[((d < 2) ? 0 : 1)] = true;
                                    continue;
                                }
                                Vec3d dv1 = getDeltaVecBetween(rotate(p2, roty), pos);
                                Vec2d dor1 = getDeltaOrient(dv1);
                                if (getOrientDif(dor1, or) > 3.14) {
                                    cancel[((d < 2) ? 0 : 1)] = true;
                                    continue;
                                }
                                double x3 = (double) (lensd * (Math.tan(dor1.x - or.x))) + (WIDTH / 2);
                                double y3 = (double) (lensd * (Math.tan(dor1.y + or.y))) + (HEIGHT / 2);
                                if (d < 2) {
                                    t1x[((d == 0) ? 1 : 2)] = (int) x3;
                                    t1y[((d == 0) ? 1 : 2)] = (int) y3;
                                } else {
                                    t2x[((d == 2) ? 1 : 2)] = (int) x3;
                                    t2y[((d == 2) ? 1 : 2)] = (int) y3;
                                }
                                if (net && !panels) {
                                    g.drawLine((int) x1, (int) y1, (int) x3, (int) y3);
                                }
                            } else {
                                cancel[((d < 2) ? 0 : 1)] = true;
                            }

                        }
                        if (!net) {
                            if (!cancel[0]) {
                                g.fillPolygon(t1x, t1y, 3);
                            }
                            if (!cancel[1]) {
                                g.fillPolygon(t2x, t2y, 3);
                            }
                        }
                        //if (panels&&!cancel[0]&&!cancel[1]){pls.add(new int[][]{{dist},t1x,t1y,t2x,t2y,col});}
                        if (panels) {
                            pls.add(new int[][]{{dist}, (!cancel[0]) ? t1x : null, (!cancel[0]) ? t1y : null, (!cancel[1]) ? t2x : null, (!cancel[1]) ? t2y : null, col});
                        }
                        //g.fillOval((int) x1 - 5, (int) y1 - 5, 10, 10);
                        rn++;
                        x++;
                        if (aim != 0) {
                            if (x % aim == 0) {
                                y += (im < 0) ? -1 : 1;
                            }
                        }
                    }
                }
                if (aim != 0) {
                    for (int c = aim; c < msize; c += aim) {
                        int x = c;
                        int y = msize - 1;
                        while (x >= 0 && x < msize && y >= 0 && y < msize) {
                            //Vec3d p=(!small)?pointmap[x][y]:pointmap[y][x];
                            int x2 = (small) ? x : y;
                            int y2 = (small) ? y : x;
                            if (q == 2) {
                                x2 = msize - 1 - x2;
                            } else if (q == 3) {
                                x2 = msize - 1 - x2;
                                y2 = msize - 1 - y2;
                            } else if (q == 4) {
                                y2 = msize - 1 - y2;
                            }
                            Vec3d p = pointmap[x2][y2];
                            if (p == null) {
                                rn++;
                                x++;
                                if (aim != 0) {
                                    if (x % aim == 0) {
                                        y += (im < 0) ? -1 : 1;
                                    }
                                }
                                continue;
                            }
                            Vec3d dv = getDeltaVecBetween(rotate(p, roty), pos);
                            int dist = (int) (dv.length() * 10);
                            Vec2d dor = getDeltaOrient(dv);
                            if (getOrientDif(dor, or) > 3.14159) {
                                rn++;
                                x++;
                                if (aim != 0) {
                                    if (x % aim == 0) {
                                        y += (im < 0) ? -1 : 1;
                                    }
                                }
                                continue;
                            }
                            double x1 = (double) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                            double y1 = (double) (lensd * (Math.tan(dor.y + or.y))) + (HEIGHT / 2);

                            int[] col = getColorAt(x2, y2, p);
                            if (!panels) {
                                g.setColor(new Color(col[0], col[1], col[2]));
                            }

                            int[] t1x = new int[]{(int) x1, 0, 0};
                            int[] t1y = new int[]{(int) y1, 0, 0};
                            int[] t2x = new int[]{(int) x1, 0, 0};
                            int[] t2y = new int[]{(int) y1, 0, 0};

                            boolean[] cancel = new boolean[2];
                            for (int d = 0; d < 4; d++) {
                                int x4 = x2 + ((d % 2 == 0) ? 0 : ((d == 1) ? 1 : -1));
                                int y4 = y2 + ((d % 2 == 1) ? 0 : ((d == 0) ? 1 : -1));
                                if (net && parametric) {
                                    if (d % 2 == 1) {
                                        continue;
                                    }
                                }

                                if (x4 >= 0 && y4 >= 0 && y4 < pointmap[0].length && x4 < pointmap.length) {
                                    Vec3d p2 = pointmap[x4][y4];
                                    if (p2 == null) {
                                        cancel[((d < 2) ? 0 : 1)] = true;
                                        continue;
                                    }
                                    Vec3d dv1 = getDeltaVecBetween(rotate(p2, roty), pos);
                                    Vec2d dor1 = getDeltaOrient(dv1);
                                    if (getOrientDif(dor1, or) > 3.14) {
                                        cancel[((d < 2) ? 0 : 1)] = true;
                                        continue;
                                    }

                                    double x3 = (double) (lensd * (Math.tan(dor1.x - or.x))) + (WIDTH / 2);
                                    double y3 = (double) (lensd * (Math.tan(dor1.y + or.y))) + (HEIGHT / 2);
                                    if (d < 2) {
                                        t1x[((d == 0) ? 1 : 2)] = (int) x3;
                                        t1y[((d == 0) ? 1 : 2)] = (int) y3;
                                    } else {
                                        t2x[((d == 2) ? 1 : 2)] = (int) x3;
                                        t2y[((d == 2) ? 1 : 2)] = (int) y3;
                                    }
                                    if (net & !panels) {
                                        g.drawLine((int) x1, (int) y1, (int) x3, (int) y3);
                                    }
                                } else {
                                    cancel[((d < 2) ? 0 : 1)] = true;
                                }
                            }
                            if (!net) {
                                if (!cancel[0]) {
                                    g.fillPolygon(t1x, t1y, 3);
                                }
                                if (!cancel[1]) {
                                    g.fillPolygon(t2x, t2y, 3);
                                }
                            }
                            //if (panels&&!cancel[0]&&!cancel[1]){pls.add(new int[][]{{dist},t1x,t1y,t2x,t2y,col});}
                            if (panels) {
                                pls.add(new int[][]{{dist}, (!cancel[0]) ? t1x : null, (!cancel[0]) ? t1y : null, (!cancel[1]) ? t2x : null, (!cancel[1]) ? t2y : null, col});
                            }
                            //g.fillOval((int) x1 - 5, (int) y1 - 5, 10, 10);
                            rn++;
                            x++;
                            if (aim != 0) {
                                if (x % aim == 0) {
                                    y += ((im < 0) ? -1 : 1);
                                }
                            } else {
                                y += ((im < 0) ? -1 : 1);
                            }
                        }
                    }
                }
            }else {
                for (int x=0; x<mw; x++){
                    for (int y=0; y<mh; y++){
                        Vec3d p = pointmap[x][y];
                        if (p == null) { continue; }
                        //Vec3d dv = getDeltaVecBetween(p, pos);
                        Vec3d dv = getDeltaVecBetween(rotate(p, roty), pos);
                        int dist = (int) (dv.length() * 10);
                        Vec2d dor = getDeltaOrient(dv);
                        if (getOrientDif(dor, or) > 3.14159) { continue; }

                        double x1 = (double) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                        double y1 = (double) (lensd * (Math.tan(dor.y + or.y))) + (HEIGHT / 2);

                        int[] col = getColorAt(x, y, p);
                        if (!panels) {
                            g.setColor(new Color(col[0], col[1], col[2]));
                        }
                        int[] t1x = new int[]{(int) x1, 0, 0};
                        int[] t1y = new int[]{(int) y1, 0, 0};
                        int[] t2x = new int[]{(int) x1, 0, 0};
                        int[] t2y = new int[]{(int) y1, 0, 0};
                        boolean[] cancel = new boolean[2];
                        for (int d = 0; d < 4; d++) {
                            int x4 = x + ((d % 2 == 0) ? 0 : ((d == 1) ? 1 : -1));
                            int y4 = y + ((d % 2 == 1) ? 0 : ((d == 0) ? 1 : -1));
                            /*if (net && parametric) {
                                if (d % 2 == 1) {
                                    continue;
                                }
                            }*/

                            if (x4 >= 0 && y4 >= 0 && y4 < pointmap[0].length && x4 < pointmap.length) {
                                Vec3d p2 = pointmap[x4][y4];
                                if (p2 == null) {
                                    cancel[((d < 2) ? 0 : 1)] = true;
                                    continue;
                                }
                                Vec3d dv1 = getDeltaVecBetween(rotate(p2, roty), pos);
                                Vec2d dor1 = getDeltaOrient(dv1);
                                if (getOrientDif(dor1, or) > 3.14) {
                                    cancel[((d < 2) ? 0 : 1)] = true;
                                    continue;
                                }
                                double x3 = (double) (lensd * (Math.tan(dor1.x - or.x))) + (WIDTH / 2);
                                double y3 = (double) (lensd * (Math.tan(dor1.y + or.y))) + (HEIGHT / 2);
                                if (d < 2) {
                                    t1x[((d == 0) ? 1 : 2)] = (int) x3;
                                    t1y[((d == 0) ? 1 : 2)] = (int) y3;
                                } else {
                                    t2x[((d == 2) ? 1 : 2)] = (int) x3;
                                    t2y[((d == 2) ? 1 : 2)] = (int) y3;
                                }
                                if (net && !panels) {
                                    g.drawLine((int) x1, (int) y1, (int) x3, (int) y3);
                                }
                            } else {
                                cancel[((d < 2) ? 0 : 1)] = true;
                            }

                        }
                        if (!net) {
                            if (!cancel[0]) {
                                g.fillPolygon(t1x, t1y, 3);
                            }
                            if (!cancel[1]) {
                                g.fillPolygon(t2x, t2y, 3);
                            }
                        }
                        //if (panels&&!cancel[0]&&!cancel[1]){pls.add(new int[][]{{dist},t1x,t1y,t2x,t2y,col});}
                        if (panels) {
                            pls.add(new int[][]{{dist}, (!cancel[0]) ? t1x : null, (!cancel[0]) ? t1y : null, (!cancel[1]) ? t2x : null, (!cancel[1]) ? t2y : null, col});
                        }
                    }
                }
            }
            //ORDER PANELS
            if (panels) {
                ArrayList<int[][]> o1 = new ArrayList<>();
                double[] dists = new double[pls.size()];
                int i1 = 0;
                for (int[][] o : pls) {
                    //Vec3d vd=new Vec3d(pos.x-o.loc.x,pos.y-o.loc.y,pos.z-o.loc.z);
                    dists[i1] = o[0][0];
                    i1++;
                }
                ArrayList<Integer> s = new ArrayList<>();
                for (int i = 0; i < dists.length; i++) {
                    if (s.size() == 0) {
                        s.add(i);
                    } else {
                        int addin = s.size() - 1;
                        for (Integer k : s) {
                            if (dists[k] > dists[i]) {
                                addin = s.indexOf(k);
                                break;
                            }
                        }
                        s.add(addin, i);
                    }
                }
                for (Integer i : s) {
                    o1.add(0, pls.get(i));
                }
                pls = o1;
                int d=0;
                for (int[][] p : pls) {
                    g.setColor(new Color(p[5][0],p[5][1],p[5][2]));
                    //System.out.println(p[5][0]+", "+p[5][1]+", "+p[5][2]);
                    //System.out.println("poly "+" ("+p[1][0]+", "+p[2][0]+" ) , "+" ("+p[1][1]+", "+p[2][1]+" ) , "+" ("+p[1][2]+", "+p[2][2]+" ) ");

                    if (p[1]!=null) {
                        d++;
                        g.fillPolygon(p[1], p[2], 3);
                    }
                    if (p[3]!=null) {
                        d++;
                        g.fillPolygon(p[3], p[4], 3);
                    }
                }
                //System.out.println("drawn "+d);
            }
        }


    }

    public Vec3d rotate(Vec3d p,double orient){
        double r=(double)(Math.sqrt((p.x*p.x)+(p.z*p.z)));
        double o=(double)(Math.atan2(p.z,p.x));
        double o1=o+orient;
        double x1=r*(double)Math.cos(o1);
        double z1=r*(double)Math.sin(o1);
        return new Vec3d(x1,p.y,z1);
    }

    public Color doesLineCross(Vec2d orient, Vec3d pos){
        //d=dist from source
        //x^2+y*2+z^2=r
        if (shape==0){
            Vec3d dv=getDeltaVecBetween(pos,loc);
            Vec2d dor=getDeltaOrient(dv);
            //System.out.println("looking in "+orient+", at "+dor);
            double odif=getOrientDif(orient,dor);
            double arclength=(odif*getDistOfDelta(dv));
            if(arclength<rad){
                double cm=1-(arclength/rad);
                double cr=(rad<=4)?rad/4:1;
                Color c=new Color((float)cr*(float)cm,0,(float)cm);
                return c;
            }
            //double cr=getDistOfDelta(dv);
        }
        return null;
    }
    public double getDistOfDelta(Vec3d dp){
        return (double)(Math.sqrt((dp.x*dp.x)+(dp.y*dp.y)+(dp.z*dp.z)));
    }

    public void attractTo(Vec3d p1,double amt){
        Vec3d dv=getDeltaVecBetween(loc,p1);
        double r= getDistOfDelta(dv);
        double acc=(amt/(r*r))/vol;
        //Vec3d newv=new Vec3d();
        if(Math.abs(dv.x)>rad){ vel.x=(dv.x>0) ?vel.x+acc : vel.x-acc;}
        if(Math.abs(dv.y)>rad){vel.y=(dv.y>0) ?vel.y+acc : vel.y-acc;}
        if(Math.abs(dv.z)>rad){vel.z=(dv.z>0) ?vel.z+acc : vel.z-acc;}
    }

    public Vec2d getDeltaOrient(Vec3d dp){
        double r1=(double)(Math.sqrt((dp.x*dp.x)+(dp.y*dp.y)));
        double th1=(double)Math.atan2(dp.z,r1);
        double th0=(double)Math.atan2(dp.y,dp.x);
        return new Vec2d(th0,th1);
    }
    public Vec3d getDeltaVecBetween(Vec3d p1, Vec3d p2){
        return new Vec3d(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);
    }
    public double getOrientDif(Vec2d o1, Vec2d o2){
        double d0=getAngDif(o1.x,o2.x);
        double d1=getAngDif(o1.y,o2.y);
        return (double)Math.sqrt((d0*d0)+(d1*d1));
    }
    public double getAngDif(double a1, double a2){
        double ad=a1-a2;
        if(ad<-3.14159){
            ad+=6.28318;
        }else if(ad>3.14159){
            ad-=6.28318;
        }
        return ad;
    }
    public Vec3d getLocOnLine(Vec2d orient, Vec3d pos, double r) {
        double r1=(double)(r*Math.cos(orient.y));
        double x=(double)(r1*Math.cos(orient.x));
        double y=(double)(r1*Math.sin(orient.x));
        double z=(double)(r1*Math.tan(orient.y));
        return new Vec3d(x+pos.x,y+pos.y,z+pos.z);
    }
}
