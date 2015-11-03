package pl.slapps.dot.drawing;

import pl.slapps.dot.route.Route;

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
        init();
    }

    private void init() {
        vertices = new float[]{
                bottomLeft.x, bottomLeft.y, bottomLeft.z, // 0 bottom left

                topLeft.x, topLeft.y, topLeft.z, // 1 top left
                bottomRight.x, bottomRight.y, bottomRight.z, // 2 bottom right
                topRight.x, topRight.y, topRight.z// 3 top right

        };

        indices = new short[]{bottomLeft.index, topLeft.index, bottomRight.index,
                bottomRight.index, topLeft.index, topRight.index};


    }


    public Quad(float centerX, float centerY, float width,
                float height) {
        bottomLeft = new Verticle((centerX - width / 2), (centerY + height / 2), 0, (short) 0);
        topLeft = new Verticle((centerX - width / 2), (centerY - height / 2), 0, (short) 1);
        bottomRight = new Verticle((centerX + width / 2), (centerY + height / 2), 0, (short) 2);

        topRight = new Verticle((centerX + width / 2), (centerY - height / 2), 0, (short) 3);

        init();


    }


}
