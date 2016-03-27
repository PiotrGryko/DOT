package pl.slapps.dot.generator.builder;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import pl.slapps.dot.drawing.Junction;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 06/03/16.
 */
public class PathBuilderDraw {

    private Generator generator;
    FingerTracker fingerTracker;

    private ArrayList<TileRoute> routes;


    public PathBuilderDraw(Generator generator) {
        this.generator = generator;
        fingerTracker = new FingerTracker();
    }


    public boolean onTouch(MotionEvent event) {


        TileRoute result = fingerTracker.trackFinger(event);


        return true;

    }

    class FingerTracker {


        TileRoute lastTile;
        private boolean isDraging;

        public void setCurrentTile(TileRoute tile) {

            if (this.lastTile != null) {
                this.lastTile.setCurrentTile(false);
                this.lastTile.setCurrentHeadTile(false);
            }
            this.lastTile = tile;
            this.lastTile.setCurrentTile(true);
            this.lastTile.setCurrentHeadTile(true);

            generator.getLayout().setCurrentTile(lastTile);
        }

        private void finishDraging(float x, float y) {


            TileRoute finalTile = generator.findTileByCoords(x, y);

            if (finalTile.type == Route.Type.START) {
                TileRoute blankTile = generator.getTileRouteManager().getTileFromRoute(finalTile);
                //generator.tiles.remove(finalTile);
                //generator.tiles.add(blankTile);
                int index = generator.tiles.indexOf(finalTile);
                generator.tiles.set(index,blankTile);
                setCurrentTile(blankTile);
            }

            isDraging = false;
        }

        private TileRoute drag(float x, float y) {


            //newTile - tile in front
            //lasttile - end of the current maze
            TileRoute newTile = generator.findTileByCoords(x, y);
            if (lastTile != newTile
                    ) {

                if (lastTile != null) {

                    TileRoute previousTile = generator.getTileRouteManager().getPreviousTileForRoute(lastTile);

                    if (newTile == previousTile) {
                        TileRoute blankTile = generator.getTileRouteManager().getTileFromRoute(lastTile);
                        //generator.tiles.remove(lastTile);
                        //generator.tiles.add(blankTile);

                        int index = generator.tiles.indexOf(lastTile);
                        generator.tiles.set(index, blankTile);

                        setCurrentTile(previousTile);
                        if (previousTile.type != Route.Type.START) {
                            TileRoute newLastTile = generator.getTileRouteManager().getRouteFinishFromTile(previousTile.from, previousTile.to, previousTile);
                            //generator.tiles.remove(previousTile);

                            index = generator.tiles.indexOf(previousTile);
                            generator.tiles.set(index, newLastTile);

                            //generator.tiles.add(newLastTile);
                            setCurrentTile(newLastTile);
                        }
                        Log.d("hhh", "prevoious " + previousTile.type);
                        return previousTile;
                    }
                }


                if (newTile.type != Route.Type.TILE)

                {
                    isDraging = false;
                    //finishDraging(x, y);
                    Log.d("hhh", "draging finished");
                    return null;
                }


                //we know new tile from
                //we know old tile to
                Log.d("hhh", "draging coninued...");


                TileRoute r = null;
                if (generator.getStartRoute() == null && lastTile != null) {

                    lastTile.to = generator.getTileRouteManager().getTo(lastTile, newTile);
                    lastTile.from = generator.getTileRouteManager().getOposite(lastTile.to);

                    Log.d("www", "current tile as a start route " + lastTile.from + " " + lastTile.to);

                    r = generator.getTileRouteManager().getRouteStartFromTile(lastTile.from, lastTile.to, lastTile);
                    if (r != null) {
                        int index = generator.tiles.indexOf(lastTile);
                        generator.tiles.set(index, r);
                        //generator.tiles.remove(lastTile);
                        //generator.tiles.add(r);
                        setCurrentTile(r);
                    }
                } else if (lastTile != null) {
                    lastTile.to = generator.getTileRouteManager().getTo(lastTile, newTile);

                    while (true) {
                        r = generator.getTileRouteManager().getStep(lastTile, newTile);

                        if (r == null) {
                            Log.d("www", "step == null");
                            break;
                        }
                        addNewAndRefreshOldTile(r);

                    }
                }

                return r;

            }
            return null;
        }

        private void addNewAndRefreshOldTile(TileRoute r) {


            if (lastTile != null) {
                lastTile.to = generator.getTileRouteManager().getTo(lastTile, r);

                if (lastTile.type == Route.Type.START) {
                    lastTile.from = generator.getTileRouteManager().getOposite(lastTile.to);
                    TileRoute newLastRoute = generator.getTileRouteManager().getRouteStartFromTile(lastTile.from, lastTile.to, lastTile);

                    int index = generator.tiles.indexOf(lastTile);
                    generator.tiles.set(index, newLastRoute);
                    //generator.tiles.add(newLastRoute);
                    //generator.tiles.remove(lastTile);
                    Log.d("www", "start tile to " + newLastRoute.to + " start tile from  " + newLastRoute.from + " type " + newLastRoute.type);

                } else if (lastTile.type == Route.Type.FINISH) {
                    TileRoute newLastRoute = generator.getTileRouteManager().getRouteFromTile(lastTile.from, lastTile.to, lastTile);
                    int index = generator.tiles.indexOf(lastTile);
                    generator.tiles.set(index, newLastRoute);
                    //generator.tiles.add(newLastRoute);

                    //generator.tiles.remove(lastTile);
                    Log.d("www", "last tile to " + newLastRoute.to + " last tile from  " + newLastRoute.from + " type " + newLastRoute.type);

                }


            }

            r.setScale(0);
            TileRoute toRemove = generator.findTile(r.horizontalPos, r.verticalPos);
            int index = generator.tiles.indexOf(toRemove);
            generator.tiles.set(index, r);
            //generator.tiles.add(r);

            //generator.tiles.remove(toRemove);
            setCurrentTile(r);

            Log.d("www", "new tile added " + r.type + " " + r.from + " " + r.to + "####################################");
        }


        public TileRoute trackFinger(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();


            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("aaa", "track finger down");


                    TileRoute t = generator.findTileByCoords(x, y);

                    if (generator.getStartRoute() == null) {
                        setCurrentTile(t);

                        isDraging = true;
                        Log.d("aaa", "draging setted to true");

                    } else if (this.lastTile != null && this.lastTile.contains(x, y)) {
                        isDraging = true;
                        Log.d("aaa", "draging setted to true");
                    } else if (t.type == Route.Type.FINISH) {
                        setCurrentTile(t);
                        isDraging = true;
                        Log.d("aaa", "draging setted to true, current head");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isDraging) {
                        drag(x, y);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (isDraging) {
                        finishDraging(event.getX(), event.getY());
                        isDraging = false;
                        Log.d("aaa", "draging setted to false");
                    }
                    break;

            }


            return null;
        }
    }


}
