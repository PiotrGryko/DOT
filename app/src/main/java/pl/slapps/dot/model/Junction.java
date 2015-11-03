package pl.slapps.dot.model;

/**
 * Created by piotr on 12.10.15.
 */
public class Junction {
    public float x;
    public float y;
    public float z;



    public Junction(float x, float y, float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public boolean equals(Junction junction)
    {
        if(this.z==junction.z && this.x==junction.x && this.y==junction.y)
            return true;
        else
            return false;
    }

    public String print()
    {
        return "x y z "+ x+" "+y +" "+z;
    }

    public float[] getCoords()
    {
        return new float[]{this.x,this.y,this.z};
    }
}
