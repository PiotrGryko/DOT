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

        //canvasView.onTouch(event);

        TileRoute result = fingerTracker.trackFinger(event);


        return true;

    }

    class FingerTracker {


        public void setCurrentTile(TileRoute tile) {

            if (this.currentTile != null)
                this.currentTile.setCurrentTile(false);
            this.currentTile = tile;
            this.currentTile.setCurrentTile(true);

            generator.getLayout().setCurrentTile(currentTile);
        }

        public void setDraging(boolean draging) {
            this.isDraging = draging;
        }

        private void finishDraging(float x, float y) {

            currentTile = lastTile;
            if (currentTile != null && currentTile.getType() == Route.Type.ROUTE) {

                TileRoute r = generator.getTileRouteManager().getRouteFinishFromTile(currentTile.from, currentTile.to, currentTile);
                //  TileRoute r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, currentTile.horizontalPos, currentTile.verticalPos, currentTile.from.name(), currentTile.to.name(),
                //          generator);
                generator.tiles.remove(currentTile);
                generator.tiles.add(r);

                setCurrentTile(r);
                lastTile = r;
                Log.d("aaa", "dragging finished");

            }

            isDraging = false;
        }


        TileRoute currentTile;
        TileRoute lastTile;
        private boolean isDraging;


        private TileRoute drag(float x, float y) {


            //newTile - tile in front
            //current tile - in that case tile to draw. Nearest empty tile
            //lasttile - end of the current maze
            TileRoute newTile = generator.findTileByCoords(x, y);
            if (currentTile != newTile
                //&& generator.getTileRouteManager().isNeighboor(currentTile, newTile)
                    ) {

                /*
                if(newTile.type== Route.Type.ROUTE  && generator.getTileRouteManager().areConnected(currentTile,newTile))
                {
                    Log.d("rrr","are connected");
                    generator.tiles.remove(currentTile);
                    setCurrentTile(newTile);
                }
                else
                */
                if (newTile.type != Route.Type.TILE)

                {
                    finishDraging(x, y);
                    return null;
                }
                //we know new tile from
                //we know old tile to

                Route.Direction newfrom = generator.getTileRouteManager().getFrom(currentTile, newTile);
                newTile.from = newfrom;
                Route.Direction currentto = generator.getTileRouteManager().getTo(currentTile, newTile);
                currentTile.to = currentto;

                //TileRoute sh = buildConnection(currentTile, newTile);


                TileRoute r;
                if (generator.getStartRoute() == null) {
                    currentTile.from = newfrom;
                    Log.d("www", "current tile as a start route " + currentTile.from + " " + currentTile.to);

                    r = generator.getTileRouteManager().getRouteStartFromTile(currentTile.from, currentTile.to, currentTile);
                    if (r != null) {
                        generator.tiles.add(r);
                        lastTile = r;
                        //addNewAndRefreshOldTile(r);
                    }
                    setCurrentTile(newTile);
                } else {


                    while (true) {
                        if (lastTile != null) {
                            r = generator.getTileRouteManager().getStep(lastTile, newTile);

                            if (r == null)
                                break;

                            addNewAndRefreshOldTile(r);
                        }
                    }
                    setCurrentTile(newTile);
                    //generator.getTileRouteManager().getRouteFinishFromTile(currentTile.from, currentTile.to, currentTile);


                }


                //generator.tiles.remove(currentTile);


                return r;

            }
            return null;
        }

        private void addNewAndRefreshOldTile(TileRoute r) {
            if (lastTile != null) {
                lastTile.to = generator.getTileRouteManager().getTo(lastTile, r);
                //lastTile.from=generator.getTileRouteManager().getFrom(lastTile,r);

                if (lastTile.type == Route.Type.FINISH) {
                    TileRoute newLastRoute = generator.getTileRouteManager().getRouteFromTile(lastTile.from, lastTile.to, lastTile);
                    generator.tiles.remove(lastTile);
                    generator.tiles.add(newLastRoute);
                    Log.d("www", "last tile to " + newLastRoute.to + " last tile from  " + newLastRoute.from + " type " + newLastRoute.type);

                } else {
                    Log.d("www", "last tile == start " + lastTile.to + " last tile from  " + lastTile.from);

                }


            }

            //generator.tiles.remove(currentTile);

            generator.tiles.add(r);
            lastTile = r;
            //lastTile = generator.getTileRouteManager().getRouteFromTile(r.from, r.to, r);
//            Log.d("www", "last tile value seted  " + lastTile.to + " last tile from  " + lastTile.from);

            Log.d("www", "new tile added " + r.type + " " + r.from + " " + r.to + "####################################");
        }


        public TileRoute trackFinger(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();


            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:


                    if (generator.getStartRoute() == null) {
                        setCurrentTile(generator.findTileByCoords(x, y));

                        isDraging = true;
                        Log.d("aaa", "draging setted to true");

                    } else if (this.currentTile != null && this.currentTile.contains(x, y)) {
                        isDraging = true;
                        Log.d("aaa", "draging setted to true");
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
