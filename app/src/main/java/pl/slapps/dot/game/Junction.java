package pl.slapps.dot.game;

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
        //Log.d("fence", "comparing junctions " + this.x + " " + this.y + " " + junction.x + " " + junction.y);
        if(Math.round(this.z)==Math.round(junction.z) && Math.round(this.x)==Math.round(junction.x) && Math.round(this.y)==Math.round(junction.y))
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
