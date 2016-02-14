package pl.slapps.dot.generator.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Stage;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutStrings {

    private String TAG = GeneratorLayoutStrings.class.getName();
    private View layoutStrings;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    /*
        strings layout elements
    */
    private EditText etName;
    private EditText etDesc;


    public View getLayout()
    {
        return layoutStrings;
    }


    public void refreashLayout(Stage stage) {
        etName.setText(stage.name);
        etDesc.setText(stage.description);
    }

    public void initLayout(GeneratorLayout generatorLayout) {

        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;


        layoutStrings = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_strings,null);

        etName = (EditText) layoutStrings.findViewById(R.id.et_name);
        etDesc = (EditText) layoutStrings.findViewById(R.id.et_desc);
        ImageView btnSave = (ImageView) layoutStrings.findViewById(R.id.btn_ok);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textName = etName.getText().toString();
                String textDesc = etDesc.getText().toString();


                generator.name = textName;
                generator.description = textDesc;
            }
        });


    }
}
