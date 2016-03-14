package pl.slapps.dot.drawing;

import android.util.Log;

/**
 * Created by piotr on 01.11.15.
 */
public class Quad {


    public Verticle topLeft;
    public Verticle bottomLeft;
    public Verticle bottomRight;
    public Verticle topRight;


    public float[] vertices;
    public short[] indices;

    public Quad(Verticle bottomLeft, Verticle topLeft, Verticle bottomRight, Verticle topRight) {
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
        initSharedVerticles();
    }




    public void initSharedVerticles() {
        vertices = new float[]{
                bottomLeft.x, bottomLeft.y,0.0f, // 0 bottom left

                topLeft.x, topLeft.y,0.0f, // 1 top left
                bottomRight.x, bottomRight.y,0.0f, // 2 bottom right
                topRight.x, topRight.y,0.0f// 3 top right

        };

        indices = new short[]{bottomLeft.index, topLeft.index, bottomRight.index,
                bottomRight.index, topLeft.index, topRight.index};


    }



    public void initVertexArray(float centerX, float centerY, float width,
                                float height) {



        vertices = new float[]{
                centerX - width / 2, centerY + height / 2,0.0f, // 0 bottom left
                centerX - width / 2, centerY - height / 2,0.0f, // 1 top left
                centerX + width / 2, centerY + height / 2,0.0f, // 2 bottom right

                centerX + width / 2, centerY + height / 2,0.0f,// 3 bottom right
                centerX - width / 2, centerY - height / 2,0.0f,// 4 top left
                centerX + width / 2, centerY - height / 2,0.0f// 3 top right


        };



    }


    public Quad(float centerX, float centerY, float width,
                float height) {
        bottomLeft = new Verticle((centerX - width / 2), (centerY + height / 2), 0, (short) 0);
        topLeft = new Verticle((centerX - width / 2), (centerY - height / 2), 0, (short) 1);
        bottomRight = new Verticle((centerX + width / 2), (centerY + height / 2), 0, (short) 2);

        topRight = new Verticle((centerX + width / 2), (centerY - height / 2), 0, (short) 3);

        initSharedVerticles();


    }

    public boolean compareTop(Quad next)
    {

        //Log.d("Path","compare top next: "+next.print()+"\n"+this.print());


        if(next.bottomLeft.equals(topLeft) && next.bottomRight.equals(topRight))
        {


            return true;
        }
        return false;
    }

    public boolean compareRight(Quad next)
    {

        //Log.d("Path","compare right next: "+next.print()+"\n"+this.print());

        if(Math.floor(next.bottomLeft.x)==Math.ceil(bottomRight.x)
                &&next.bottomLeft.y==bottomRight.y
                &&Math.floor(next.topLeft.x)==Math.ceil(topRight.x)
                &&next.topLeft.y==topRight.y
                )
        //if(next.bottomLeft.equals(bottomRight) && next.topLeft.equals(topRight))
        {
            return true;
        }
        return false;
    }

    public boolean compareLeft(Quad next)
    {

        //Log.d("Path","compare left next:"+next.print()+"\n"+this.print());

        if(Math.ceil(next.bottomRight.x)==Math.floor(bottomLeft.x)
                &&next.bottomRight.y==bottomLeft.y
                &&Math.ceil(next.topRight.x)==Math.floor(topLeft.x)
                &&next.topRight.y==topLeft.y
                )
        //if(next.bottomRight.equals(bottomLeft) && next.topRight.equals(topLeft))
        {
            return true;
        }
        return false;
    }


    public boolean compareBottom(Quad next)
    {
        //Log.d("Path","compare bottom next: "+next.print()+"\n\n"+this.print());


        if(next.topLeft.equals(bottomLeft) && next.topRight.equals(bottomRight))
        {
            return true;
        }
        return false;
    }

    public String print()
    {
        return "\nBottomLeft: "+this.bottomLeft.print()+"\nBottomRight: "+this.bottomRight.print()+"\nTopRight: "+this.topRight.print()+"\nTopLeft: "+this.topLeft.print();
    }



}
