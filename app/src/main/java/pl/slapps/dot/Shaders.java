package pl.slapps.dot;

import android.os.Build;

/**
 * Created by piotr on 21/04/16.
 */
public class Shaders {


    public static String getVertexShaderCode() {
        if (Build.VERSION.SDK_INT >= 21)
            return h_vertexShaderCode;
            //  else if (Build.VERSION.SDK_INT >= 17)
            //      return m_vertexShaderCode;
        else
            return l_vertexShaderCode;
    }

    public static String getFragmentShaderCode() {
        if (Build.VERSION.SDK_INT >= 21)
            return h_fragmentShaderCode;
            //  else if (Build.VERSION.SDK_INT >= 17)
            //      return m_fragmentShaderCode;
        else
            return l_fragmentShaderCode;
    }


    public static final String pointVertexShader =
            "uniform mat4 uMVPMatrix;      "
                    + "attribute vec4 vPosition;     "
                    + "attribute float vSize;     "
                    + "void main()                    "
                    + "{                              "

                    + "   gl_Position = uMVPMatrix   "
                    + "               * vPosition;   "
                    + "   gl_PointSize = vSize;         "
                    + "}                              ";

    public static final String pointFragmentShader =
            "precision mediump float;       " +
                    "uniform lowp vec4 vColor;          // This is the color from the vertex shader interpolated across the\n"

                    + "void main()                    "
                    + "{                              "
                    + "   gl_FragColor = vColor;      "
                    + "}                              ";


    private final static String h_vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    // A constant representing the combined model/generator/projection matrix
                    "attribute vec4 vPosition;     " +
                    "uniform lowp vec4 vColor;" +
                    "uniform lowp vec4 vColorFilter;" +

                    "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +

                    // Per-vertex position information we will pass in.

                    // Per-vertex color information we will pass in.
                    "varying vec3 v_Position;       " +
                    "varying vec4 v_Color;       " +


                    // This will be passed into the fragment shader.
                    // This will be passed into the fragment shader.


                    // The entry point for our vertex shader.
                    "void main()" +
                    "{" +
                    // Transform the vertex into eye space.
                    "    v_Position = vec3(vPosition);" +
                    "v_Color=vColor+vColorFilter;" +
                    "v_TexCoordinate = a_TexCoordinate;" +
                    // Pass through the color.

                    // gl_Position is a special variable used to store the final position.
                    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates. +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final static String h_fragmentShaderCode =

            "precision lowp float;" +
                    "struct LightSource" +
                    "{" +
                    "lowp vec3 u_LightPos;" +
                    "lowp float lightShinning;" +
                    "lowp float lightDistance;" +
                    "lowp vec4 lightColor;" +
                    "};" +

                    "uniform LightSource lights[5];" +


                    "varying lowp vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "varying vec4 v_Color;       " +

                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +

                    "uniform lowp vec3 dotLightPos;" +
                    "uniform lowp float dotlightShinning;" +
                    "uniform lowp float dotlightDistance;" +

                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{" +


                    "   lowp float dotDistance = length(lights[0].u_LightPos - v_Position);\n" +
                    "   lowp float dotDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * lights[0].lightDistance)));\n" +

                    "   lowp float explosionOneDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   lowp float explosionOneDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + 0.025 *(  explosionOneDistance* lights[1].lightDistance)));\n" +
                    "   lowp vec4 explosionOneResult = explosionOneDiffuse * lights[1].lightColor;" +

                    "   lowp float explosionTwoDistance = length(lights[2].u_LightPos - v_Position);\n" +
                    "   lowp float explosionTwoDiffuse =  lights[2].lightShinning * (1.0 / (1.0 + 0.025 *( explosionTwoDistance* lights[2].lightDistance)));\n" +
                    "   lowp vec4 explosionTwoResult = explosionTwoDiffuse * lights[2].lightColor;" +
/*
                    "   lowp float explosionThreeDistance = length(lights[3].u_LightPos - v_Position);\n" +
                    "   lowp float explosionThreeDiffuse =  lights[3].lightShinning * (1.0 / (1.0 + 0.025 *(  explosionThreeDistance* lights[3].lightDistance)));\n" +
                    "   lowp vec4 explosionThreeResult = explosionThreeDiffuse * lights[3].lightColor;" +

                    "   lowp float explosionFourDistance = length(lights[4].u_LightPos - v_Position);\n" +
                    "   lowp float explosionFourDiffuse =  lights[4].lightShinning * (1.0 / (1.0 + 0.025 *( explosionFourDistance* lights[4].lightDistance)));\n" +
                    "   lowp vec4 explosionFourResult = explosionFourDiffuse * lights[4].lightColor;" +
*/
                    //"    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate)+v_Color)*dotDiffuse +explosionOneResult + explosionTwoResult;" +// +explosionThreeResult + explosionFourResult;" +
                    //"    gl_FragColor = v_Color*dotDiffuse+texture2D(u_Texture, v_TexCoordinate);"+
                    "}";


    private final static String m_vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    "struct LightSource" +
                    "{" +
                    "lowp vec3 u_LightPos;" +
                    "lowp float lightShinning;" +
                    "lowp float lightDistance;" +
                    "lowp vec4 lightColor;" +
                    "};" +

                    "uniform lowp vec4 vColor;" +
                    "uniform lowp vec4 vColorFilter;" +
                    "uniform LightSource lights[3];" +

                    "attribute vec4 vPosition;     " +


                    "varying lowp vec4 color;" +


                    "varying vec3 v_Position;" +
                    "varying lowp vec3 v_lightPos;" +
                    "varying lowp float v_lightShinning;" +
                    "varying lowp float v_lightDistance;" +


                    "void main()" +
                    "{" +

                    "v_Position = vec3(vPosition);" +
                    "v_lightPos=lights[0].u_LightPos;" +
                    "v_lightShinning=lights[0].lightShinning;" +
                    "v_lightDistance=lights[0].lightDistance;" +


                    "   lowp float explosionOneDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   lowp float explosionOneDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + 0.015 *  explosionOneDistance* lights[1].lightDistance));\n" +
                    "   lowp vec4 explosionOneResult = explosionOneDiffuse * lights[1].lightColor;" +


                    "   lowp float explosionTwoDistance = length(lights[2].u_LightPos - v_Position);\n" +
                    "   lowp float explosionTwoDiffuse =  lights[2].lightShinning * (1.0 / (1.0 + 0.015 * explosionTwoDistance* lights[2].lightDistance));\n" +
                    "   lowp vec4 explosionTwoResult = explosionTwoDiffuse * lights[2].lightColor;" +

                    // "   lowp float dotDistance = length(lights[0].u_LightPos - vPosition);" +
                    // "   lowp float dotDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * lights[0].lightDistance)));" +
                    "color = vColor+vColorFilter+explosionOneResult+explosionTwoResult;" +

                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final static String m_fragmentShaderCode =

            "precision lowp float;" +

                    "varying lowp vec4 color;" +


                    "varying lowp vec3 v_Position;" +
                    "varying lowp vec3 v_lightPos;" +
                    "varying lowp float v_lightShinning;" +
                    "varying lowp float v_lightDistance;" +

                    "void main()" +
                    "{" +

                    "   lowp float dotDistance = length(v_lightPos - v_Position);" +
                    "   lowp float dotDiffuse =  v_lightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * v_lightDistance)));" +

                    "    gl_FragColor = color*dotDiffuse;" +
                    "}";


    private final static String l_vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    "struct LightSource" +
                    "{" +
                    "lowp vec3 u_LightPos;" +
                    "lowp float lightShinning;" +
                    "lowp float lightDistance;" +
                    "lowp vec4 lightColor;" +
                    "};" +

                    "uniform lowp vec4 vColor;" +
                    "uniform lowp vec4 vColorFilter;" +
                    "uniform LightSource lights[3];" +

                    "attribute vec4 vPosition;     " +


                    "varying lowp vec4 color;" +


                    "varying vec3 v_Position;" +
                    "varying lowp vec3 v_lightPos;" +
                    "varying lowp float v_lightShinning;" +
                    "varying lowp float v_lightDistance;" +


                    "void main()" +
                    "{" +

                    "v_Position = vec3(vPosition);" +
               /*+

                    "   lowp float dotDistance = length(lights[0].u_LightPos - vPosition);" +
                    "   lowp float dotDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * lights[0].lightDistance)));" +
                 */
                    "color = vColor;" +//*dotDiffuse+vColorFilter+explosionOneResult+explosionTwoResult;" +

                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final static String l_fragmentShaderCode =

            "precision lowp float;" +

                    "varying lowp vec4 color;" +


                    "varying lowp vec3 v_Position;" +
                    "varying lowp vec3 v_lightPos;" +
                    "varying lowp float v_lightShinning;" +
                    "varying lowp float v_lightDistance;" +

                    "void main()" +
                    "{" +

                    "    gl_FragColor = color;" +
                    "}";


}
