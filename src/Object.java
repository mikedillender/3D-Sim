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
    Vec3f[][] pointmap;
    float timer=0;
    boolean side=false;
    float avg=0;
    boolean polar=false;
    boolean land=false;
    float[][] vrngs=new float[3][3];

    public Object(int shape, Vec3f loc, Vec3f vel,float rad){
        points=new ArrayList<>();
        this.color=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        this.vel=vel;
        this.shape=shape;
        this.loc=loc;
        this.rad=rad;
        if (shape==1||shape==2){createPoints();}
    }

    public void createPoints(){
        int numAdded=0;
        float sum=0;
        float minr=0;
        float maxr=0;
        if (shape==1){
            points=new ArrayList<>();
            for (int x=-1; x<=1;x+=2){
                for (int y=-1; y<=1; y+=2) {
                    for (int z = -1; z <= 1; z += 2) {
                        points.add(new Vec3f(x * BOUNDS[0], y * BOUNDS[1], z * BOUNDS[2]));
                    }
                }
            }
        }else if (shape==2){
            float sep=30;
            int size=32;
            int maxv=7;
            int v=(int)(Math.floor(Math.random()*(maxv+.99)));
            v=9;

            float vr=25;
            int vs=(int)(Math.random()*5)+5;
            float[][] vecs=new float[vs][];

            for (int i=0; i<vs; i++) {
                vecs[i]=new float[]{(float)(Math.random()*vr-(vr/2)),(float)(Math.random()*vr-(vr/2)),(float)(Math.random()*vr-(vr/2)),(float)(Math.random()*vr-(vr/2))};
            }
            for (float[] vc:vecs) {
                if (Math.abs(vc[1])<1){ vc[1]=(vc[1]<0)?-1:1; }
                if (Math.abs(vc[3])<1){ vc[3]=(vc[3]<0)?-1:1; }
            }
            pointmap=new Vec3f[size*2+1][size*2+1];
            for (int x=-size; x<=size;x+=1){
                for (int y=-size; y<=size; y+=1) {
                    float[] v1=new float[]{x*sep,y*sep,0};
                    switch (v){
                        case 0:
                            v1[2]=size*((size-Math.abs(x))*(size-Math.abs(y))/(float)(size*size));
                            break;
                        case 1:
                            v1[2]=(float)(Math.sin(x/4f)*size)/(Math.abs(y)+.5f);
                            break;
                        case 2:
                            for (float[] vc:vecs){
                                v1[2]=v1[2]+(float)(Math.sin(((x+vc[0])/vc[1])+((y+vc[2])/vc[3])));
                            }
                            v1[2]=v1[2]/vecs.length;
                            //z=sep*sep/8f*(float)(Math.sin(x/3f)+Math.sin(2.1+x/12f+y/4f)-Math.cos(1.21+(x+y)/8f));
                            v1[2]*=sep*sep/4f;
                            land=true;
                            break;
                        case 3:
                            float c=(size*size)-(x*x)-(y*y);//DOme
                            v1[2]=(c>0)?(float)(Math.sqrt(c)*sep):0;
                            break;
                        case 4:
                            float c1=(float)Math.sqrt((x*x)+(y*y));
                            float c2=25-(float)Math.pow(Math.abs((14-c1)),2);
                            if (c2<-20){continue;}
                            v1[2]=(c2>0)?(sep*(float)Math.sqrt(c2)):0;
                            break;
                        case 5:
                            v1[2]=(float)(sep*sep*Math.sin(x+y));
                            break;
                        case 6:
                            float c3=(float)Math.sqrt((x*x)+(y*y));
                            v1[2]=(float)(sep*sep/4f*Math.cos(c3/2f));
                            break;
                        case 7://polar attempt
                            //float r=(float)(200*Math.sin((y)/(size*2f)));
                            float xor=((3.14159f)*((x+size)/(float)size));
                            float yor=((3.14159f/2f)*((y)/(float)size));
                            float r=200*(float)(Math.sin(xor*2));

                            if (Float.isNaN(r)||Float.isInfinite(r)){r=200;}
                            float r1 = r * (float) (Math.cos(-yor));
                            v1[2] = r * (float) (Math.sin(yor));
                            v1[0] = -r1 * (float) (Math.cos(-xor));
                            v1[1] = r1 * (float) (Math.sin(-xor));
                            polar=true;
                            break;
                        case 8:
                            xor=((3.14159f)*((x+size)/(float)size));
                            yor=((3.14159f/2f)*((y)/(float)size));
                            //if (Math.abs(xor)%3.1415>.2){continue;}
                            float s=100;
                            //float
                            //r=(float)Math.sqrt((Math.pow((100/Math.cos(Math.abs((xor%(3.14159f/2))-(3.14159f/4)))),2)+Math.pow(100/Math.cos(Math.abs(((yor+(3.14159f/4))%(3.14159f/2))-(3.14159f/4))),2)));
                            //r=(float)(((100/((Math.cos(Math.abs((xor%(3.14159f/2))-(3.14159f/4))))*Math.cos(Math.abs(((yor+(3.14159f/4))%(3.14159f/2))-(3.14159f/4)))))));
                            r=100;
                            float cornerx=(float)Math.abs(((yor+(Math.PI/4))%(Math.PI/2))-(Math.PI/4));
                            float cornery=(float)Math.abs(((xor+(Math.PI/4))%(Math.PI/2))-(Math.PI/4));
                            float r3=(float)(s/Math.cos(xor));

                            r=(float)((100/(Math.cos(cornery)))/Math.cos(cornerx));
                            if (r>190){
                                System.out.println(r+" at "+(int)(cornerx*180/3.14)+", "+(int)(cornery*180/3.14));
                            }
                            //r=(float)(100/Math.cos(Math.abs((yor%(3.14159f/2)))));
                            //if (yor>Math.PI/4){
                                //r=(float)(100/Math.cos(Math.abs((xor%(3.14159f/2)))));
                            r=100;
                            //}
                            //if (r>Math.sqrt(30000)){r=(float)(Math.sqrt(30000));}
                            //if (Math.abs((yor%(3.14159f)))>(Math.PI/4f)){r=0;}
                            //r=(float)(((100/(Math.cos(xor)*Math.sin(yor)))));
                            //r=(float)((100f/Math.cos(Math.abs(((xor%(Math.PI/2))-(Math.PI/4))))));
                            //r=200*xor;
                            //if (xor>Math.PI/4||yor>Math.PI/4){ r=10;}
                            if (Float.isNaN(r)||Float.isInfinite(r)){continue;}
                            if (r<minr){minr=r;}
                            if (r>maxr){maxr=r;}
                            r1 = r * (float) (Math.cos(yor));
                            v1[2] = r * (float) (Math.sin(yor));
                            v1[0] = r1 * (float) (Math.cos(xor));
                            v1[1] = r1 * (float) (Math.sin(xor));
                            polar=true;
                            break;
                        case 9://polar attempt
                            //float r=(float)(200*Math.sin((y)/(size*2f)));
                            xor=((3.14159f)*((x+size)/(float)size));
                            yor=((3.14159f/2f)*((y)/(float)size));
                            //System.out.println(xor+", "+yor);
                            r=200*(float)(Math.sin(xor*2)*Math.cos(yor*2));
                            //r=200;
                            //r=200*(float)(xor*yor/36);
                            //r=200*(float)Math.sqrt(xor/(yor+1));
                            //r=200*(float)((Math.abs(yor-Math.PI)));
                            //r=(float)(200*(xor));
                            if (Float.isNaN(r)||Float.isInfinite(r)){r=200;}
                            r1 = r * (float) (Math.cos(yor));
                            v1[2] = r * (float) (Math.sin(yor));
                            v1[0] = r1 * (float) (Math.cos(xor));
                            v1[1] = r1 * (float) (Math.sin(xor));
                            polar=true;
                            break;
                    }

                    for (int i=0; i<3; i++){
                        if (v1[i]>vrngs[i][1]){ vrngs[i][1]=(float)Math.ceil(v1[i]); }
                        if (v1[i]<vrngs[i][0]){ vrngs[i][0]=(float)Math.floor(v1[i]); }
                    }
                    sum+=v1[2];
                    numAdded++;
                    pointmap[size+x][size+y]=(new Vec3f(v1[0], (side)?v1[2]:(v1[1]), (side)?v1[1]:v1[2]));

                }
            }
        }
        System.out.println("radius:"+minr+"-"+maxr);
        avg=sum/numAdded;
        for (int i=0; i<3; i++) {
            vrngs[i][2]=vrngs[i][1]-vrngs[i][0];
            System.out.println(i+" | "+vrngs[i][0]+" - "+vrngs[i][1]+" ("+vrngs[i][2]+")");
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

    public int[] getColorAt(int x, int y, Vec3f p){
        float zmult=(((!side)?((p.z-vrngs[2][0])/vrngs[2][2]):((p.y-vrngs[1][0])/vrngs[1][2])));
        int msize=pointmap.length;
        if (land){
            boolean abvavg=(((!side)?p.z:p.y)>avg);
            //if (!abvavg){rn++;x++;if (aim!=0){ if(x%aim==0){ y+=(im<0)?-1:1; }}continue;}
            return new int[]{(int)(100*zmult),(int)(zmult*255),(int)((abvavg)?80:(int)((zmult*500>255)?255:(zmult*500)))};
        }else {
            //return new int[]{(int) (255 * x * zmult / (float) msize), (int) (zmult * (255 - (255 * x / (float) msize))), (int) (zmult * 255 * y / (float) msize)};
            int r=(int)(zmult*((x>(msize/2))?240:0));
            int g=(int)(zmult*((y>(msize/2))?240:0));
            int b=(int)(zmult*150);

            //return new int[]{(int)(255*x/(float)msize),(int)(255*y/(float)msize),100};
            return new int[]{r,g,b};
        }
    }

    public void render(Graphics g, int WIDTH, int HEIGHT, float lensd, Vec3f pos, Vec2f or,float roty){
        if (shape==0) {
            if (points.size()<2){return;}
            g.setColor(color);
            Vec2f last=null;
            for (int i=0; i<points.size(); i++) {
                Vec3f dv = getDeltaVecBetween(pos, points.get(i));
                Vec2f dor = getDeltaOrient(dv);
                if (getOrientDif(dor, or) > 3.14159) {
                    continue;
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
            if (getOrientDif(dor, or) > 3.14159) {
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
                g.setColor(Color.BLACK);
                g.fillOval((int)x-5,(int)y-5,10,10);
                for (Vec3f p2: points){
                    int sim=0;if(p2.x==p.x){sim++;}if(p2.y==p.y){sim++;}if(p2.z==p.z){sim++;}
                    if (sim!=2){continue;}
                    //if (!(p2.x==p.x || p.y==p2.y || p.z==p2.z)){continue;}
                    Vec3f dv1 = getDeltaVecBetween(p2,pos);
                    Vec2f dor1 = getDeltaOrient(dv1);
                    if (getOrientDif(dor,or)>3.14){continue;}
                    float x1=(float)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                    float y1=(float)(lensd*(Math.tan(dor1.y+or.y)))+(HEIGHT/2);
                    g.drawLine((int)x,(int)y,(int)x1,(int)y1);

                }
                //System.out.println("rendering at "+x+", "+y);
            }
        }else if (shape==2){
            boolean f=true;
            int msize=pointmap.length;
            float m=-(Math.abs(pos.x)/Math.abs(pos.y));
            boolean small=Math.abs(m)<1;
            //boolean small=true;
            if (small){m=1f/m;}
            if (Math.abs(m)>msize){small=!small;m=0;}
            int im=Math.round(m);
            int aim=Math.abs(im);
            int q=(pos.x<0)?((pos.y>0)?2:3):((pos.y>0)?1:4);
            //System.out.println("in quadrant "+q+" | p : "+pos);
            //System.out.println("in quad "+q+", angle small? =  "+small+" |  m = "+aim);
            boolean net=false;
            if (polar){net=true;}
            boolean panels=false;if (polar){panels=true;};
            ArrayList<int[][]> pls=new ArrayList<>();
            int rn=0;
            int mssq=msize*msize+(msize*4);
            land=false;
            if (polar){land=false;}
            for (int c=0; c<msize; c++){
                int x=0;
                int y=c;
                while (x>=0&&x<msize&&y>=0&&y<msize){
                    //Vec3f p=(!small)?pointmap[x][y]:pointmap[y][x];
                    int x2=(small)?x:y;
                    int y2=(small)?y:x;
                    if(q==2){
                        x2=msize-1-x2;
                    }else if(q==3){
                        x2=msize-1-x2;
                        y2=msize-1-y2;
                    }else if(q==4){
                        y2=msize-1-y2;
                    }
                    Vec3f p=pointmap[x2][y2];
                    if (p==null){ rn++;x++;if (aim!=0){ if(x%aim==0){ y+=(im<0)?-1:1; }}continue; }
                    //Vec3f dv = getDeltaVecBetween(p, pos);
                    Vec3f dv = getDeltaVecBetween(rotate(p,roty), pos);
                    int dist=(int)(dv.length()*10);
                    Vec2f dor = getDeltaOrient(dv);
                    float x1 = (float) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                    float y1 = (float) (lensd * (Math.tan(dor.y + or.y))) + (HEIGHT / 2);

                    int[] col=getColorAt(x2,y2,p);
                    if (!panels){g.setColor(new Color(col[0],col[1],col[2]));}
                    int[] t1x=new int[]{(int)x1,0,0};
                    int[] t1y=new int[]{(int)y1,0,0};
                    int[] t2x=new int[]{(int)x1,0,0};
                    int[] t2y=new int[]{(int)y1,0,0};
                    boolean[] cancel=new boolean[2];
                    for (int d=0; d<4; d++){
                        int x4=x2+((d%2==0)?0:((d==1)?1:-1));
                        int y4=y2+((d%2==1)?0:((d==0)?1:-1));
                        if (x4>=0&&y4>=0&&y4<pointmap[0].length&&x4<pointmap.length){
                            Vec3f p2=pointmap[x4][y4];
                            if (p2==null){ cancel[((d<2)?0:1)]=true;continue; }
                            Vec3f dv1 = getDeltaVecBetween(rotate(p2,roty),pos);
                            Vec2f dor1 = getDeltaOrient(dv1);
                            if (getOrientDif(dor,or)>3.14){continue;}
                            float x3=(float)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                            float y3=(float)(lensd*(Math.tan(dor1.y+or.y)))+(HEIGHT/2);
                            if (d<2){
                                t1x[((d==0)?1:2)]=(int)x3;
                                t1y[((d==0)?1:2)]=(int)y3;
                            }else {
                                t2x[((d==2)?1:2)]=(int)x3;
                                t2y[((d==2)?1:2)]=(int)y3;
                            }
                            if(net&&!panels){g.drawLine((int)x1,(int)y1,(int)x3,(int)y3);}
                        }else {
                            cancel[((d<2)?0:1)]=true;
                        }
                    }
                    if (!net){ if (!cancel[0]){ g.fillPolygon(t1x,t1y,3);}if(!cancel[1]) { g.fillPolygon(t2x, t2y, 3); }}
                    //if (panels&&!cancel[0]&&!cancel[1]){pls.add(new int[][]{{dist},t1x,t1y,t2x,t2y,col});}
                    if (panels){pls.add(new int[][]{{dist},(!cancel[0])?t1x:null,(!cancel[0])?t1y:null,(!cancel[1])?t2x:null,(!cancel[1])?t2y:null,col});}
                    //g.fillOval((int) x1 - 5, (int) y1 - 5, 10, 10);
                    rn++;
                    x++;
                    if (aim!=0){ if(x%aim==0){ y+=(im<0)?-1:1; }}
                }
            }
            if (aim!=0) {
                for (int c = aim; c < msize; c += aim) {
                    int x = c;
                    int y = msize - 1;
                    while (x >= 0 && x < msize && y >= 0 && y < msize) {
                        //Vec3f p=(!small)?pointmap[x][y]:pointmap[y][x];
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
                        Vec3f p = pointmap[x2][y2];
                        if (p==null){ rn++;x++;if (aim!=0){ if(x%aim==0){ y+=(im<0)?-1:1; }}continue; }
                        Vec3f dv = getDeltaVecBetween(rotate(p,roty), pos);
                        int dist=(int)(dv.length()*10);
                        Vec2f dor = getDeltaOrient(dv);
                        float x1 = (float) (lensd * (Math.tan(dor.x - or.x))) + (WIDTH / 2);
                        float y1 = (float) (lensd * (Math.tan(dor.y + or.y))) + (HEIGHT / 2);

                        int[] col=getColorAt(x2,y2,p);
                        if (!panels){g.setColor(new Color(col[0],col[1],col[2]));}

                        int[] t1x=new int[]{(int)x1,0,0};int[] t1y=new int[]{(int)y1,0,0};
                        int[] t2x=new int[]{(int)x1,0,0};int[] t2y=new int[]{(int)y1,0,0};

                        boolean[] cancel=new boolean[2];
                        for (int d=0; d<4; d++){
                            int x4=x2+((d%2==0)?0:((d==1)?1:-1));
                            int y4=y2+((d%2==1)?0:((d==0)?1:-1));
                            if (x4>=0&&y4>=0&&y4<pointmap[0].length&&x4<pointmap.length){
                                Vec3f p2=pointmap[x4][y4];
                                if (p2==null){ cancel[((d<2)?0:1)]=true;continue; }
                                Vec3f dv1 = getDeltaVecBetween(rotate(p2,roty),pos);
                                Vec2f dor1 = getDeltaOrient(dv1);
                                if (getOrientDif(dor,or)>3.14){continue;}
                                float x3=(float)(lensd*(Math.tan(dor1.x-or.x)))+(WIDTH/2);
                                float y3=(float)(lensd*(Math.tan(dor1.y+or.y)))+(HEIGHT/2);
                                if (d<2){
                                    t1x[((d==0)?1:2)]=(int)x3;
                                    t1y[((d==0)?1:2)]=(int)y3;
                                }else {
                                    t2x[((d==2)?1:2)]=(int)x3;
                                    t2y[((d==2)?1:2)]=(int)y3;
                                }
                                if(net&!panels){g.drawLine((int)x1,(int)y1,(int)x3,(int)y3);}
                            }else {
                                cancel[((d<2)?0:1)]=true;
                            }
                        }
                        if (!net){ if (!cancel[0]){ g.fillPolygon(t1x,t1y,3);}if(!cancel[1]) { g.fillPolygon(t2x, t2y, 3); }}
                        //if (panels&&!cancel[0]&&!cancel[1]){pls.add(new int[][]{{dist},t1x,t1y,t2x,t2y,col});}
                        if (panels){pls.add(new int[][]{{dist},(!cancel[0])?t1x:null,(!cancel[0])?t1y:null,(!cancel[1])?t2x:null,(!cancel[1])?t2y:null,col});}
                        //g.fillOval((int) x1 - 5, (int) y1 - 5, 10, 10);
                        rn++;
                        x++;
                        if (aim != 0) {
                            if (x % aim == 0) {
                                y += ((im < 0) ? -1 : 1);
                            }
                        }else { y += ((im < 0) ? -1 : 1);}
                    }
                }
            }
            //ORDER PANELS
            if (panels) {
                ArrayList<int[][]> o1 = new ArrayList<>();
                float[] dists = new float[pls.size()];
                int i1 = 0;
                for (int[][] o : pls) {
                    //Vec3f vd=new Vec3f(pos.x-o.loc.x,pos.y-o.loc.y,pos.z-o.loc.z);
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
                for (int[][] p : pls) {
                    g.setColor(new Color(p[5][0],p[5][1],p[5][2]));
                    if (p[1]!=null) {
                        g.fillPolygon(p[1], p[2], 3);
                    }
                    if (p[3]!=null) {
                        g.fillPolygon(p[3], p[4], 3);
                    }
                }
            }
        }


    }

    public Vec3f rotate(Vec3f p,float orient){
        float r=(float)(Math.sqrt((p.x*p.x)+(p.z*p.z)));
        float o=(float)(Math.atan2(p.z,p.x));
        float o1=o+orient;
        float x1=r*(float)Math.cos(o1);
        float z1=r*(float)Math.sin(o1);
        return new Vec3f(x1,p.y,z1);
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
