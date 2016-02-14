package pl.slapps.dot.generator;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import pl.slapps.dot.drawing.Junction;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 13.10.15.
 */
public class TileRoute {


    private TileRouteBackground backgroundPartOne;
    private TileRouteBackground backgroundPartTwo;


    public float topX;
    public float topY;
    public float width;
    public float height;
    public float routeWidth;
    public float routeHeight;

    public float centerX;
    public float centerY;
    float borderX;
    float borderY;

    public Route.Direction from;
    public Route.Direction to;

    public Route.Movement next;


    private boolean isInitialized;
    public int horizontalPos;
    public int verticalPos;
    public ArrayList<Wall> walls;
    public Route.Type type;
    public String backgroundColor;
    public String sound;

    public double speedRatio = 1;
    public Generator generator;
    //public SurfaceRenderer generator;


    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to, Route.Type t, Generator generator) {
        this.generator = generator;
        initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, t);

    }


    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route, Generator generator) {

        this.generator = generator;
        this.next = Route.Movement.valueOf(route.next.name());
        backgroundColor = route.backgroundColor;
        sound = route.sound;
        speedRatio = route.speedRatio;


        initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route.x, route.y, route.from.name(), route.to.name(), Route.Type.valueOf(route.type.name()));


    }

    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {

        this.next = Route.Movement.valueOf(route.next.name());
        backgroundColor = route.backgroundColor;
        sound = route.sound;
        speedRatio = route.speedRatio;


        initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route.x, route.y, route.from.name(), route.to.name(), Route.Type.valueOf(route.type.name()));


    }


    public void initRoute(Route.Movement d) {

        if (type == Route.Type.FILL) {


            if(generator!=null)
            backgroundPartOne = new TileRouteBackground(centerX, centerY, width, height,  generator);
            else
                backgroundPartOne = new TileRouteBackground(centerX, centerY, width, height);


            return;
        }
        if (type == Route.Type.TILE || type == Route.Type.BLOCK) {
            borderX = 0;
            borderY = 0;
            walls.add(new Wall(new Junction(topX + borderX, topY + height - borderY, 0), new Junction(topX + borderX, topY + borderY, 0), Wall.Type.LEFT, generator));
            walls.add(new Wall(new Junction(topX + borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY + borderY, 0), Wall.Type.TOP, generator));
            walls.add(new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY + height - borderY, 0), Wall.Type.RIGHT, generator));
            walls.add(new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0), new Junction(topX + borderX, topY + height - borderY, 0), Wall.Type.BOTTOM, generator));

            //backgroundPartOne = new TileRouteBackground(centerX, centerY, routeWidth, routeHeight, backgroundColor,generator);

            return;
        }

        switch (d) {

            case BOTTOMRIGHT:
            case RIGHTBOTTOM: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY + borderY, 0),
                        Wall.Type.LEFT, generator);


                Wall tWall = new Wall(new Junction(topX + borderX, topY + borderY, 0),
                        new Junction(topX + width, topY + borderY, 0),
                        Wall.Type.TOP, generator);


                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0),
                        new Junction(topX + width - borderX, topY + height - borderY, 0),
                        Wall.Type.RIGHT, generator);


                Wall bWall = new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0),
                        new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM, generator);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY, generator);
                    backgroundPartTwo = new TileRouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeHeight, generator);
                }
                else
                {

                    backgroundPartOne = new TileRouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY);
                    backgroundPartTwo = new TileRouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeHeight);
                }
                break;
            }
            case BOTTOMLEFT:
            case LEFTBOTTOM: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY + height - borderY, 0),
                        Wall.Type.LEFT, generator);


                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX + borderX, topY + height - borderY, 0),
                        Wall.Type.BOTTOM, generator);

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0),
                        new Junction(topX + width - borderX, topY + borderY, 0),

                        Wall.Type.TOP, generator);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0), new Junction(topX + width - borderX, topY + borderY, 0),


                        Wall.Type.RIGHT, generator);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY, generator);
                    backgroundPartTwo = new TileRouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeHeight, generator);
                }
                else
                {

                    backgroundPartOne = new TileRouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY);
                    backgroundPartTwo = new TileRouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeHeight);
                }
                break;
            }

            case RIGHTTOP:
            case TOPRIGHT: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height - borderY, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT, generator);


                Wall bWall = new Wall(new Junction(topX + borderX, topY + height - borderY, 0), new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM, generator);

                Wall tWall = new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width, topY + borderY, 0), Wall.Type.TOP, generator);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT, generator);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, topY + borderY / 2, routeWidth, borderY, generator);
                    backgroundPartTwo = new TileRouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeHeight, generator);
                }
                else
                {

                    backgroundPartOne = new TileRouteBackground(centerX, topY + borderY / 2, routeWidth, borderY);
                    backgroundPartTwo = new TileRouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeHeight);
                }
                break;
            }

            case LEFTTOP:
            case TOPLEFT: {

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0), new Junction(topX + borderX, topY + borderY, 0), Wall.Type.TOP, generator);

                Wall lWall = new Wall(new Junction(topX + borderX, topY + borderY, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT, generator);

                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX + width - borderX, topY + height - borderY, 0), Wall.Type.BOTTOM, generator);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT, generator);

                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, topY + borderY / 2, routeWidth, borderY, generator);
                    backgroundPartTwo = new TileRouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeHeight, generator);
                }
                else
                {

                    backgroundPartOne = new TileRouteBackground(centerX, topY + borderY / 2, routeWidth, borderY);
                    backgroundPartTwo = new TileRouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeHeight);
                }
                break;
            }

            case LEFTRIGHT:
            case RIGHTLEFT: {

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0),
                        new Junction(topX + width, topY + borderY, 0), Wall.Type.TOP, generator
                );

                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0),
                        new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM, generator);

                walls.add(tWall);
                walls.add(bWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY, width, routeHeight, generator);
                }
                else
                {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY, width, routeHeight);

                }

                break;
            }
            case TOPBOTTOM:
            case BOTTOMTOP: {

                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT, generator);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT, generator);

                walls.add(lWall);
                walls.add(rWall);
                if(generator!=null) {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY, routeWidth, height, generator);
                }
                else
                {
                    backgroundPartOne = new TileRouteBackground(centerX, centerY, routeWidth, height);

                }
            }

        }


    }


    private void initData(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to, Route.Type t) {
        this.type = t;
        width = screenWidth / widthBlocksCount;
        height = screenHeight / heightBlocksCount;
        topX = width * (widthNumber);
        topY = height * heightNumber;
        this.routeWidth = width * 9 / 10;
        this.routeHeight = height * 9 / 10;

        this.borderX = (width - routeWidth) / 2;
        this.borderY = (height - routeHeight) / 2;
        this.horizontalPos = widthNumber;
        this.verticalPos = heightNumber;
        this.from = Route.Direction.valueOf(from);
        this.to = Route.Direction.valueOf(to);
        this.walls = new ArrayList<>();
        this.centerX = topX + width / 2;
        this.centerY = topY + height / 2;


        initRoute(getDirection());
        isInitialized = true;
    }


    public void configure(Config config)
    {
        if (backgroundPartOne != null)
            backgroundPartOne.configure(config);
        if (backgroundPartTwo != null)
            backgroundPartTwo.configure(config);

        for(int i=0;i<walls.size();i++)
        {
            walls.get(i).configure(config);
        }
    }


    public void drawGL20(float[] mvpMatrix) {


        if (!isInitialized)
            return;


        if (backgroundPartOne != null)
            backgroundPartOne.drawGl2(mvpMatrix);
        if (backgroundPartTwo != null)
            backgroundPartTwo.drawGl2(mvpMatrix);


        for (int i = 0; i < walls.size(); i++) {
            walls.get(i).drawGl2(mvpMatrix);
        }



    }


    public boolean contains(float x, float y) {



        if (x >= topX && x <= topX + width
                && y >= topY && y <= topY + height) {


            return true;
        }


        return false;
    }

    public Wall.Type checkCollision(float x, float y, float width) {

        //  Log.d(TAG, "contains " + contains(x, y));
        if (!contains(x, y))
            return null;
        for (int i = 0; i < walls.size(); i++) {
            Wall.Type result = walls.get(i).checkCollision(x, y, width);
            if (result != null)
                return result;
        }


        return null;
    }

    public Route.Type getType() {
        return type;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public ArrayList<TileRouteBackground> getBackgrounds() {
        ArrayList<TileRouteBackground> routeBackgrounds = new ArrayList<>();
        if (backgroundPartOne != null)
            routeBackgrounds.add(backgroundPartOne);
        if (backgroundPartTwo != null)
            routeBackgrounds.add(backgroundPartTwo);

        return routeBackgrounds;
    }

    public Route.Movement getDirection() {


        if (from == Route.Direction.TOP) {
            if (to == Route.Direction.LEFT)
                return Route.Movement.TOPLEFT;
            if (to == Route.Direction.RIGHT)
                return Route.Movement.TOPRIGHT;
            if (to == Route.Direction.BOTTOM)
                return Route.Movement.TOPBOTTOM;
        }
        if (from == Route.Direction.BOTTOM) {
            if (to == Route.Direction.LEFT)
                return Route.Movement.BOTTOMLEFT;
            if (to == Route.Direction.RIGHT)
                return Route.Movement.BOTTOMRIGHT;
            if (to == Route.Direction.TOP)
                return Route.Movement.BOTTOMTOP;
        }
        if (from == Route.Direction.LEFT) {
            if (to == Route.Direction.TOP)
                return Route.Movement.LEFTTOP;
            if (to == Route.Direction.BOTTOM)
                return Route.Movement.LEFTBOTTOM;
            if (to == Route.Direction.RIGHT)
                return Route.Movement.LEFTRIGHT;
        }
        if (from == Route.Direction.RIGHT) {
            if (to == Route.Direction.TOP)
                return Route.Movement.RIGHTTOP;
            if (to == Route.Direction.BOTTOM)
                return Route.Movement.RIGHTBOTTOM;
            if (to == Route.Direction.LEFT)
                return Route.Movement.RIGHTLEFT;
        }
        return null;
    }
}
