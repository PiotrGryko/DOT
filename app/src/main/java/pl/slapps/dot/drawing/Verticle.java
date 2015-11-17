package pl.slapps.dot.drawing;

import android.util.Log;

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

    public void update(float x, float y)
    {
        this.x+=x;
        this.y+=y;
    }

    public String print()
    {
        return x +" "+y +" "+z+" ";
    }

    public boolean equals(Verticle v)
    {
        //Log.d("path", "comparing verticles " +  this.x + " " + this.y + " " +  v.x + " " +  v.y);
        if((int)x==(int)v.x && (int)y==(int)v.y && (int)z==(int)v.z)
            return true;
        else
            return false;
    }
}
