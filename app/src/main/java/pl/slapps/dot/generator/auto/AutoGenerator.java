package pl.slapps.dot.generator.auto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 27/03/16.
 */
public class AutoGenerator {

    private final static String TAG = AutoGenerator.class.getName();

    private Generator generator;
    private Random random;


    private boolean cancelGeneration = false;


    public AutoGenerator(Generator generator) {
        this.generator = generator;
        this.random = new Random();
    }


    private AlertDialog dialog;

    private void initDialog() {
        dialog = new AlertDialog.Builder(generator.view.context).create();
        dialog.setTitle("Generating stages");

        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelGeneration = true;


            }
        });

        dialog.show();


    }


    private void generateStagesSetInternal(final int index, final int count) {
        if (dialog.isShowing()) {
            dialog.setTitle("generating stage " + index + "/" + count);
            Log.d(TAG,"generating stage "+index +"/"+count);
        }
        generator.saveMaze(new Generator.OnSaveListener() {
            @Override
            public void onSaved() {
                if (index >= count) {
                    if(dialog.isShowing())
                    dialog.dismiss();
                    generator.view.setDrawing(true);

                    return;
                }
                else if (!cancelGeneration) {
                    generateMaze(0, 0, null);
                    generateStagesSetInternal(index + 1, count);
                }
                else
                {
                    dialog.dismiss();
                    generator.view.setDrawing(true);

                }


            }
        });
    }

    public void generateStagesSet(int stagesCount) {
        cancelGeneration = false;
        if (generator.getLayout().getCurrentWorld() == null) {
            Toast.makeText(generator.view.context, "Select world first!", Toast.LENGTH_LONG).show();
            return;
        }

        int index = 0;
        initDialog();

        generator.view.setDrawing(false);

        generateMaze(0, 0, null);
        generateStagesSetInternal(index, stagesCount);


    }

    private String randomColor() {
        int color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        return "#" + Integer.toHexString(color);
    }

    public void generateMaze(int x, int y, Config config) {

        x = 1 + random.nextInt(19);
        y = 1 + random.nextInt(19);


        generator.initGrid(x, y);
        generator.getConfig().colors.colorBackground = randomColor();
        generator.getConfig().colors.colorExplosionEnd = randomColor();
        generator.getConfig().colors.colorExplosionStart = randomColor();
        generator.getConfig().colors.colorShip = randomColor();
        generator.getConfig().colors.colorFence = randomColor();
        generator.getConfig().colors.colorRoute = randomColor();


        int startX = random.nextInt(x);
        int startY = random.nextInt(y);

        TileRoute startRoute = generator.findTile(startX, startY);


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

        boolean flag = generator.getPreview();

        if (flag)
            generator.stopPreview();
        generator.refreashMaze();
        generator.getLayout().refreshControlls();

        if (flag)
            generator.startPreview();


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

    private TileRoute nextStep(TileRoute tileRoute) {


        // int side = random.nextInt(4);


        ArrayList<Route.Direction> usedOptions = new ArrayList<>();
        TileRoute nextTile = null;

        Route.Direction nextDirection = randomDirection(tileRoute, usedOptions);
        nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, nextDirection);

        while (nextDirection != null && (nextTile == null || nextTile.type != Route.Type.TILE)) {
            usedOptions.add(nextDirection);
            nextDirection = randomDirection(tileRoute, usedOptions);
            if (nextDirection != null)
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, nextDirection);
        }

        /*
        switch (side) {
            case 0: {
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.TOP);
                break;
            }
            case 1: {
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.BOTTOM);

                break;
            }
            case 2: {
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.LEFT);

                break;
            }
            case 3: {
                nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.RIGHT);

                break;
            }


        }

        if (nextTile == null || nextTile.type != Route.Type.TILE) {
            nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.TOP);

        }
        if (nextTile == null || nextTile.type != Route.Type.TILE) {
            nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.BOTTOM);

        }
        if (nextTile == null || nextTile.type != Route.Type.TILE) {
            nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.RIGHT);

        }
        if (nextTile == null || nextTile.type != Route.Type.TILE) {
            nextTile = generator.getTileRouteManager().getNeighbour(tileRoute, Route.Direction.LEFT);

        }
*/

        if (nextTile != null && nextTile.type == Route.Type.TILE) {
            tileRoute.to = generator.getTileRouteManager().getTo(tileRoute, nextTile);
            nextTile.from = generator.getTileRouteManager().getOposite(tileRoute.to);

            if (generator.getStartRoute() == null) {

                TileRoute startRoute = generator.getTileRouteManager().getRouteStartFromTile(generator.getTileRouteManager().getOposite(tileRoute.to), tileRoute.to, tileRoute);

                int index = generator.tiles.indexOf(tileRoute);
                generator.tiles.set(index, startRoute);
            } else {
                TileRoute startRoute = generator.getTileRouteManager().getRouteFromTile(tileRoute.from, tileRoute.to, tileRoute);

                int index = generator.tiles.indexOf(tileRoute);
                generator.tiles.set(index, startRoute);
            }
            Log.d("rrr", "step generated ... " + nextTile.horizontalPos + " " + nextTile.verticalPos);

            return nextTile;
        }
        return null;
    }


}
