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
}
