package pl.slapps.dot.route;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.model.Junction;
import pl.slapps.dot.model.Wall;

/**
 * Created by piotr on 13.10.15.
 */
public class Route {

    public enum Movement {
        BOTTOMRIGHT, BOTTOMLEFT, RIGHTTOP, RIGHTBOTTOM, LEFTBOTTOM, LEFTTOP, TOPLEFT, TOPRIGHT, LEFTRIGHT, RIGHTLEFT, TOPBOTTOM, BOTTOMTOP;
    }

    public enum Direction {
        LEFT, TOP, RIGHT, BOTTOM;
    }

    public enum Type {
        FINISH, START, ROUTE, TILE, BLOCK, FILL;
    }

    private RouteBackground backgroundPartOne;
    private RouteBackground backgroundPartTwo;


    public float topX;
    public float topY;
    public float width;
    public float height;
    public float routeWidth;
    public float centerX;
    public float centerY;
    float borderX;
    float borderY;

    public Direction from;
    public Direction to;

    public Movement next;


    private boolean isInitialized;
    public int horizontalPos;
    public int verticalPos;
    public ArrayList<Wall> walls;
    public Type type;
    public String backgroundColor;


    public void initRoute(Movement d) {

        if (type == Type.FILL) {


            backgroundPartOne = new RouteBackground(centerX, centerY, width, height, backgroundColor);

            return;
        }
        if (type == Type.TILE || type == Type.BLOCK) {
            walls.add(new Wall(new Junction(topX + borderX, topY + height - borderY, 0), new Junction(topX + borderX, topY + borderY, 0), Wall.Type.LEFT));
            walls.add(new Wall(new Junction(topX + borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY + borderY, 0), Wall.Type.TOP));
            walls.add(new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY + height - borderY, 0), Wall.Type.RIGHT));
            walls.add(new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0), new Junction(topX + borderX, topY + height - borderY, 0), Wall.Type.BOTTOM));


            backgroundPartOne = new RouteBackground(centerX, centerY, routeWidth, routeWidth, backgroundColor);

            return;
        }

        switch (d) {
            case BOTTOMRIGHT:
            case RIGHTBOTTOM: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY + borderY, 0),
                        Wall.Type.LEFT);


                Wall tWall = new Wall(new Junction(topX + borderX, topY + borderY, 0),
                        new Junction(topX + width, topY + borderY, 0),
                        Wall.Type.TOP);


                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0),
                        new Junction(topX + width - borderX, topY + height - borderY, 0),
                        Wall.Type.RIGHT);


                Wall bWall = new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0),
                        new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);

                backgroundPartOne = new RouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY, backgroundColor);
                backgroundPartTwo = new RouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeWidth, backgroundColor);

                //  backgroundPartOne=new RouteBackground(centerX,centerY,(int)width,(int)(height-2*borderY));
                break;
            }
            case BOTTOMLEFT:
            case LEFTBOTTOM: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY + height - borderY, 0),
                        Wall.Type.LEFT);


                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX + borderX, topY + height - borderY, 0),
                        Wall.Type.BOTTOM);

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0),
                        new Junction(topX + width - borderX, topY + borderY, 0),

                        Wall.Type.TOP);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0), new Junction(topX + width - borderX, topY + borderY, 0),


                        Wall.Type.RIGHT);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);

                backgroundPartOne = new RouteBackground(centerX, topY + height - borderY / 2, routeWidth, borderY, backgroundColor);
                backgroundPartTwo = new RouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeWidth, backgroundColor);

                break;
            }

            case RIGHTTOP:
            case TOPRIGHT: {
                Wall lWall = new Wall(new Junction(topX + borderX, topY + height - borderY, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT);


                Wall bWall = new Wall(new Junction(topX + borderX, topY + height - borderY, 0), new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM);

                Wall tWall = new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width, topY + borderY, 0), Wall.Type.TOP);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + borderY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT);


                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);

                backgroundPartOne = new RouteBackground(centerX, topY + borderY / 2, routeWidth, borderY, backgroundColor);
                backgroundPartTwo = new RouteBackground(topX + borderX + (width - borderX) / 2, centerY, width - borderX, routeWidth, backgroundColor);

                break;
            }

            case LEFTTOP:
            case TOPLEFT: {

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0), new Junction(topX + borderX, topY + borderY, 0), Wall.Type.TOP);

                Wall lWall = new Wall(new Junction(topX + borderX, topY + borderY, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT);

                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0), new Junction(topX + width - borderX, topY + height - borderY, 0), Wall.Type.BOTTOM);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height - borderY, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT);

                walls = new ArrayList<>();
                walls.add(lWall);
                walls.add(tWall);
                walls.add(rWall);
                walls.add(bWall);

                backgroundPartOne = new RouteBackground(centerX, topY + borderY / 2, routeWidth, borderY, backgroundColor);
                backgroundPartTwo = new RouteBackground(topX + (width - borderX) / 2, centerY, width - borderX, routeWidth, backgroundColor);

                break;
            }

            case LEFTRIGHT:
            case RIGHTLEFT: {

                Wall tWall = new Wall(new Junction(topX, topY + borderY, 0),
                        new Junction(topX + width, topY + borderY, 0), Wall.Type.TOP
                );

                Wall bWall = new Wall(new Junction(topX, topY + height - borderY, 0),
                        new Junction(topX + width, topY + height - borderY, 0), Wall.Type.BOTTOM);

                walls.add(tWall);
                walls.add(bWall);

                backgroundPartOne = new RouteBackground(centerX, centerY, (int) width, (int) routeWidth, backgroundColor);


                break;
            }
            case TOPBOTTOM:
            case BOTTOMTOP: {

                Wall lWall = new Wall(new Junction(topX + borderX, topY + height, 0),
                        new Junction(topX + borderX, topY, 0),
                        Wall.Type.LEFT);

                Wall rWall = new Wall(new Junction(topX + width - borderX, topY + height, 0), new Junction(topX + width - borderX, topY, 0), Wall.Type.RIGHT);

                walls.add(lWall);
                walls.add(rWall);

                backgroundPartOne = new RouteBackground(centerX, centerY, routeWidth, height, backgroundColor);

            }

        }


    }






    private void initData(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to, Type t) {
        this.type = t;
        width = screenWidth / widthBlocksCount;
        height = screenHeight / heightBlocksCount;
        topX = width * (widthNumber);
        topY = height * heightNumber;
        this.routeWidth = width * 9 / 10;
        this.borderX = (width - routeWidth) / 2;
        this.borderY = (height - routeWidth) / 2;
        this.horizontalPos = widthNumber;
        this.verticalPos = heightNumber;
        this.from = Direction.valueOf(from);
        this.to = Direction.valueOf(to);
        this.walls = new ArrayList<>();
        this.centerX = topX + width / 2;
        this.centerY = topY + height / 2;


        initRoute(getDirection());
        isInitialized = true;
    }

    public Route(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to, Type t) {

        initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, t);

    }

    public Route(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, String from, String to, Type t,String color) {

        initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, t);
        this.backgroundColor=color;

    }

    public Route(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, JSONObject element, String color) {

        int widthNumber = 0;
        try {
            widthNumber = element.has("x") ? element.getInt("x") : 0;

            int heightNumber = element.has("y") ? element.getInt("y") : 0;
            String from = element.has("from") ? element.getString("from") : "";
            String to = element.has("to") ? element.getString("to") : "";
            String next = element.has("next") ? element.getString("next") : null;
            if(next!=null)
            this.next=Movement.valueOf(next);
            String type = element.has("type") ? element.getString("type") : "ROUTE";
            JSONObject colors = element.has("colors")?element.getJSONObject("colors"):new JSONObject();
            backgroundColor =  element.has("background_color") ? element.getString("background_color") : color;
            Route.Type t = Route.Type.valueOf(type);

            initData(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, t);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setRouteColor(String color) {
        if (backgroundPartOne != null)
            backgroundPartOne.setColor(color);
        if (backgroundPartTwo != null)
            backgroundPartTwo.setColor(color);
    }

    public void draw(GL10 gl) {


        if (!isInitialized)
            return;
        gl.glLoadIdentity();



        if (backgroundPartOne != null)
            backgroundPartOne.draw(gl);
        if (backgroundPartTwo != null)
            backgroundPartTwo.draw(gl);



        for (int i = 0; i < walls.size(); i++) {
            walls.get(i).draw(gl);
        }


    }


    public boolean contains(float x, float y) {
        if (x >= topX && x <= topX + width
                && y >= topY && y <= topY + height)
            return true;

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

    public Type getType() {
        return type;
    }

    public ArrayList<Wall> getWalls()
    {
        return walls;
    }

    public ArrayList<RouteBackground> getBackgrounds()
    {
        ArrayList<RouteBackground> routeBackgrounds = new ArrayList<>();
        if(backgroundPartOne!=null)
            routeBackgrounds.add(backgroundPartOne);
        if(backgroundPartTwo!=null)
            routeBackgrounds.add(backgroundPartTwo);

        return routeBackgrounds;
    }

    public Movement getDirection() {


        if (from == Direction.TOP) {
            if (to == Direction.LEFT)
                return Movement.TOPLEFT;
            if (to == Direction.RIGHT)
                return Movement.TOPRIGHT;
            if (to == Direction.BOTTOM)
                return Movement.TOPBOTTOM;
        }
        if (from == Direction.BOTTOM) {
            if (to == Direction.LEFT)
                return Movement.BOTTOMLEFT;
            if (to == Direction.RIGHT)
                return Movement.BOTTOMRIGHT;
            if (to == Direction.TOP)
                return Movement.BOTTOMTOP;
        }
        if (from == Direction.LEFT) {
            if (to == Direction.TOP)
                return Movement.LEFTTOP;
            if (to == Direction.BOTTOM)
                return Movement.LEFTBOTTOM;
            if (to == Direction.RIGHT)
                return Movement.LEFTRIGHT;
        }
        if (from == Direction.RIGHT) {
            if (to == Direction.TOP)
                return Movement.RIGHTTOP;
            if (to == Direction.BOTTOM)
                return Movement.RIGHTBOTTOM;
            if (to == Direction.LEFT)
                return Movement.RIGHTLEFT;
        }
        return null;
    }
}
