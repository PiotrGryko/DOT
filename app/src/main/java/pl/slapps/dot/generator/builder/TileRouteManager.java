package pl.slapps.dot.generator.builder;

import android.util.Log;

import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 08/03/16.
 */
public class TileRouteManager {


    private Generator generator;

    public TileRouteManager(Generator generator) {
        this.generator = generator;
    }

    public enum CrossType {
        TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
    }

    public boolean isNeighboor(TileRoute currentTile, TileRoute tile) {

        int wDiff = Math.abs(tile.horizontalPos - currentTile.horizontalPos);
        int hDiff = Math.abs(tile.verticalPos - currentTile.verticalPos);


        if (wDiff < 2 && hDiff < 2)
            return true;
        return false;
    }

    public boolean areConnected(TileRoute currentTile, TileRoute tile)
    {

        if(!isNeighboor(currentTile,tile))
        {
        return false;

        }

        if(currentTile.to == getOposite(tile.from)||currentTile.from==getOposite(tile.to))
            return true;

        return false;
    }
    public CrossType areCrossed(TileRoute currentTile, TileRoute tile) {

        if (!isNeighboor(currentTile, tile))
            return null;

        int wDiff = tile.horizontalPos - currentTile.horizontalPos;
        int hDiff = tile.verticalPos - currentTile.verticalPos;

        if (wDiff != 0 && hDiff != 0) {

            if (wDiff == -1 && hDiff == -1)
                return CrossType.TOPLEFT;

            else if (wDiff == 1 && hDiff == -1)
                return CrossType.TOPRIGHT;

            else if (wDiff == -1 && hDiff == 1)
                return CrossType.BOTTOMLEFT;
            else if (wDiff == 1 && hDiff == 1)
                return CrossType.BOTTOMRIGHT;


        }


        return null;

    }

    public TileRoute getStep(TileRoute currentTile, TileRoute tile) {

        if(currentTile.verticalPos==tile.verticalPos && currentTile.horizontalPos==tile.horizontalPos)
            return null;

        Route.Direction startFrom = getOposite(currentTile.to);
        int xStart = currentTile.horizontalPos;
        int yStart = currentTile.verticalPos;
        switch (startFrom)
        {

            case LEFT:
                xStart+=1;
                break;
            case RIGHT:
                xStart-=1;
                break;
            case TOP:
                yStart+=1;
                break;
            case BOTTOM:
                yStart-=1;
                break;

        }

        TileRoute toRemove = generator.findTile(xStart,yStart);
        if(toRemove==tile)
            toRemove=null;


        if(toRemove==null)
            return null;

        if(toRemove.type!= Route.Type.TILE)
            toRemove=null;

        Route.Direction from = getFrom(currentTile, toRemove);
        Route.Direction to = getTo(toRemove, tile);

        Log.d("www","STEP from: " +from +" to: "+to+"type: ROUTE  coords: "+xStart +" "+yStart);

        if(from==null)
            return null;



        generator.tiles.remove(toRemove);
        return getRouteFromTile(from, to, toRemove);
    }


    public Route.Direction getOposite(Route.Direction direction) {
        switch (direction) {
            case LEFT:
                return Route.Direction.RIGHT;
            case RIGHT:
                return Route.Direction.LEFT;
            case BOTTOM:
                return Route.Direction.TOP;
            case TOP:
                return Route.Direction.BOTTOM;
        }
        return null;
    }

    public Route.Direction getTo(TileRoute currentTile, TileRoute newTile) {
        if (currentTile.horizontalPos < newTile.horizontalPos) {
            return Route.Direction.RIGHT;
        }
        if (currentTile.horizontalPos > newTile.horizontalPos) {
            return Route.Direction.LEFT;
        }
        if (currentTile.verticalPos < newTile.verticalPos) {
            return Route.Direction.BOTTOM;
        }
        if (currentTile.verticalPos > newTile.verticalPos) {
            return Route.Direction.TOP;
        }
        return null;

    }


    public Route.Direction getFrom(TileRoute currentTile, TileRoute newTile) {


        if (currentTile.horizontalPos < newTile.horizontalPos) {
            return Route.Direction.LEFT;
        }
        if (currentTile.horizontalPos > newTile.horizontalPos) {
            return Route.Direction.RIGHT;
        }
        if (currentTile.verticalPos < newTile.verticalPos) {
            return Route.Direction.TOP;
        }
        if (currentTile.verticalPos > newTile.verticalPos) {
            return Route.Direction.BOTTOM;
        }
        return null;

    }


    public TileRoute getRouteFromTile(Route.Direction from, Route.Direction to, TileRoute currentTile) {

        return new TileRoute(generator.view.screenWidth,
                generator.view.screenHeight,
                generator.gridX,
                generator.gridY,
                currentTile.horizontalPos,
                currentTile.verticalPos,
                from,
                to,
                Route.Type.ROUTE, generator);
    }

    public TileRoute getRouteStartFromTile(Route.Direction from, Route.Direction to, TileRoute currentTile) {

        return new TileRouteStart(generator.view.screenWidth,
                generator.view.screenHeight,
                generator.gridX,
                generator.gridY,
                currentTile.horizontalPos,
                currentTile.verticalPos,
                from,
                to,
                generator);
    }

    public TileRoute getRouteFinishFromTile(Route.Direction from, Route.Direction to, TileRoute currentTile) {

        return new TileRouteFinish(generator.view.screenWidth,
                generator.view.screenHeight,
                generator.gridX,
                generator.gridY,
                currentTile.horizontalPos,
                currentTile.verticalPos,
                from,
                getOposite(from),
                generator);
    }


}