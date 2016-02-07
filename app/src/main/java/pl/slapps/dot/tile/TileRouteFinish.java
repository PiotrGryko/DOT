package pl.slapps.dot.tile;

import android.util.Log;

import org.json.JSONObject;

import pl.slapps.dot.game.Junction;
import pl.slapps.dot.game.Wall;
import pl.slapps.dot.game.GameView;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 17.10.15.
 */
public class TileRouteFinish extends TileRoute {

    public String TAG = TileRouteFinish.class.getName();
    private boolean passed = false;
    private Wall wall;
    private Wall.Type type;



    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, GameView view, Route route) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route,view);

    }

    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, GameView view, String from, String to) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Route.Type.FINISH,view);

    }

    public void initRoute(Route.Movement d) {
        super.initRoute(d);

        Route.Direction f = this.to;

        if(view==null)
            Log.d("XXX", "tile route finish view is null " + d.name());

        switch (f) {
            case LEFT: {

                wall = new Wall(new Junction(topX, topY + height - borderY, 0),
                        new Junction(topX, topY + borderY, 0), Wall.Type.LEFT,view);

                this.walls.add(wall);
                type = Wall.Type.LEFT;

                break;
            }

            case RIGHT: {
                wall = new Wall(new Junction(topX + width, topY + height - borderY, 0),
                        new Junction(topX + width, topY + borderY, 0), Wall.Type.RIGHT,view);

                walls.add(wall);
                type = Wall.Type.RIGHT;

                break;
            }
            case TOP: {
                wall = new Wall(new Junction(topX + borderX, topY, 0),
                        new Junction(topX + width - borderX, topY, 0), Wall.Type.TOP,view);

                walls.add(wall);
                type = Wall.Type.TOP;

                break;
            }
            case BOTTOM: {
                wall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + width - borderX, topY + height, 0), Wall.Type.BOTTOM,view);

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
            // view.explodeDot();
            //view.context.playFinishSound();
            view.moveToNextLvl();
        }

        return t;
    }


}
