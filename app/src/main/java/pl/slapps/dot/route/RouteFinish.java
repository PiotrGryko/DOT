package pl.slapps.dot.route;

import org.json.JSONObject;

import pl.slapps.dot.model.Junction;
import pl.slapps.dot.model.Wall;
import pl.slapps.dot.view.GameView;

/**
 * Created by piotr on 17.10.15.
 */
public class RouteFinish extends Route {

    public String TAG = RouteFinish.class.getName();
    private GameView view;
    private boolean passed = false;
    private Wall wall;
    private Wall.Type type;


    public RouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount,GameView view, JSONObject element,String color) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, element,color);
        this.view=view;
    }

    public RouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber,GameView view, String from, String to) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Type.FINISH);
        this.view=view;
    }

    public void initRoute(Movement d) {
        super.initRoute(d);

        Direction f = this.to;

        switch (f) {
            case LEFT: {

                wall = new Wall(new Junction(topX, topY + height - borderY, 0),
                        new Junction(topX, topY + borderY, 0), Wall.Type.LEFT);

                this.walls.add(wall);
                type = Wall.Type.LEFT;

                break;
            }

            case RIGHT: {
                wall = new Wall(new Junction(topX + width, topY + height - borderY, 0),
                        new Junction(topX + width, topY + borderY, 0), Wall.Type.RIGHT);

                walls.add(wall);
                type = Wall.Type.RIGHT;

                break;
            }
            case TOP: {
                wall = new Wall(new Junction(topX + borderX, topY, 0),
                        new Junction(topX + width - borderX, topY, 0), Wall.Type.TOP);

                walls.add(wall);
                type = Wall.Type.TOP;

                break;
            }
            case BOTTOM: {
                wall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + width - borderX, topY + height, 0), Wall.Type.BOTTOM);

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
