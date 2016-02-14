package pl.slapps.dot.generator.gui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.TileRouteFinish;
import pl.slapps.dot.generator.TileRouteStart;
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

    public View getLayout()
    {
        return layoutRoutes;
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
        }

        if (generatorLayout.oldTile != null) {
            tvStartBottom.setVisibility(View.GONE);
            tvStartLeft.setVisibility(View.GONE);
            tvStartRight.setVisibility(View.GONE);
            tvStartTop.setVisibility(View.GONE);

            Log.d(TAG, "TEST " + generatorLayout.oldTile.getDirection());
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
                    break;


                }

            }
        }

    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;

        layoutRoutes = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_path,null);

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


        refreashLayout();




        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                generator.view.context.getSoundsManager().playRawFile("construct");

                final int id = vi.getId();
                TileRoute r = null;

                switch (id) {
                    case R.id.tv_leftright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE,generator);
                        break;
                    }
                    case R.id.tv_lefttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE,generator);

                        break;
                    }

                    case R.id.tv_righttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE,generator);

                        break;
                    }


                    case R.id.tv_bottomleft: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.LEFT.name(), Route.Type.ROUTE,generator);

                        break;
                    }
                    case R.id.tv_bottomright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE,generator);

                        break;
                    }
                    case R.id.tv_bottomtop: {

                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name(), Route.Type.ROUTE,generator);

                        break;
                    }



                    case R.id.tv_finishtop: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos,  Route.Direction.BOTTOM.name(), Route.Direction.TOP.name(),generator);
                        break;
                    }
                    case R.id.tv_finishbottom: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos,Route.Direction.TOP.name(), Route.Direction.BOTTOM.name(),generator);
                        break;
                    }
                    case R.id.tv_finishleft: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos,  Route.Direction.RIGHT.name(), Route.Direction.LEFT.name(),generator);
                        break;
                    }
                    case R.id.tv_finishright: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos,  Route.Direction.LEFT.name(), Route.Direction.RIGHT.name(),generator);
                        break;
                    }


                    case R.id.tv_starttop: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.BOTTOM.name(),generator);

                        break;
                    }
                    case R.id.tv_startbottom: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name(),generator);

                        break;
                    }
                    case R.id.tv_startleft: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name(),generator);
                        break;
                    }
                    case R.id.tv_startright: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, GeneratorLayoutPath.this.generatorLayout.tile.horizontalPos, GeneratorLayoutPath.this.generatorLayout.tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.LEFT.name(),generator);
                        break;
                    }




                }

                TileRoute nextTile = null;

                if (r != null) {



                    if (!generator.routeSound.equals(""))
                        r.sound = generator.routeSound;
                    switch (r.getType()) {
                        case BLOCK:
                            r.setRouteColor(generator.blockColor);
                            break;
                        case ROUTE:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case START:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case FINISH:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case FILL:
                            r.setRouteColor(generator.fillColor);
                            break;


                    }

                    generator.tiles.remove(GeneratorLayoutPath.this.generatorLayout.tile);

                    generator.tiles.add(r);




                    //generator.startRouteConfiguration();
                    generator.startRouteConfiguration();
                    ArrayList<TileRoute> currentRoutes = generator.getPath();
                    GeneratorLayoutPath.this.generatorLayout.layoutConstruct.refreashLayout(currentRoutes);

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
                        GeneratorLayoutPath.this.generatorLayout.tile = nextTile;
                        GeneratorLayoutPath.this.generatorLayout.oldTile = r;
                        refreashLayout();
                        generatorLayout.refreashCurrentTileLabels();

                    }
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
}
