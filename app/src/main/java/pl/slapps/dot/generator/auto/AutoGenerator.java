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
import pl.slapps.dot.drawing.Util;
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

    private void finishSetGeneration() {

        if (dialog.isShowing())
            dialog.dismiss();
        generator.view.setDrawing(true);

        boolean flag = generator.getPreview();

        if (flag)
            generator.stopPreview();
        generator.refreashMaze();
        generator.getLayout().refreshControlls();

        if (flag)
            generator.startPreview();
    }


    private void generateStagesSetInternal(final int index, final int count) {
        if (dialog.isShowing()) {
            dialog.setTitle("generating stage " + index + "/" + count);
            Log.d(TAG, "generating stage " + index + "/" + count);
        }
        generator.saveMaze(new Generator.OnSaveListener() {
            @Override
            public void onSaved() {
                if (index >= count) {
                    finishSetGeneration();

                    return;
                } else if (!cancelGeneration) {
                    generateMaze(true, index + 1);
                    generateStagesSetInternal(index + 1, count);
                } else {
                    finishSetGeneration();

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

        generateMaze(true, index);
        generateStagesSetInternal(index, stagesCount);


    }

    private String randomColor() {
        int color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        return "#" + Integer.toHexString(color);
    }

    public void generateRandomStage() {
        generateMaze(false, 0);
    }

    private void generateMaze(boolean set, int count) {

        int step = random.nextInt(20);
        int x = 3;
        int y = 3;

        if (set) {
            step = 0;
            if (count > 0)
                step = (int) Math.sqrt(count);

            if (step == 0)
                step = 1;
            if (step / 2 > 0) {
                x = x + step / 2 + random.nextInt(step / 2);
                y = y + step / 2 + random.nextInt(step / 2);
            } else {
                x = x + step;
                y = y + step;
            }

        } else {
            x = x + step;
            y = y + step;
        }


        String routeColor = randomColor();
        String dotColor = randomColor();

        if (Util.isColorDark(routeColor) && Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, true);
        } else if (!Util.isColorDark(routeColor) && !Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, false);

        }


        generator.initGrid(x, y);
        generator.getConfig().colors.colorBackground = randomColor();
        generator.getConfig().colors.colorExplosionEnd = randomColor();
        generator.getConfig().colors.colorExplosionStart = randomColor();
        generator.getConfig().colors.colorShip = dotColor;
        generator.getConfig().colors.colorFence = randomColor();
        generator.getConfig().colors.colorRoute = routeColor;
        generator.getConfig().settings.explosionOneLightDistance = random.nextFloat();
        generator.getConfig().settings.explosionOneLightShinning = 2.5f * random.nextFloat();


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

        if (!set) {
            boolean flag = generator.getPreview();

            if (flag)
                generator.stopPreview();
            generator.refreashMaze();
            generator.getLayout().refreshControlls();

            if (flag)
                generator.startPreview();
        }

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
