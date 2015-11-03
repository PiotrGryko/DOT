package pl.slapps.dot.drawing;

/**
 * Created by piotr on 01.11.15.
 */
public class Verticle {

    public float x,y,z;
    public short index;

    public Verticle(float x, float y, float z, short index)
    {
        this.x=x;
        this.y=y;
        this.z=z;

        this.index=index;


    }

    public String print()
    {
        return x +" "+y +" "+z+" ";
    }

    public boolean equals(Verticle v)
    {
        if(x==v.x && y==v.y && z==v.z)
            return true;
        else
            return false;
    }
}
