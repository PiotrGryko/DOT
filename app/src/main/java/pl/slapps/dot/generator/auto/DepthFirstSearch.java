package pl.slapps.dot.generator.auto;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 01/04/16.
 */
public class DepthFirstSearch {
    private Generator generator;
    private Random random;
    public boolean isRunning;

    public DepthFirstSearch(Generator generator) {
        this.generator = generator;
        this.random = new Random();
    }

    private Route.Direction randomDirection(TileRoute tileRoute, ArrayList<Route.Direction> usedOptions) {
        Route.Direction from = tileRoute.from;
        ArrayList<Route.Direction> options = new ArrayList<>();

        if (from != Route.Direction.BOTTOM && !usedOptions.contains(Route.Direction.BOTTOM)) {
            options.add(Route.Direction.BOTTOM);
        }
        if (from != Route.Direction.TOP && !usedOptions.contains(Route.Direction.TOP)) {
            options.add(Route.Direction.TOP);
        }
        if (from != Route.Direction.LEFT && !usedOptions.contains(Route.Direction.LEFT)) {
            options.add(Route.Direction.LEFT);
        }
        if (from != Route.Direction.RIGHT && !usedOptions.contains(Route.Direction.RIGHT)) {
            options.add(Route.Direction.RIGHT);
        }

        if (options.size() == 0)
            return null;


        int index = random.nextInt(options.size());


        return options.get(index);


    }

    private int hasNeighboor(TileRoute randomTile, Route.Direction nextDirection) {
        int count = 0;
        TileRoute tileTop = generator.getTileRouteManager().getNeighbour(randomTile, Route.Direction.TOP);
        TileRoute tileBottom = generator.getTileRouteManager().getNeighbour(randomTile, Route.Direction.BOTTOM);
        TileRoute tileLeft = generator.getTileRouteManager().getNeighbour(randomTile, Route.Direction.LEFT);
        TileRoute tileRight = generator.getTileRouteManager().getNeighbour(randomTile, Route.Direction.RIGHT);

        if (tileTop != null && tileTop.type == Route.Type.TILE && generator.getTileRouteManager().getOposite(nextDirection) != Route.Direction.TOP) {
            count++;

        } else if (tileBottom != null && tileBottom.type == Route.Type.TILE && generator.getTileRouteManager().getOposite(nextDirection) != Route.Direction.BOTTOM) {
            count++;

        } else if (tileLeft != null && tileLeft.type == Route.Type.TILE && generator.getTileRouteManager().getOposite(nextDirection) != Route.Direction.LEFT) {
            count++;

        } else if (tileRight != null && tileRight.type == Route.Type.TILE && generator.getTileRouteManager().getOposite(nextDirection) != Route.Direction.RIGHT) {
            count++;

        }

        return count;
    }

    private TileRoute startRoute = null;

    private TileRoute nextStep(TileRoute tileRoute) {


        // int side = random.nextInt(4);


        ArrayList<Route.Direction> usedOptions = new ArrayList<>();
        TileRoute nextTile = null;

        Route.Direction nextDirection = randomDirection(tileRoute, usedOptions);

        nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, nextDirection);
        if (nextTile != null && nextTile.visited)
            nextTile = null;


        while (nextDirection != null && (nextTile == null || nextTile.type != Route.Type.TILE)) {
            usedOptions.add(nextDirection);
            nextDirection = randomDirection(tileRoute, usedOptions);
            if (nextDirection != null) {
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, nextDirection);
                if (nextTile != null && nextTile.visited)
                    nextTile = null;
            }

        }


        if (nextTile != null && nextTile.type == Route.Type.TILE) {
            tileRoute.to = generator.getTileRouteManager().getTo(tileRoute, nextTile);
            nextTile.from = generator.getTileRouteManager().getOposite(tileRoute.to);

            if (generator.getStartRoute() == null) {

                startRoute = generator.getTileRouteManager().getRouteStartFromTile(generator.getTileRouteManager().getOposite(tileRoute.to), tileRoute.to, tileRoute);

                int index = generator.tiles.indexOf(tileRoute);
                startRoute.visited = true;
                generator.tiles.set(index, startRoute);
            } else {
                startRoute = generator.getTileRouteManager().getRouteFromTile(tileRoute.from, tileRoute.to, tileRoute);

                int index = generator.tiles.indexOf(tileRoute);
                startRoute.visited = true;
                generator.tiles.set(index, startRoute);
            }
            Log.d("rrr", "step generated ... " + nextTile.horizontalPos + " " + nextTile.verticalPos);

            return nextTile;
        }
        return null;
    }

    public boolean allTilesVisited() {


        for (int i = 0; i < generator.tiles.size(); i++) {
            if (!generator.tiles.get(i).visited) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldstop(float percent) {


        float count = 0;
        for (int i = 0; i < generator.tiles.size(); i++) {
            if (!generator.tiles.get(i).visited) {
                count++;
            }
        }
        if(count==0)
            return true;

        float left = count/generator.tiles.size();

        if (left<=1-percent)
            return true;
        else
            return false;
    }

    public void generateMaze(final int x, final int y, final float percent, final Handler.Callback callback, final boolean sleep) {


        new Thread() {
            public void run() {

                isRunning=true;
                generator.initGrid(x, y);


                int startX = random.nextInt(x);
                int startY = random.nextInt(y);

                startRoute = generator.findTile(startX, startY);


                while (!allTilesVisited()) {
                    TileRoute next = nextStep(startRoute);

                    if (next == null) {
                        if(shouldstop(percent))
                            break;
                        Log.d("ggg", "getting back " + startRoute.from + " " + startRoute.to + " " + startRoute.type);

                        TileRoute previousTile = generator.getTileRouteManager().getPreviousTileForRoute(startRoute);

                        TileRoute tileRoute = generator.getTileRouteManager().getTileFromRoute(startRoute);
                        tileRoute.visited = true;
                        int index = generator.tiles.indexOf(startRoute);
                        generator.tiles.set(index, tileRoute);
                        startRoute = previousTile;
                        Log.d("ggg", "getting back " + previousTile);



                    } else {
                        startRoute = next;
                        Log.d("ggg", "getting forward " + next);

                    }
                    if(sleep) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                }

                /*
                if(startRoute.type== Route.Type.ROUTE) {
                    TileRoute endRoute = generator.getTileRouteManager().getNextTileForRoute(startRoute);
                    endRoute.from = generator.getTileRouteManager().getOposite(startRoute.to);
                    endRoute.to = generator.getTileRouteManager().getOposite(endRoute.from);

                    TileRoute finisRoute = generator.getTileRouteManager().getRouteFinishFromTile(startRoute.from, generator.getTileRouteManager().getOposite(startRoute.from), endRoute);
                    int index = generator.tiles.indexOf(endRoute);
                    generator.tiles.set(index, finisRoute);
                }
                else
                {
                  */
                    TileRoute finisRoute = generator.getTileRouteManager().getRouteFinishFromTile(startRoute.from, generator.getTileRouteManager().getOposite(startRoute.from), startRoute);
                    int index = generator.tiles.indexOf(startRoute);
                    generator.tiles.set(index, finisRoute);
                //}

                Log.d("rrr", "generation finished... " + generator.getStartRoute() + " " + generator.getFinishRoute());
                callback.handleMessage(null);
                isRunning=false;
/*

        while (startRoute != null) {
            TileRoute next = nextStep(startRoute);

            if (next == null) {
                TileRoute endRoute = generator.getTileRouteManager().getRouteFinishFromTile(startRoute.from, generator.getTileRouteManager().getOposite(startRoute.from), startRoute);

                int index = generator.tiles.indexOf(startRoute);
                generator.tiles.set(index, endRoute);
                startRoute = null;

                Log.d("rrr", "generation finished... " + generator.getStartRoute() + " " + generator.getFinishRoute());
            } else
                startRoute = next;
        }
*/
            }
        }.start();

    }


}
