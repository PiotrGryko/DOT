package pl.slapps.dot.generator.auto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.Generator;

/**
 * Created by piotr on 27/03/16.
 */
public class AutoGenerator {

    private final static String TAG = AutoGenerator.class.getName();

    private Generator generator;
    private Random random;
    private DepthFirstSearch firstSearch;

    private boolean cancelGeneration = false;


    public AutoGenerator(Generator generator) {
        this.generator = generator;
        this.random = new Random();
        this.firstSearch = new DepthFirstSearch(generator);
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

    private void generateMaze(final boolean set, int count) {

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




        generator.getConfig().colors.colorBackground = randomColor();
        generator.getConfig().colors.colorExplosionEnd = randomColor();
        generator.getConfig().colors.colorExplosionStart = randomColor();
        generator.getConfig().colors.colorShip = dotColor;
        generator.getConfig().colors.colorFence = randomColor();
        generator.getConfig().colors.colorRoute = routeColor;
        generator.getConfig().settings.explosionOneLightDistance = random.nextFloat();
        generator.getConfig().settings.explosionOneLightShinning = 2.5f * random.nextFloat();
        generator.refreashMaze();

        firstSearch.generateMaze(x, y, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (!set) {
                    boolean flag = generator.getPreview();

                    if (flag)
                        generator.stopPreview();
                    generator.refreashMaze();
                    generator.getLayout().refreshControlls();

                    if (flag)
                        generator.startPreview();
                }

                return false;
            }
        });




    }


}
