package pl.slapps.dot.tile;

import org.json.JSONObject;

import pl.slapps.dot.game.GameView;
import pl.slapps.dot.game.Junction;
import pl.slapps.dot.game.Wall;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 18.10.15.
 */
public class TileRouteStart extends TileRoute {


    public Wall lWall;
    public Wall rWall;

    private String TAG = TileRouteStart.class.getName();

    public void initRoute(Route.Movement d) {
        super.initRoute(d);

        Route.Direction f = this.from;

        switch (f) {
            case LEFT: {

                Wall lWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX, topY + borderY, 0), Wall.Type.LEFT);

                this.walls.add(lWall);


                break;
            }

            case RIGHT: {
                Wall rWall = new Wall(new Junction(topX + width, topY + height - borderY, 0), new Junction(topX + width, topY + borderY, 0), Wall.Type.RIGHT);

                walls.add(rWall);
                break;
            }
            case TOP: {
                Wall tWall = new Wall(new Junction(topX + borderX, topY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.TOP);

                walls.add(tWall);
                break;
            }
            case BOTTOM: {
                Wall bWall = new Wall(new Junction(topX + borderX, topY + height, 0), new Junction(topX + width - borderX, topY + height, 0), Wall.Type.BOTTOM);

                walls.add(bWall);
                break;
            }


        }
    }


    public TileRouteStart(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route);

    }



    public TileRouteStart(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Route.Type.START);
    }


}
