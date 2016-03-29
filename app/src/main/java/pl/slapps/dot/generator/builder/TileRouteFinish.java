package pl.slapps.dot.generator.builder;

import java.util.ArrayList;

import pl.slapps.dot.drawing.Junction;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 17.10.15.
 */
public class TileRouteFinish extends TileRoute {

    public String TAG = TileRouteFinish.class.getName();
    private boolean passed = false;


    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route, Generator generator) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route, generator);

    }

    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {

        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route);

    }

    public TileRouteFinish(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, Route.Direction from, Route.Direction to, Generator generator) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, Route.Type.FINISH, generator);

    }

    public void initRoute(Route.Movement d) {
        //super.initRoute(d);


        Route.Direction f = this.to;
        float top = topY;
        float left = topX;
        float right = topX + width;
        float bottom = topY + height;


        switch (f) {

            case LEFT: {

                left = topX + borderX;
                top = topY + borderY;
                right = topX + width;
                bottom = topY + height - borderY;

                if (generator != null) {
                    backgroundPartOne = new TileRouteBackground(centerX + borderX / 2, centerY, width - borderX, routeHeight, generator);
                } else {
                    backgroundPartOne = new TileRouteBackground(centerX + borderX / 2, centerY, width - borderX, routeHeight);

                }

                break;
            }

            case RIGHT: {

                left = topX;
                top = topY + borderY;
                right = topX + width - borderX;
                bottom = topY + height - borderY;

                if (generator != null) {
                    backgroundPartOne = new TileRouteBackground(centerX - borderX / 2, centerY, width - borderX, routeHeight, generator);
                } else {
                    backgroundPartOne = new TileRouteBackground(centerX - borderX / 2, centerY, width - borderX, routeHeight);

                }

                break;
            }
            case TOP: {

                left = topX + borderX;
                top = topY + borderY;
                right = topX + width - borderX;
                bottom = topY + height;

                if (generator != null) {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY + borderY / 2, routeWidth, height-borderY, generator);
                } else {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY + borderY / 2, routeWidth, height-borderY);

                }
                break;
            }
            case BOTTOM: {
                left = topX + borderX;
                top = topY;
                right = topX + width - borderX;
                bottom = topY + height - borderY;
                if (generator != null) {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY - borderY / 2, routeWidth, height-borderY, generator);
                } else {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY - borderY / 2, routeWidth, height-borderY);

                }
                break;
            }
        }

        switch (d) {


            case LEFTRIGHT:
            case RIGHTLEFT: {

                Wall tWall = new Wall(new Junction(left, top, 0),
                        new Junction(right, top, 0), Wall.Type.TOP, generator
                );

                Wall bWall = new Wall(new Junction(left, bottom, 0),
                        new Junction(right, bottom, 0), Wall.Type.BOTTOM, generator);
                walls = new ArrayList<>();

                walls.add(tWall);
                walls.add(bWall);


                break;
            }
            case TOPBOTTOM:
            case BOTTOMTOP: {

                Wall lWall = new Wall(new Junction(left, bottom, 0),
                        new Junction(left, top, 0),
                        Wall.Type.LEFT, generator);

                Wall rWall = new Wall(new Junction(right, bottom, 0), new Junction(right, top, 0), Wall.Type.RIGHT, generator);
                walls = new ArrayList<>();

                walls.add(lWall);
                walls.add(rWall);

            }
        }


        switch (f) {
            case LEFT: {

                Wall lWall = new Wall(new Junction(left, bottom, 0), new Junction(left, top, 0), Wall.Type.LEFT, generator);

                this.walls.add(lWall);


                break;
            }

            case RIGHT: {
                Wall rWall = new Wall(new Junction(right, bottom, 0), new Junction(right, top, 0), Wall.Type.RIGHT, generator);

                walls.add(rWall);
                break;
            }
            case TOP: {
                Wall tWall = new Wall(new Junction(left, top, 0), new Junction(right, top, 0), Wall.Type.TOP, generator);

                walls.add(tWall);
                break;
            }
            case BOTTOM: {
                Wall bWall = new Wall(new Junction(left, bottom, 0), new Junction(right, bottom, 0), Wall.Type.BOTTOM, generator);

                walls.add(bWall);
                break;
            }


        }


    }



}
