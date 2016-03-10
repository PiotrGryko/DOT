package pl.slapps.dot.generator.builder;

import pl.slapps.dot.drawing.Junction;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 18.10.15.
 */
public class TileRouteStart extends TileRoute {






    public TileRouteStart(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route, Generator generator) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route,generator);


    }

    public TileRouteStart(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route);


    }



    public TileRouteStart(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, Route.Direction from,Route.Direction to, Generator generator) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Route.Type.START,generator);

    }

    private String TAG = TileRouteStart.class.getName();

    public void initRoute(Route.Movement d) {
        super.initRoute(d);

        Route.Direction f = this.from;


        switch (f) {
            case LEFT: {

                Wall lWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX, topY + borderY, 0), Wall.Type.LEFT, generator);

                this.walls.add(lWall);


                break;
            }

            case RIGHT: {
                Wall rWall = new Wall(new Junction(topX + width, topY + height - borderY, 0), new Junction(topX + width, topY + borderY, 0), Wall.Type.RIGHT, generator);

                walls.add(rWall);
                break;
            }
            case TOP: {
                Wall tWall = new Wall(new Junction(topX + borderX, topY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.TOP, generator);

                walls.add(tWall);
                break;
            }
            case BOTTOM: {
                Wall bWall = new Wall(new Junction(topX + borderX, topY + height, 0), new Junction(topX + width - borderX, topY + height, 0), Wall.Type.BOTTOM, generator);

                walls.add(bWall);
                break;
            }


        }
    }




}
