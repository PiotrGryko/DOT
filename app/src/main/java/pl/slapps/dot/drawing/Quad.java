package pl.slapps.dot.drawing;

import android.opengl.Matrix;
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

    public float left;
    public float top;
    public float right;
    public float bottom;
    public float centerX;
    public float centerY;



    public boolean contains(float x, float y) {


        if (x >= left && x <= right
                && y >= top && y <= bottom) {


            return true;
        }


        return false;
    }

    public static boolean checkIfContains(Quad parent, Quad child) {

        if (child.left >= parent.left
                && child.right <= parent.right
                && child.top >= parent.top
                && child.bottom <= parent.bottom) {
            return true;
        }


        return false;
    }

    public static Wall.Type checkChollision(Quad parent, Quad child) {


        if (child.left < parent.left)
            return Wall.Type.LEFT;
        else if (child.top < parent.top)
            return Wall.Type.TOP;
        else if (child.right > parent.right)
            return Wall.Type.RIGHT;
        else if (child.bottom > parent.bottom)
            return Wall.Type.BOTTOM;


        return null;
    }



    public Quad(Verticle bottomLeft, Verticle topLeft, Verticle bottomRight, Verticle topRight) {
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
        initSharedVerticles();
    }








    public void update(float x, float y)
    {
        bottomLeft.update(x, y);
        bottomRight.update(x, y);
        topLeft.update(x, y);
        topRight.update(x, y);
    }

    public void initSharedVerticles() {
        vertices = new float[]{
                bottomLeft.x, bottomLeft.y, 0.0f, // 0 bottom left

                topLeft.x, topLeft.y, 0.0f, // 1 top left
                bottomRight.x, bottomRight.y, 0.0f, // 2 bottom right
                topRight.x, topRight.y, 0.0f// 3 top right

        };

        indices = new short[]{bottomLeft.index, topLeft.index, bottomRight.index,
                bottomRight.index, topLeft.index, topRight.index};

        left = topLeft.x;
        right = topRight.x;
        bottom = bottomRight.y;
        top = topLeft.y;

    }



    public void initVertexArray(float centerX, float centerY, float width,
                                float height) {

        left = centerX - width / 2;
        right = centerX + width / 2;
        bottom = centerY + height / 2;
        top = centerY - height / 2;


        vertices = new float[]{
                centerX - width / 2, centerY + height / 2, 0.0f, // 0 bottom left
                centerX - width / 2, centerY - height / 2, 0.0f, // 1 top left
                centerX + width / 2, centerY + height / 2, 0.0f, // 2 bottom right

                centerX + width / 2, centerY + height / 2, 0.0f,// 3 bottom right
                centerX - width / 2, centerY - height / 2, 0.0f,// 4 top left
                centerX + width / 2, centerY - height / 2, 0.0f// 3 top right


        };


    }

    public void rotate(float angleInDegrees)
    {
/*
        angleInDegrees=5;
        float x = (float) (centerX + (vertices[0]-centerX)*Math.cos(angleInDegrees) - (vertices[1]-centerY)*Math.sin(angleInDegrees));
        float y = (float) (centerY + (vertices[0]-centerX)*Math.sin(angleInDegrees) + (vertices[1]-centerY)*Math.cos(angleInDegrees));
        vertices[0]=x;
        vertices[1]=y;


        float x1 = (float) (centerX + (vertices[3]-centerX)*Math.cos(angleInDegrees) - (vertices[4]-centerY)*Math.sin(angleInDegrees));
        float y1 = (float) (centerY + (vertices[3]-centerX)*Math.sin(angleInDegrees) + (vertices[4]-centerY)*Math.cos(angleInDegrees));
        vertices[3]=x1;
        vertices[4]=y1;


        float x2 = (float) (centerX + (vertices[6]-centerX)*Math.cos(angleInDegrees) - (vertices[7]-centerY)*Math.sin(angleInDegrees));
        float y2 = (float) (centerY + (vertices[6]-centerX)*Math.sin(angleInDegrees) + (vertices[7]-centerY)*Math.cos(angleInDegrees));
        vertices[6]=x2;
        vertices[7]=y2;


        float x3 = (float) (centerX + (vertices[9]-centerX)*Math.cos(angleInDegrees) - (vertices[10]-centerY)*Math.sin(angleInDegrees));
        float y3 = (float) (centerY + (vertices[9]-centerX)*Math.sin(angleInDegrees) + (vertices[10]-centerY)*Math.cos(angleInDegrees));
        vertices[9]=x3;
        vertices[10]=y3;


        float x4 = (float) (centerX + (vertices[12]-centerX)*Math.cos(angleInDegrees) - (vertices[13]-centerY)*Math.sin(angleInDegrees));
        float y4 = (float) (centerY + (vertices[12]-centerX)*Math.sin(angleInDegrees) + (vertices[13]-centerY)*Math.cos(angleInDegrees));
        vertices[12]=x4;
        vertices[13]=y4;


        float x5 = (float) (centerX + (vertices[15]-centerX)*Math.cos(angleInDegrees) - (vertices[16]-centerY)*Math.sin(angleInDegrees));
        float y5 = (float) (centerY + (vertices[15]-centerX)*Math.sin(angleInDegrees) + (vertices[16]-centerY)*Math.cos(angleInDegrees));
        vertices[15]=x5;
        vertices[16]=y5;
        //newY = centerY + (point2x-centerX)*Math.sin(angleInDegrees) + (point2y-centerY)*Math.cos(angleInDegrees);
        //  Matrix.rotateM(vertices, 0, angleInDegrees, 1.0f, 1.0f, 1.0f);
*/
    }




    public Quad(float centerX, float centerY, float width,
                float height) {

        left = centerX - width / 2;
        right = centerX + width / 2;
        bottom = centerY + height / 2;
        top = centerY - height / 2;

        bottomLeft = new Verticle((centerX - width / 2), (centerY + height / 2), 0, (short) 0);
        topLeft = new Verticle((centerX - width / 2), (centerY - height / 2), 0, (short) 1);
        bottomRight = new Verticle((centerX + width / 2), (centerY + height / 2), 0, (short) 2);
        topRight = new Verticle((centerX + width / 2), (centerY - height / 2), 0, (short) 3);
        initSharedVerticles();


    }

    public boolean compareTop(Quad next) {

        //Log.d("Path","compare top next: "+next.print()+"\n"+this.print());


        if (next.bottomLeft.equals(topLeft) && next.bottomRight.equals(topRight)) {


            return true;
        }
        return false;
    }

    public boolean compareRight(Quad next) {

        //Log.d("Path","compare right next: "+next.print()+"\n"+this.print());

        if (Math.floor(next.bottomLeft.x) == Math.ceil(bottomRight.x)
                && next.bottomLeft.y == bottomRight.y
                && Math.floor(next.topLeft.x) == Math.ceil(topRight.x)
                && next.topLeft.y == topRight.y
                )
        //if(next.bottomLeft.equals(bottomRight) && next.topLeft.equals(topRight))
        {
            return true;
        }
        return false;
    }

    public boolean compareLeft(Quad next) {

        //Log.d("Path","compare left next:"+next.print()+"\n"+this.print());

        if (Math.ceil(next.bottomRight.x) == Math.floor(bottomLeft.x)
                && next.bottomRight.y == bottomLeft.y
                && Math.ceil(next.topRight.x) == Math.floor(topLeft.x)
                && next.topRight.y == topLeft.y
                )
        //if(next.bottomRight.equals(bottomLeft) && next.topRight.equals(topLeft))
        {
            return true;
        }
        return false;
    }


    public boolean compareBottom(Quad next) {
        //Log.d("Path","compare bottom next: "+next.print()+"\n\n"+this.print());


        if (next.topLeft.equals(bottomLeft) && next.topRight.equals(bottomRight)) {
            return true;
        }
        return false;
    }

    public String print() {
        return "\nBottomLeft: " + this.bottomLeft.print() + "\nBottomRight: " + this.bottomRight.print() + "\nTopRight: " + this.topRight.print() + "\nTopLeft: " + this.topLeft.print();
    }


}
