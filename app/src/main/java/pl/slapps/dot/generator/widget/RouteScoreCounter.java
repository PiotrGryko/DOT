package pl.slapps.dot.generator.widget;

import pl.slapps.dot.generator.TileRoute;

/**
 * Created by piotr on 21/02/16.
 */
public class RouteScoreCounter {


    private TileRoute route;
    private float enterX;
    private float enterY;

    private float exitX;
    private float exitY;

    public RouteScoreCounter(TileRoute route)
    {
        setRoute(route);
    }

    public void setRoute(TileRoute route)
    {
        this.route=route;
        this.enterX = route.centerX;
        this.enterY=route.centerY;
        this.exitX=enterX;
        this.exitY=enterY;
    }


    public TileRoute getRoute()
    {
        return route;
    }


    public void setEnterCoords(float x, float y)
    {
        this.enterX=x;
        this.enterY=y;

    }

    public void setExitCoords(float x, float y)
    {
        this.exitX=x;
        this.exitY=y;
    }

    public float estimateScore()
    {
        float score =0;

        switch (route.getDirection())
        {

            case LEFTRIGHT:
            case RIGHTLEFT: {
                float enterDiff = Math.abs(route.centerY - enterY);
                float exitDiff = Math.abs(route.centerY - exitY);
                float maxDiff = route.height/2;
                float scoreEnter = (maxDiff - enterDiff) / maxDiff;
                float scoreExit = (maxDiff - exitDiff) / maxDiff;

                score = scoreEnter*scoreExit;
            break;
            }
            case BOTTOMTOP:
            case TOPBOTTOM: {
                float enterDiff = Math.abs(route.centerX - enterX);
                float exitDiff = Math.abs(route.centerX - exitX);
                float maxDiff = route.width/2;
                float scoreEnter = (maxDiff - enterDiff) / maxDiff;
                float scoreExit = (maxDiff - exitDiff) / maxDiff;

                score = scoreEnter*scoreExit;
                break;
            }


            case LEFTBOTTOM:
            case LEFTTOP:
            case RIGHTTOP:
            case RIGHTBOTTOM:{
                float enterDiff = Math.abs(route.centerY - enterY);
                float exitDiff = Math.abs(route.centerX - exitX);
                float maxDiffEnter = route.height/2;
                float maxDiffExit = route.width/2;

                float scoreEnter = (maxDiffEnter - enterDiff) / maxDiffEnter;
                float scoreExit = (maxDiffExit - exitDiff) / maxDiffExit;

                score = scoreEnter*scoreExit;
                break;
            }


            case BOTTOMLEFT:
            case BOTTOMRIGHT:
            case TOPLEFT:
            case TOPRIGHT:{
                float enterDiff = Math.abs(route.centerX - enterX);
                float exitDiff = Math.abs(route.centerY - exitY);
                float maxDiffEnter = route.width/2;
                float maxDiffExit = route.height/2;

                float scoreEnter = (maxDiffEnter - enterDiff) / maxDiffEnter;
                float scoreExit = (maxDiffExit - exitDiff) / maxDiffExit;

                score = scoreEnter*scoreExit;
                break;
            }



        }

        return score;
    }

}
