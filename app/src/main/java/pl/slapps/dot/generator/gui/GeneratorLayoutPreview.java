package pl.slapps.dot.generator.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;

/**
 * Created by piotr on 15/02/16.
 */
public class GeneratorLayoutPreview {

    private String TAG = GeneratorLayoutPreview.class.getName();
    private View layoutPreview;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    public View getLayout() {
        return layoutPreview;
    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutPreview = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_preview, null);

        ImageView btnPlay = (ImageView) layoutPreview.findViewById(R.id.btn_play);
        ImageView btnStop = (ImageView) layoutPreview.findViewById(R.id.btn_stop);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.startPreview();


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.stopPreview();


            }
        });

    }


}
