package pl.slapps.dot.generator.builder;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 06/03/16.
 */
public class PathBuilderPopup {


    private Generator generator;

    private String TAG = PathBuilderPopup.class.getName();

    public PathBuilderPopup(Generator generator) {
        this.generator = generator;
    }


    public void startRouteConfiguration() {
        ArrayList<TileRoute> routes = new ArrayList<>();
        TileRoute startRoute = null;
        TileRoute nextRoute = null;
        Route.Direction from = null;
        for (int i = 0; i < generator.tiles.size(); i++) {
            TileRoute t = generator.tiles.get(i);
            if (t.getType() == Route.Type.START) {
                startRoute = t;
                break;
            }
        }

        if (startRoute != null) {
            Route.Movement movement = startRoute.getDirection();
            switch (movement) {
                case LEFTRIGHT:
                    from = Route.Direction.LEFT;
                    nextRoute = generator.findTile(startRoute.horizontalPos + 1, startRoute.verticalPos);
                    break;

                case RIGHTLEFT:
                    from = Route.Direction.RIGHT;
                    nextRoute = generator.findTile(startRoute.horizontalPos - 1, startRoute.verticalPos);
                    break;
                case BOTTOMTOP:
                    from = Route.Direction.BOTTOM;
                    nextRoute = generator.findTile(startRoute.horizontalPos, startRoute.verticalPos - 1);
                    break;
                case TOPBOTTOM:
                    from = Route.Direction.TOP;
                    nextRoute = generator.findTile(startRoute.horizontalPos, startRoute.verticalPos + 1);
                    break;


            }
            Log.d(TAG, "config started ");
            Log.d(TAG, "route configured from:" + startRoute.from.name() + " to: " + startRoute.to.name() + " movement: " + startRoute.getDirection().name() + " type: " + startRoute.getType().name());


            configRoute(nextRoute, from, routes);


            Log.d(TAG, "config end  " + routes.size());

            for (int i = 0; i < routes.size(); i++) {
                routes.get(i).next = getNextMove(i, routes);

            }

            Log.d(TAG, "next moves set   " + routes.size());

            for (int i = 0; i < routes.size(); i++) {
                Log.d(TAG, "next move : " + i + " " + routes.get(i).next + " " + routes.get(i).from + " " + routes.get(i).to + " " + routes.get(i).type);
            }


        }


    }

    private void configRoute(TileRoute route, Route.Direction target_from, ArrayList<TileRoute> routes) {
        //route.from=from;


        Route.Movement t = route.getDirection();
        Route.Direction target_to = route.to;
        int targetX = route.horizontalPos;
        int targetY = route.verticalPos;

        Route.Direction nextFrom = target_from;
        Log.d(TAG, "config route from: " + target_from + " movement  " + t + " " + route.from + "  " + route.to + " type " + route.type);

        switch (target_from) {
            case LEFT:

                switch (t) {
                    case RIGHTLEFT:
                    case LEFTRIGHT:
                        target_to = Route.Direction.RIGHT;
                        nextFrom = Route.Direction.LEFT;
                        targetX++;
                        break;
                    case BOTTOMLEFT:
                    case LEFTBOTTOM:
                        target_to = Route.Direction.BOTTOM;
                        nextFrom = Route.Direction.TOP;
                        targetY++;
                        break;
                    case TOPLEFT:
                    case LEFTTOP:
                        targetY--;
                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        break;

                }
                break;
            case RIGHT:
                switch (t) {
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        targetX--;

                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        break;
                    case BOTTOMRIGHT:
                    case RIGHTBOTTOM:
                        targetY++;

                        nextFrom = Route.Direction.TOP;
                        target_to = Route.Direction.BOTTOM;
                        break;
                    case TOPRIGHT:
                    case RIGHTTOP:
                        targetY--;

                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        break;

                }
                break;
            case TOP:

                switch (t) {
                    case BOTTOMTOP:
                    case TOPBOTTOM:
                        targetY++;
                        nextFrom = Route.Direction.TOP;
                        target_to = Route.Direction.BOTTOM;
                        break;
                    case LEFTTOP:
                    case TOPLEFT:
                        targetX--;
                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        break;
                    case RIGHTTOP:
                    case TOPRIGHT:
                        targetX++;
                        nextFrom = Route.Direction.LEFT;
                        target_to = Route.Direction.RIGHT;
                        break;

                }
                break;
            case BOTTOM:
                switch (t) {
                    case TOPBOTTOM:
                    case BOTTOMTOP:
                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        targetY--;

                        break;
                    case LEFTBOTTOM:
                    case BOTTOMLEFT:
                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        targetX--;

                        break;
                    case RIGHTBOTTOM:
                    case BOTTOMRIGHT:
                        nextFrom = Route.Direction.LEFT;
                        target_to = Route.Direction.RIGHT;
                        targetX++;

                        break;

                }
                break;


        }
        route.from = target_from;
        route.to = target_to;
        routes.add(route);
        Log.d(TAG, "route configured from:" + target_from + " to: " + target_to + " movement: " + route.getDirection().name() + " type: " + route.getType().name());

        TileRoute nextRoute = generator.findTile(targetX, targetY);

        if (route.getType() == Route.Type.FINISH)
            return;

        if (nextRoute != null && nextRoute.getType() != Route.Type.TILE && nextRoute != route) {
            configRoute(nextRoute, nextFrom, routes);
        }


    }

    public ArrayList<TileRoute> getPath() {
        ArrayList<TileRoute> path = new ArrayList<>();
        TileRoute startRoute = generator.getStartRoute();


        while (startRoute != null) {
            if (startRoute.getType() == Route.Type.TILE)
                break;
            path.add(startRoute);
            if (startRoute.getType() == Route.Type.FINISH)
                break;
            Route.Movement movement = startRoute.getDirection();
            switch (movement) {
                case LEFTRIGHT:
                case TOPRIGHT:
                case BOTTOMRIGHT:
                    startRoute = generator.findTile(startRoute.horizontalPos + 1, startRoute.verticalPos);
                    break;

                case RIGHTLEFT:
                case TOPLEFT:
                case BOTTOMLEFT:
                    startRoute = generator.findTile(startRoute.horizontalPos - 1, startRoute.verticalPos);
                    break;
                case BOTTOMTOP:
                case RIGHTTOP:
                case LEFTTOP:
                    startRoute = generator.findTile(startRoute.horizontalPos, startRoute.verticalPos - 1);
                    break;
                case TOPBOTTOM:
                case LEFTBOTTOM:
                case RIGHTBOTTOM:
                    startRoute = generator.findTile(startRoute.horizontalPos, startRoute.verticalPos + 1);
                    break;
                default:
                    startRoute = null;


            }
        }
        for (int i = 0; i < generator.tiles.size(); i++) {
            if (generator.tiles.get(i).getType() == Route.Type.ROUTE || generator.tiles.get(i).getType() == Route.Type.FINISH || generator.tiles.get(i).getType() == Route.Type.START) {
                if (!path.contains(generator.tiles.get(i)))
                    path.add(generator.tiles.get(i));
            }
        }

        return path;
    }


    public Route.Movement getNextMove(int start, ArrayList<TileRoute> routes) {


        for (int i = start; i < routes.size(); i++) {

            Route.Movement type = routes.get(i).getDirection();
            if (type != Route.Movement.LEFTRIGHT && type != Route.Movement.RIGHTLEFT && type != Route.Movement.BOTTOMTOP && type != Route.Movement.TOPBOTTOM)
                return type;
            else {
                continue;
            }

        }
        return null;
    }

    public boolean onTouch(MotionEvent event) {


        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                for (int i = 0; i < generator.tiles.size(); i++) {
                    TileRoute t = generator.tiles.get(i);
                    if (t.contains(x, y)) {

                        //tmp
                        if (t.type == Route.Type.FINISH) {
                            generator.getLayout().setCurrentTile(t);
                            return true;
                        }
                        if (t.type != Route.Type.ROUTE)
                            return true;

                        generator.getLayout().setCurrentTile(t);

                        generator.getPathPopup().showPath(event.getX(), event.getY());


                        return true;
                    }
                }
                break;
            }

        }


        return true;
    }


}
