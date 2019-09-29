import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

public class Object {
    int shape=0;//sphere
    Vec3f loc;
    Vec3f vel;
    public Object(int shape, Vec3f loc, Vec3f vel){
        this.vel=vel;
        this.shape=shape;
        this.loc=loc;
    }
    public boolean doesLineCross(Vec2f orient, Vec3f pos){
        //d=dist from source
        //x^2+y*2+z^2=r
        if (shape==0){
            
        }
        return false;
    }

    public Vec3f getLocOnLine(Vec2f orient, Vec3f pos, float r) {
        float r1=(float)(r*Math.cos(orient.y));
        float x=(float)(r1*Math.cos(orient.x));
        float y=(float)(r1*Math.sin(orient.x));
        float z=(float)(r1*Math.tan(orient.y));
        return new Vec3f(x+pos.x,y+pos.y,z+pos.z);
    }
}
