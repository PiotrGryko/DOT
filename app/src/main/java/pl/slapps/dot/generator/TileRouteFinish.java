package pl.slapps.dot.generator;

import android.util.Log;

import pl.slapps.dot.drawing.Junction;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 17.10.15.
 */
public class TileRouteFinish extends TileRoute {

    public String TAG = TileRouteFinish.class.getName();
    private boolean passed = false;
    private Wall wall;
    private Wall.Type type;



    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route, Generator generator) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route,generator);

    }

    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route);

    }

    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber,  String from, String to,Generator generator) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Route.Type.FINISH,generator);

    }

    public void initRoute(Route.Movement d) {
        super.initRoute(d);

        Route.Direction f = this.to;


        switch (f) {
            case LEFT: {

                wall = new Wall(new Junction(topX, topY + height - borderY, 0),
                        new Junction(topX, topY + borderY, 0), Wall.Type.LEFT, generator);

                this.walls.add(wall);
                type = Wall.Type.LEFT;

                break;
            }

            case RIGHT: {
                wall = new Wall(new Junction(topX + width, topY + height - borderY, 0),
                        new Junction(topX + width, topY + borderY, 0), Wall.Type.RIGHT, generator);

                walls.add(wall);
                type = Wall.Type.RIGHT;

                break;
            }
            case TOP: {
                wall = new Wall(new Junction(topX + borderX, topY, 0),
                        new Junction(topX + width - borderX, topY, 0), Wall.Type.TOP, generator);

                walls.add(wall);
                type = Wall.Type.TOP;

                break;
            }
            case BOTTOM: {
                wall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + width - borderX, topY + height, 0), Wall.Type.BOTTOM, generator);

                walls.add(wall);
                type = Wall.Type.BOTTOM;

                break;
            }

        }
    }

    public Wall.Type checkCollision(float x, float y, float width) {

        Wall.Type t = super.checkCollision(x, y, width);

        if (t == type && !passed) {
            passed = true;
            // generator.explodeDot();
            //generator.context.playFinishSound();
            //generator.moveToNextLvl();
        }

        return t;
    }


}
