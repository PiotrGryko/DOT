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
    public DepthFirstSearch firstSearch;

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


    int retries = 0;
    private void generateStagesSetInternal(final int index, final int count, final int widht, final int height, final float percent) {
        if (dialog.isShowing()) {
            dialog.setTitle("generating stage " + index + "/" + count);
            Log.d(TAG, "generating stage " + index + "/" + count + " world "+generator.getLayout().getCurrentWorld().id);
        }
        generator.saveMaze(new Generator.OnSaveListener() {
            @Override
            public void onSaved() {
                if (index >= count) {
                    finishSetGeneration();

                    return;
                } else if (!cancelGeneration) {
                    generateMaze(true, index + 1,widht,height,percent, new Generator.OnSaveListener() {
                        @Override
                        public void onSaved() {
                            generator.view.context.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    generateStagesSetInternal(index + 1, count,widht,height,percent);

                                }
                            });

                        }

                        @Override
                        public void onFailed() {

                        }
                    });

                } else {
                    generator.view.context.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            finishSetGeneration();

                        }
                    });

                }


            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void generateStagesSet(final int stagesCount, final int widht, final int height, final float percent) {
        cancelGeneration = false;

        if (generator.getLayout().getCurrentWorld() == null) {
            Toast.makeText(generator.view.context, "Select world first!", Toast.LENGTH_LONG).show();
            return;
        }

        generator._id=null;

        final int index = 0;
        initDialog();

        generator.view.setDrawing(false);

        generateMaze(true, index,widht,height,percent, new Generator.OnSaveListener() {
            @Override
            public void onSaved() {
                generator.view.context.getHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        generateStagesSetInternal(index, stagesCount,widht,height,percent);

                    }
                });

            }

            @Override
            public void onFailed() {

            }
        });


    }

    private String randomColor() {
        int color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        return "#" + Integer.toHexString(color);
    }

    public void generateRandomStage() {
        generator._id=null;

        generateMaze(false, 0,1+random.nextInt(30),1+random.nextInt(30),0.8f, null);
    }

    private void generateMaze(final boolean set, int count, int widht, int height, float percent, final Generator.OnSaveListener onSaveListener) {

        if (firstSearch.isRunning)
            return;

        int x = widht;
        int y = height;



        String routeColor = randomColor();
        String dotColor = randomColor();

        if (Util.isColorDark(routeColor) && Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, true);
        } else if (!Util.isColorDark(routeColor) && !Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, false);

        }

        float dotShinning = 1.25f + 1.25f*random.nextFloat();
        float dotDistance = dotShinning-1.25f;

        generator.getConfig().colors.colorBackground = randomColor();
        generator.getConfig().colors.colorExplosionEnd = randomColor();
        generator.getConfig().colors.colorExplosionStart = randomColor();
        generator.getConfig().colors.colorShip = dotColor;
        generator.getConfig().colors.colorFence = randomColor();
        generator.getConfig().colors.colorFilter = "#000000";

        generator.getConfig().colors.colorRoute = routeColor;
        generator.getConfig().settings.explosionOneLightDistance = random.nextFloat();
        generator.getConfig().settings.explosionOneLightShinning = 2.5f * random.nextFloat();
        generator.getConfig().settings.dotLightDistance=dotDistance;
        generator.getConfig().settings.dotLightShinning=dotShinning;
        generator.refreashMaze();

        final boolean flag = generator.getPreview();
        generator.stopPreview();

        firstSearch.generateMaze(x, y,percent, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if (!set) {


                    generator.view.context.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (flag)
                                generator.stopPreview();
                            generator.refreashMaze();
                            generator.getLayout().refreshControlls();

                            if (flag)
                                generator.startPreview();
                        }
                    });


                }

                if (onSaveListener != null)
                    onSaveListener.onSaved();

                return false;
            }
        }, !set);


    }


}
