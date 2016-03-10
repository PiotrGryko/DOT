package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.builder.TileRouteStart;
import pl.slapps.dot.generator.widget.NumberPickerTextView;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutPath {

    private String TAG = GeneratorLayoutPath.class.getName();
    private View layoutRoutes;
    private GeneratorLayout generatorLayout;
    private Generator generator;


    /*
   path buttons
    */
    private ImageView tvBottomLeft;
    private ImageView tvBottomRight;
    private ImageView tvBottomTop;
    private ImageView tvLeftTop;
    private ImageView tvLeftRight;
    private ImageView tvRightTop;

    private ImageView tvStartTop;
    private ImageView tvStartBottom;
    private ImageView tvStartRight;
    private ImageView tvStartLeft;

    private ImageView tvFinishTop;
    private ImageView tvFinishBottom;
    private ImageView tvFinishLeft;
    private ImageView tvFinishRight;

    private TextView tvStartLabel;
    private TextView tvPathLabel;
    private TextView tvFinishLabel;


    private LinearLayout layoutDetails;
    private LinearLayout layoutPaths;


    private NumberPickerTextView etSpeedRatio;
    private TextView btnSound;
    private ImageView btnPlay;
    private ImageView imgTrash;
    private CheckBox drawCoin;


    public View getLayout() {
        return layoutRoutes;
    }


    ///////////////////////////////////////////////////////////////////////////////////////

    /////routes

    private Route.Direction considerRouteTo(TileRoute route)
    {
        Route.Direction direction = route.to;
        if(route.getType()== Route.Type.FINISH)
        {
            direction=null;
        }
        return  direction;
    }
    private Route.Direction considerRouteFrom(TileRoute route)
    {
        Route.Direction direction = route.from;
        if(route.getType()== Route.Type.START)
        {
            direction=null;
        }
        return  direction;
    }


    private void toggleBottomTop(TileRoute top, TileRoute bottom) {

        if (top == null || bottom == null) {
            tvBottomTop.setVisibility(View.GONE);
            return;
        }

        Route.Direction topFrom = considerRouteFrom(top);
        Route.Direction topTo = considerRouteTo(top);


        Route.Direction bottomFrom = considerRouteFrom(bottom);
        Route.Direction bottomTo = considerRouteTo(bottom);



        if (top.getType() != Route.Type.TILE && bottom.getType() == Route.Type.TILE) {
            if (topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM)
                tvBottomTop.setVisibility(View.VISIBLE);
            else
                tvBottomTop.setVisibility(View.GONE);

            return;
        }

        if (top.getType() == Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if (bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                tvBottomTop.setVisibility(View.VISIBLE);
            else
                tvBottomTop.setVisibility(View.GONE);

            return;
        }

        if (top.getType() != Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if ((bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                    &&
                    (topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM))
                tvBottomTop.setVisibility(View.VISIBLE);
            else
                tvBottomTop.setVisibility(View.GONE);

            return;
        }


    }


    private void toggleLeftRight(TileRoute left, TileRoute right) {
        if (left == null || right == null) {
            tvLeftRight.setVisibility(View.GONE);
            return;
        }


        Route.Direction leftFrom = considerRouteFrom(left);
        Route.Direction leftTo = considerRouteTo(left);


        Route.Direction rightFrom = considerRouteFrom(right);
        Route.Direction rightTo = considerRouteTo(right);

        if (left.getType() != Route.Type.TILE && right.getType() == Route.Type.TILE) {
            if (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT)
                tvLeftRight.setVisibility(View.VISIBLE);
            else
                tvLeftRight.setVisibility(View.GONE);

            return;
        }

        if (left.getType() == Route.Type.TILE && right.getType() != Route.Type.TILE) {
            if (rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT)
                tvLeftRight.setVisibility(View.VISIBLE);
            else
                tvLeftRight.setVisibility(View.GONE);

            return;
        }

        if (left.getType() != Route.Type.TILE && right.getType() != Route.Type.TILE) {
            if ((rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT)
                    &&
                    (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT))
                tvLeftRight.setVisibility(View.VISIBLE);
            else
                tvLeftRight.setVisibility(View.GONE);

            return;
        }
    }

    private void toggleLeftTop(TileRoute left, TileRoute top) {
        if (left == null || top == null) {
            tvLeftTop.setVisibility(View.GONE);
            return;
        }

        Route.Direction topFrom = considerRouteFrom(top);
        Route.Direction topTo = considerRouteTo(top);


        Route.Direction leftFrom = considerRouteFrom(left);
        Route.Direction leftTo = considerRouteTo(left);


        if (left.getType() != Route.Type.TILE && top.getType() == Route.Type.TILE) {
            if (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT)
                tvLeftTop.setVisibility(View.VISIBLE);
            else
                tvLeftTop.setVisibility(View.GONE);

            return;
        }

        if (left.getType() == Route.Type.TILE && top.getType() != Route.Type.TILE) {
            if (topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM)
                tvLeftTop.setVisibility(View.VISIBLE);
            else
                tvLeftTop.setVisibility(View.GONE);

            return;
        }

        if (left.getType() != Route.Type.TILE && top.getType() != Route.Type.TILE) {
            if ((topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM)
                    &&
                    (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT))
                tvLeftTop.setVisibility(View.VISIBLE);
            else
                tvLeftTop.setVisibility(View.GONE);

            return;
        }
    }


    private void toggleRightTop(TileRoute right, TileRoute top) {
        if (right == null || top == null) {
            tvRightTop.setVisibility(View.GONE);
            return;
        }

        Route.Direction topFrom = considerRouteFrom(top);
        Route.Direction topTo = considerRouteTo(top);


        Route.Direction rightFrom = considerRouteFrom(right);
        Route.Direction rightTo = considerRouteTo(right);

        if (right.getType() != Route.Type.TILE && top.getType() == Route.Type.TILE) {
            if (rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT)
                tvRightTop.setVisibility(View.VISIBLE);
            else
                tvRightTop.setVisibility(View.GONE);
            return;
        }

        if (right.getType() == Route.Type.TILE && top.getType() != Route.Type.TILE) {
            if (topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM)
                tvRightTop.setVisibility(View.VISIBLE);
            else
                tvRightTop.setVisibility(View.GONE);
            return;
        }

        if (right.getType() != Route.Type.TILE && top.getType() != Route.Type.TILE) {
            if (
                    (topFrom == Route.Direction.BOTTOM || topTo == Route.Direction.BOTTOM)
                            &&
                            (rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT)
                    )
                tvRightTop.setVisibility(View.VISIBLE);
            else
                tvRightTop.setVisibility(View.GONE);
            return;
        }


    }


    private void toggleRightBottom(TileRoute right, TileRoute bottom) {
        if (right == null || bottom == null) {
            tvBottomRight.setVisibility(View.GONE);
            return;
        }

        Route.Direction rightFrom = considerRouteFrom(right);
        Route.Direction rightTo = considerRouteTo(right);

        Route.Direction bottomFrom = considerRouteFrom(bottom);
        Route.Direction bottomTo = considerRouteTo(bottom);

        if (right.getType() != Route.Type.TILE && bottom.getType() == Route.Type.TILE) {
            if (rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT)
                tvBottomRight.setVisibility(View.VISIBLE);
            else
                tvBottomRight.setVisibility(View.GONE);

            return;
        }

        if (right.getType() == Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if (bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                tvBottomRight.setVisibility(View.VISIBLE);
            else
                tvBottomRight.setVisibility(View.GONE);

            return;
        }

        if (right.getType() != Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if ((bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                    &&
                    (rightFrom == Route.Direction.LEFT || rightTo == Route.Direction.LEFT))
                tvBottomRight.setVisibility(View.VISIBLE);
            else
                tvBottomRight.setVisibility(View.GONE);

            return;
        }


    }


    private void toggleLeftBottom(TileRoute left, TileRoute bottom) {
        if (left == null || bottom == null) {
            tvBottomLeft.setVisibility(View.GONE);
            return;
        }

        Route.Direction leftFrom = considerRouteFrom(left);
        Route.Direction leftTo = considerRouteTo(left);


        Route.Direction bottomFrom = considerRouteFrom(bottom);
        Route.Direction bottomTo = considerRouteTo(bottom);

        if (left.getType() != Route.Type.TILE && bottom.getType() == Route.Type.TILE) {
            if (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT)
                tvBottomLeft.setVisibility(View.VISIBLE);
            else
                tvBottomLeft.setVisibility(View.GONE);

            return;
        }

        if (left.getType() == Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if (bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                tvBottomLeft.setVisibility(View.VISIBLE);
            else
                tvBottomLeft.setVisibility(View.GONE);

            return;
        }

        if (left.getType() != Route.Type.TILE && bottom.getType() != Route.Type.TILE) {
            if ((bottomFrom == Route.Direction.TOP || bottomTo == Route.Direction.TOP)
                    &&
                    (leftFrom == Route.Direction.RIGHT || leftTo == Route.Direction.RIGHT))
                tvBottomLeft.setVisibility(View.VISIBLE);
            else
                tvBottomLeft.setVisibility(View.GONE);

            return;
        }


    }

    //starts
    private void toggleStartBottom(TileRoute top) {



        if (top == null) {
            tvStartBottom.setVisibility(View.GONE);
            return;
        }



        if (top.getType() != Route.Type.TILE) {
            if (top.from == Route.Direction.BOTTOM || top.to == Route.Direction.BOTTOM)
                tvStartBottom.setVisibility(View.VISIBLE);
            else
                tvStartBottom.setVisibility(View.GONE);

            return;
        }

    }

    private void toggleStartTop(TileRoute bottom) {

        if (bottom == null) {
            tvStartTop.setVisibility(View.GONE);
            return;
        }

        if (bottom.getType() != Route.Type.TILE) {
            if (bottom.from == Route.Direction.TOP || bottom.to == Route.Direction.TOP)
                tvStartTop.setVisibility(View.VISIBLE);
            else
                tvStartTop.setVisibility(View.GONE);

            return;
        }

    }

    private void toggleStartLeft(TileRoute right) {

        if (right == null) {
            tvStartLeft.setVisibility(View.GONE);
            return;
        }
        if (right.getType() != Route.Type.TILE) {
            if (right.from == Route.Direction.LEFT || right.to == Route.Direction.LEFT)
                tvStartLeft.setVisibility(View.VISIBLE);
            else
                tvStartLeft.setVisibility(View.GONE);

            return;
        }

    }

    private void toggleStartRight(TileRoute left) {

        if (left == null) {
            tvStartRight.setVisibility(View.GONE);
            return;
        }
        if (left.getType() != Route.Type.TILE) {
            if (left.from == Route.Direction.RIGHT || left.to == Route.Direction.RIGHT)
                tvStartRight.setVisibility(View.VISIBLE);
            else
                tvStartRight.setVisibility(View.GONE);

            return;
        }

    }


    //finishes


    private void toggleFinishBottom(TileRoute top) {

        if (top == null) {
            tvFinishBottom.setVisibility(View.GONE);
            return;
        }

        if (top.getType() != Route.Type.TILE) {
            if (top.from == Route.Direction.BOTTOM || top.to == Route.Direction.BOTTOM)
                tvFinishBottom.setVisibility(View.VISIBLE);
            else
                tvFinishBottom.setVisibility(View.GONE);

            return;
        }

    }

    private void toggleFinishTop(TileRoute bottom) {

        if (bottom == null) {
            tvFinishTop.setVisibility(View.GONE);
            return;
        }

        if (bottom.getType() != Route.Type.TILE) {
            if (bottom.from == Route.Direction.TOP || bottom.to == Route.Direction.TOP)
                tvFinishTop.setVisibility(View.VISIBLE);
            else
                tvFinishTop.setVisibility(View.GONE);

            return;
        }

    }


    private void toggleFinishLeft(TileRoute right) {

        if (right == null) {
            tvFinishLeft.setVisibility(View.GONE);
            return;
        }
        if (right.getType() != Route.Type.TILE) {
            if (right.from == Route.Direction.LEFT || right.to == Route.Direction.LEFT)
                tvFinishLeft.setVisibility(View.VISIBLE);
            else
                tvFinishLeft.setVisibility(View.GONE);

            return;
        }

    }

    private void toggleFinishRight(TileRoute left) {

        if (left == null) {
            tvFinishRight.setVisibility(View.GONE);
            return;
        }
        if (left.getType() != Route.Type.TILE) {
            if (left.from == Route.Direction.RIGHT || left.to == Route.Direction.RIGHT)
                tvFinishRight.setVisibility(View.VISIBLE);
            else
                tvFinishRight.setVisibility(View.GONE);

            return;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public void estimateAllDirections()


    {


        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        TileRoute topTile = generator.findTile(x, y - 1);
        TileRoute bottomTile = generator.findTile(x, y + 1);
        TileRoute rightTile = generator.findTile(x + 1, y);
        TileRoute leftTile = generator.findTile(x - 1, y);

        toggleStartBottom(topTile);
        toggleStartTop(bottomTile);
        toggleStartLeft(rightTile);
        toggleStartRight(leftTile);

        toggleFinishBottom(topTile);
        toggleFinishTop(bottomTile);
        toggleFinishLeft(rightTile);
        toggleFinishRight(leftTile);

        toggleBottomTop(topTile, bottomTile);
        toggleLeftRight(leftTile, rightTile);
        toggleLeftTop(leftTile, topTile);
        toggleRightTop(rightTile, topTile);
        toggleRightBottom(rightTile, bottomTile);
        toggleLeftBottom(leftTile, bottomTile);

        hideStart();
/*
        //top

        if (y - 1 < 0 || topTile == null || topTile.getType() != Route.Type.TILE) {

            tvStartBottom.setVisibility(View.GONE);
            tvFinishBottom.setVisibility(View.GONE);
            tvLeftTop.setVisibility(View.GONE);
            tvRightTop.setVisibility(View.GONE);
            tvBottomTop.setVisibility(View.GONE);

        }
        //bottom
        if (y + 1 >= generator.gridY || bottomTile == null || bottomTile.getType() != Route.Type.TILE) {
            tvBottomTop.setVisibility(View.GONE);
            tvBottomRight.setVisibility(View.GONE);
            tvBottomLeft.setVisibility(View.GONE);
            tvStartTop.setVisibility(View.GONE);
            tvFinishTop.setVisibility(View.GONE);
        }
        //right
        if (x + 1 >= generator.gridX || rightTile == null || rightTile.getType() != Route.Type.TILE) {
            tvRightTop.setVisibility(View.GONE);
            tvBottomRight.setVisibility(View.GONE);
            tvLeftRight.setVisibility(View.GONE);
            tvStartLeft.setVisibility(View.GONE);
            tvFinishLeft.setVisibility(View.GONE);
        }
        //left
        if (x - 1 < 0 || leftTile == null || leftTile.getType() != Route.Type.TILE) {
            tvLeftRight.setVisibility(View.GONE);
            tvLeftTop.setVisibility(View.GONE);
            tvBottomLeft.setVisibility(View.GONE);
            tvFinishRight.setVisibility(View.GONE);
            tvStartRight.setVisibility(View.GONE);
        }

*/

        refreashLabels();
    }

    private void hideStart() {
        if (generator.getStartRoute() == null) {
            tvBottomLeft.setVisibility(View.GONE);
            tvBottomRight.setVisibility(View.GONE);
            tvBottomTop.setVisibility(View.GONE);

            tvLeftTop.setVisibility(View.GONE);
            tvLeftRight.setVisibility(View.GONE);
            tvRightTop.setVisibility(View.GONE);

            tvFinishBottom.setVisibility(View.GONE);
            tvFinishTop.setVisibility(View.GONE);
            tvFinishLeft.setVisibility(View.GONE);
            tvFinishRight.setVisibility(View.GONE);


        } else {
            tvStartBottom.setVisibility(View.GONE);
            tvStartTop.setVisibility(View.GONE);
            tvStartLeft.setVisibility(View.GONE);
            tvStartRight.setVisibility(View.GONE);


        }


    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void estimateFromLeft() {
       /*
        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;




        //top
        if (y - 1 < 0 || generator.findTile(x, y - 1) == null || generator.findTile(x, y - 1).getType() != Route.Type.TILE) {
            tvLeftTop.setVisibility(View.GONE);
        }
        //bottom
        if (y + 1 >= generator.gridY || generator.findTile(x, y + 1) == null || generator.findTile(x, y + 1).getType() != Route.Type.TILE) {
            tvBottomLeft.setVisibility(View.GONE);
        }
        //right
        if (x + 1 >= generator.gridX || generator.findTile(x + 1, y) == null || generator.findTile(x + 1, y).getType() != Route.Type.TILE) {
            tvLeftRight.setVisibility(View.GONE);
        }
        //left
        if (x - 1 < 0 || generator.findTile(x - 1, y) == null || generator.findTile(x - 1, y).getType() != Route.Type.TILE) {
        }
    */
        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        TileRoute topTile = generator.findTile(x, y - 1);
        TileRoute bottomTile = generator.findTile(x, y + 1);
        TileRoute rightTile = generator.findTile(x + 1, y);
        TileRoute leftTile = generator.findTile(x - 1, y);

        toggleLeftTop(leftTile, topTile);
        toggleLeftBottom(leftTile, bottomTile);
        toggleLeftRight(leftTile, rightTile);

    }


    private void estimateFromRight() {
       /*
        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        //top
        if (y - 1 < 0 || generator.findTile(x, y - 1) == null || generator.findTile(x, y - 1).getType() != Route.Type.TILE) {
            tvRightTop.setVisibility(View.GONE);
        }
        //bottom
        if (y + 1 >= generator.gridY || generator.findTile(x, y + 1) == null || generator.findTile(x, y + 1).getType() != Route.Type.TILE) {
            tvBottomRight.setVisibility(View.GONE);
        }
        //right
        if (x + 1 >= generator.gridX || generator.findTile(x + 1, y) == null || generator.findTile(x + 1, y).getType() != Route.Type.TILE) {
            // tvLeftRight.setVisibility(View.GONE);
        }
        //left
        if (x - 1 < 0 || generator.findTile(x - 1, y) == null || generator.findTile(x - 1, y).getType() != Route.Type.TILE) {
            tvLeftRight.setVisibility(View.GONE);

        }
    */

        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        TileRoute topTile = generator.findTile(x, y - 1);
        TileRoute bottomTile = generator.findTile(x, y + 1);
        TileRoute rightTile = generator.findTile(x + 1, y);
        TileRoute leftTile = generator.findTile(x - 1, y);

        toggleRightTop(rightTile, topTile);
        toggleRightBottom(rightTile, bottomTile);
        toggleLeftRight(leftTile, rightTile);

    }


    private void estimateFromBottom() {

       /*
        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        //top
        if (y - 1 < 0 || generator.findTile(x, y - 1) == null || generator.findTile(x, y - 1).getType() != Route.Type.TILE) {
            tvBottomTop.setVisibility(View.GONE);
        }
        //bottom
        if (y + 1 >= generator.gridY || generator.findTile(x, y + 1) == null || generator.findTile(x, y + 1).getType() != Route.Type.TILE) {
            //    tvBottomLeft.setVisibility(View.GONE);
        }
        //right
        if (x + 1 >= generator.gridX || generator.findTile(x + 1, y) == null || generator.findTile(x + 1, y).getType() != Route.Type.TILE) {
            tvBottomRight.setVisibility(View.GONE);
        }
        //left
        if (x - 1 < 0 || generator.findTile(x - 1, y) == null || generator.findTile(x - 1, y).getType() != Route.Type.TILE) {
            tvBottomLeft.setVisibility(View.GONE);

        }
    */

        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        TileRoute topTile = generator.findTile(x, y - 1);
        TileRoute bottomTile = generator.findTile(x, y + 1);
        TileRoute rightTile = generator.findTile(x + 1, y);
        TileRoute leftTile = generator.findTile(x - 1, y);

        toggleBottomTop(topTile, bottomTile);
        toggleRightBottom(rightTile, bottomTile);
        toggleLeftBottom(leftTile, bottomTile);

    }

    private void estimateFromTop() {


        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        TileRoute topTile = generator.findTile(x, y - 1);
        TileRoute bottomTile = generator.findTile(x, y + 1);
        TileRoute rightTile = generator.findTile(x + 1, y);
        TileRoute leftTile = generator.findTile(x - 1, y);

        toggleBottomTop(topTile, bottomTile);
        toggleRightTop(rightTile, topTile);
        toggleLeftTop(leftTile, topTile);


/*
        int x = generatorLayout.tile.horizontalPos;
        int y = generatorLayout.tile.verticalPos;

        //top
        if (y - 1 < 0 || generator.findTile(x, y - 1) == null || generator.findTile(x, y - 1).getType() != Route.Type.TILE) {
            //   tvBottomTop.setVisibility(View.GONE);
        }
        //bottom
        if (y + 1 >= generator.gridY || generator.findTile(x, y + 1) == null || generator.findTile(x, y + 1).getType() != Route.Type.TILE) {
            tvBottomTop.setVisibility(View.GONE);
        }
        //right
        if (x + 1 >= generator.gridX || generator.findTile(x + 1, y) == null || generator.findTile(x + 1, y).getType() != Route.Type.TILE) {
            tvRightTop.setVisibility(View.GONE);
        }
        //left
        if (x - 1 < 0 || generator.findTile(x - 1, y) == null || generator.findTile(x - 1, y).getType() != Route.Type.TILE) {
            tvLeftTop.setVisibility(View.GONE);

        }
  */
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void refreashPathTiles() {


        if (generatorLayout.oldTile != null && generatorLayout.oldTile.getType() != Route.Type.FINISH) {
            tvStartBottom.setVisibility(View.GONE);
            tvStartLeft.setVisibility(View.GONE);
            tvStartRight.setVisibility(View.GONE);
            tvStartTop.setVisibility(View.GONE);

            switch (generatorLayout.oldTile.getDirection()) {
                case LEFTRIGHT:
                case TOPRIGHT:
                case BOTTOMRIGHT: {
                    tvBottomRight.setVisibility(View.GONE);
                    tvBottomTop.setVisibility(View.GONE);
                    tvRightTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);
                    estimateFromLeft();
                    break;
                }
                case RIGHTLEFT:
                case TOPLEFT:
                case BOTTOMLEFT: {
                    tvBottomLeft.setVisibility(View.GONE);
                    tvBottomTop.setVisibility(View.GONE);
                    tvLeftTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    estimateFromRight();
                    break;


                }

                case BOTTOMTOP:
                case LEFTTOP:
                case RIGHTTOP: {
                    tvLeftTop.setVisibility(View.GONE);
                    tvLeftRight.setVisibility(View.GONE);
                    tvRightTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    estimateFromBottom();

                    break;


                }
                case TOPBOTTOM:
                case LEFTBOTTOM:
                case RIGHTBOTTOM: {
                    tvBottomLeft.setVisibility(View.GONE);
                    tvBottomRight.setVisibility(View.GONE);
                    tvLeftRight.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    estimateFromTop();
                    break;


                }

            }
        }

        if (generatorLayout.tile.getType() != Route.Type.TILE) {
            layoutDetails.setVisibility(View.VISIBLE);
            layoutPaths.setVisibility(View.GONE);
        } else {
            layoutDetails.setVisibility(View.GONE);
            layoutPaths.setVisibility(View.VISIBLE);
        }


    }

    private void refreashLabels() {
        if (tvStartRight.getVisibility() == View.GONE
                &&
                tvStartLeft.getVisibility() == View.GONE
                &&
                tvStartTop.getVisibility() == View.GONE
                &&
                tvStartBottom.getVisibility() == View.GONE)
            tvStartLabel.setVisibility(View.GONE);
        else
            tvStartLabel.setVisibility(View.VISIBLE);


        if (tvFinishRight.getVisibility() == View.GONE
                &&
                tvFinishLeft.getVisibility() == View.GONE
                &&
                tvFinishTop.getVisibility() == View.GONE
                &&
                tvFinishBottom.getVisibility() == View.GONE)
            tvFinishLabel.setVisibility(View.GONE);
        else
            tvFinishLabel.setVisibility(View.VISIBLE);


        if (tvLeftTop.getVisibility() == View.GONE
                &&
                tvLeftRight.getVisibility() == View.GONE
                &&
                tvBottomLeft.getVisibility() == View.GONE
                &&
                tvBottomRight.getVisibility() == View.GONE
                &&
                tvRightTop.getVisibility() == View.GONE
                &&
                tvBottomTop.getVisibility() == View.GONE

                )
            tvPathLabel.setVisibility(View.GONE);
        else
            tvPathLabel.setVisibility(View.VISIBLE);


    }


    public void refreashLayout() {


        tvBottomLeft.setVisibility(View.VISIBLE);
        tvBottomRight.setVisibility(View.VISIBLE);
        tvBottomTop.setVisibility(View.VISIBLE);

        tvLeftTop.setVisibility(View.VISIBLE);
        tvLeftRight.setVisibility(View.VISIBLE);

        tvRightTop.setVisibility(View.VISIBLE);


        tvStartTop.setVisibility(View.VISIBLE);
        tvStartBottom.setVisibility(View.VISIBLE);
        tvStartRight.setVisibility(View.VISIBLE);
        tvStartLeft.setVisibility(View.VISIBLE);

        tvFinishTop.setVisibility(View.VISIBLE);
        tvFinishBottom.setVisibility(View.VISIBLE);
        tvFinishLeft.setVisibility(View.VISIBLE);
        tvFinishRight.setVisibility(View.VISIBLE);
        //tvBlank.setVisibility(View.VISIBLE);


        hideStart();

        refreashPathTiles();

        refreashDetailsLayout();

        refreashLabels();


    }


    private void refreashDetailsLayout() {
        if (generatorLayout.tile.sound != null && !generatorLayout.tile.sound.equals(""))
            btnSound.setText(generatorLayout.tile.sound);

        etSpeedRatio.putValue((float) generatorLayout.tile.speedRatio);
        if (generatorLayout.tile.sound != null && !generatorLayout.tile.sound.equals(""))
            btnSound.setText(generatorLayout.tile.sound);

        drawCoin.setChecked(generatorLayout.tile.drawCoin);


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private TileRoute getPrevTile(TileRoute tile) {
        TileRoute prevTile = null;
        switch (tile.getDirection()) {
            case LEFTBOTTOM:
            case LEFTTOP:
            case LEFTRIGHT: {
                prevTile = generator.findTile(tile.horizontalPos - 1, tile.verticalPos);
                break;
            }
            case RIGHTBOTTOM:
            case RIGHTTOP:
            case RIGHTLEFT: {
                prevTile = generator.findTile(tile.horizontalPos + 1, tile.verticalPos);
                break;
            }

            case TOPLEFT:
            case TOPBOTTOM:
            case TOPRIGHT: {
                prevTile = generator.findTile(tile.horizontalPos, tile.verticalPos - 1);
                break;
            }
            case BOTTOMLEFT:
            case BOTTOMTOP:
            case BOTTOMRIGHT: {
                prevTile = generator.findTile(tile.horizontalPos, tile.verticalPos + 1);
                break;
            }
        }

        return prevTile;
    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;

        layoutRoutes = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_path, null);
        tvStartLabel = (TextView) layoutRoutes.findViewById(R.id.tv_start_tiles);
        tvPathLabel = (TextView) layoutRoutes.findViewById(R.id.tv_path_tiles);
        tvFinishLabel = (TextView) layoutRoutes.findViewById(R.id.tv_finish_tiles);


        layoutDetails = (LinearLayout) layoutRoutes.findViewById(R.id.layout_details);
        layoutPaths = (LinearLayout) layoutRoutes.findViewById(R.id.layout_routes);

        imgTrash = (ImageView) layoutDetails.findViewById(R.id.img_trash);
        etSpeedRatio = (NumberPickerTextView) layoutDetails.findViewById(R.id.et_speed_ratio);
        btnSound = (TextView) layoutDetails.findViewById(R.id.btn_choose_sound);
        btnPlay = (ImageView) layoutDetails.findViewById(R.id.btn_play_sound);
        drawCoin = (CheckBox)layoutDetails.findViewById(R.id.draw_coin);

        tvBottomLeft = (ImageView) layoutRoutes.findViewById(R.id.tv_bottomleft);
        tvBottomRight = (ImageView) layoutRoutes.findViewById(R.id.tv_bottomright);
        tvBottomTop = (ImageView) layoutRoutes.findViewById(R.id.tv_bottomtop);

        tvLeftTop = (ImageView) layoutRoutes.findViewById(R.id.tv_lefttop);
        tvLeftRight = (ImageView) layoutRoutes.findViewById(R.id.tv_leftright);

        tvRightTop = (ImageView) layoutRoutes.findViewById(R.id.tv_righttop);


        tvStartTop = (ImageView) layoutRoutes.findViewById(R.id.tv_starttop);
        tvStartBottom = (ImageView) layoutRoutes.findViewById(R.id.tv_startbottom);
        tvStartRight = (ImageView) layoutRoutes.findViewById(R.id.tv_startright);
        tvStartLeft = (ImageView) layoutRoutes.findViewById(R.id.tv_startleft);

        tvFinishTop = (ImageView) layoutRoutes.findViewById(R.id.tv_finishtop);
        tvFinishBottom = (ImageView) layoutRoutes.findViewById(R.id.tv_finishbottom);
        tvFinishLeft = (ImageView) layoutRoutes.findViewById(R.id.tv_finishleft);
        tvFinishRight = (ImageView) layoutRoutes.findViewById(R.id.tv_finishright);
        //  tvBlank = (ImageView) v.findViewById(R.id.tv_blank);

        initdetailsLayout();

        refreashLayout();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                generator.view.context.getSoundsManager().playRawFile("construct");

                final int id = vi.getId();
                TileRoute r = null;

                switch (id) {
                    case R.id.tv_leftright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT, Route.Direction.RIGHT, Route.Type.ROUTE, generator);
                        break;
                    }
                    case R.id.tv_lefttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT, Route.Direction.TOP, Route.Type.ROUTE, generator);

                        break;
                    }

                    case R.id.tv_righttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.RIGHT, Route.Direction.TOP, Route.Type.ROUTE, generator);

                        break;
                    }


                    case R.id.tv_bottomleft: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM, Route.Direction.LEFT, Route.Type.ROUTE, generator);

                        break;
                    }
                    case R.id.tv_bottomright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM, Route.Direction.RIGHT, Route.Type.ROUTE, generator);

                        break;
                    }
                    case R.id.tv_bottomtop: {

                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM, Route.Direction.TOP, Route.Type.ROUTE, generator);

                        break;
                    }


                    case R.id.tv_finishtop: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM, Route.Direction.TOP, generator);
                        break;
                    }
                    case R.id.tv_finishbottom: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.TOP, Route.Direction.BOTTOM, generator);
                        break;
                    }
                    case R.id.tv_finishleft: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.RIGHT, Route.Direction.LEFT, generator);
                        break;
                    }
                    case R.id.tv_finishright: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT, Route.Direction.RIGHT, generator);
                        break;
                    }


                    case R.id.tv_starttop: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.TOP, Route.Direction.BOTTOM, generator);

                        break;
                    }
                    case R.id.tv_startbottom: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM, Route.Direction.TOP, generator);

                        break;
                    }
                    case R.id.tv_startleft: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT, Route.Direction.RIGHT, generator);
                        break;
                    }
                    case R.id.tv_startright: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.RIGHT, Route.Direction.LEFT, generator);
                        break;
                    }


                }

                TileRoute nextTile = null;

                if (r != null) {


                    if (!generator.getConfig().sounds.soundPress.equals(""))
                        r.sound = generator.getConfig().sounds.soundPress;

                    r.configure(generator.getConfig());
                   /*
                    switch (r.getType()) {

                        case ROUTE:
                            r.configure(generator.getConfig());
                            break;
                        case START:
                            r.configure(generator.getConfig());
                            break;
                        case FINISH:
                            r.configure(generator.getConfig());
                            break;


                    }
*/
                    generator.tiles.remove(GeneratorLayoutPath.this.generatorLayout.tile);

                    generator.tiles.add(r);


                    //generator.startRouteConfiguration();
                    generator.getPathBuilderPopup().startRouteConfiguration();
                    //ArrayList<TileRoute> currentRoutes = generator.getPath();
                    //GeneratorLayoutPath.this.generatorLayout.layoutConstruct.refreashLayout(currentRoutes);


                    if (r.getType() == Route.Type.FINISH) {
                        generator.getPathPopup().dissmissPath();
                        return;
                    }

                    TileRoute prevTile = null;
                    Route.Movement movement = r.getDirection();
                    switch (movement) {
                        case LEFTRIGHT:
                        case BOTTOMRIGHT:
                        case TOPRIGHT:


                            nextTile = generator.findTile(r.horizontalPos + 1, r.verticalPos);
                            break;
                        case RIGHTLEFT:
                        case BOTTOMLEFT:
                        case TOPLEFT:
                            nextTile = generator.findTile(r.horizontalPos - 1, r.verticalPos);

                            break;
                        case BOTTOMTOP:
                        case RIGHTTOP:
                        case LEFTTOP:
                            nextTile = generator.findTile(r.horizontalPos, r.verticalPos - 1);
                            break;
                        case LEFTBOTTOM:
                        case RIGHTBOTTOM:
                        case TOPBOTTOM:
                            nextTile = generator.findTile(r.horizontalPos, r.verticalPos + 1);
                            break;
                    }


                    if (nextTile != null) {

                        if (nextTile.getType() != Route.Type.TILE) {
                            prevTile = getPrevTile(r);
                            if (prevTile != null && prevTile.getType() == Route.Type.TILE) {
                                nextTile = prevTile;

                                Route.Direction tmpTo = r.to;

                                r.to = r.from;
                                r.from = tmpTo;

                                Log.d("test", "switched to prev!!!!!!!!!!!!!!!!!");
                            }
                        }

                        GeneratorLayoutPath.this.generatorLayout.tile = nextTile;
                        GeneratorLayoutPath.this.generatorLayout.oldTile = r;
                        GeneratorLayoutPath.this.generatorLayout.tile.setCurrentTile(true);
                        GeneratorLayoutPath.this.generatorLayout.oldTile.setCurrentTile(false);

                        refreashLayout();

                    }
                    generator.getPathPopup().getLayoutControls().refreshLayout();

                    //      initPathLayout(generator, nextTile, r);
                }
            }
        };

        tvBottomLeft.setOnClickListener(listener);
        tvBottomRight.setOnClickListener(listener);
        tvBottomTop.setOnClickListener(listener);

        tvRightTop.setOnClickListener(listener);

        tvLeftRight.setOnClickListener(listener);
        tvLeftTop.setOnClickListener(listener);


        //  tvBlank.setOnClickListener(listener);

        tvFinishBottom.setOnClickListener(listener);
        tvFinishLeft.setOnClickListener(listener);
        tvFinishRight.setOnClickListener(listener);
        tvFinishTop.setOnClickListener(listener);


        tvStartBottom.setOnClickListener(listener);
        tvStartLeft.setOnClickListener(listener);
        tvStartRight.setOnClickListener(listener);
        tvStartTop.setOnClickListener(listener);


    }


    private View initdetailsLayout() {


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generatorLayout.tile.sound == null || generatorLayout.tile.sound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(generatorLayout.tile.sound);
            }
        });

        drawCoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generatorLayout.tile.drawCoin=b;
                Toast.makeText(generator.view.context,"draw coin "+b,Toast.LENGTH_LONG).show();
            }
        });


        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, generator.view.context.getActivityLoader().listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generatorLayout.tile.sound = adapterView.getItemAtPosition(i).toString();
                        btnSound.setText(generatorLayout.tile.sound);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        etSpeedRatio.setmMinValue(0.1f);
        etSpeedRatio.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generatorLayout.tile.speedRatio = value;
                if (generator.view.getGame().getPreview())
                    generator.view.getGame().configRoute(generatorLayout.tile);

            }
        });


        imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.view.context.getSoundsManager().playRawFile("construct");

                TileRoute r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, generatorLayout.tile.horizontalPos, generatorLayout.tile.verticalPos, Route.Direction.TOP, Route.Direction.RIGHT, Route.Type.TILE, generator);
                generator.tiles.remove(generatorLayout.tile);
                generator.tiles.add(r);
                GeneratorLayoutPath.this.generatorLayout.tile = r;
                generatorLayout.setCurrentTile(r);
                //ArrayList<TileRoute> currentRoutes = generator.getPath();
                refreashLayout();

            }
        });

        return layoutDetails;
    }
}
