package pl.slapps.dot.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


import android.graphics.Color;

import pl.slapps.dot.game.GameView;

public class Background {

	private float[] vertices;
	private float[] textVertices = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
			0.0f };
	private int textures[] = new int[1];
	private FloatBuffer vertBuffer;
	private FloatBuffer textBuffer;
	private GameView view;
	//private Bitmap bitmap;

	private float moveX=0;
	private float moveY=0;
	private float a;
	private float r;
	private float g;
	private float b;
	public String backgroundcolor;

	public void setColor(String color)
	{
		backgroundcolor=color;
		int intColor = Color.parseColor(color);
		a=(float)Color.alpha(intColor)/255;
		r=(float)Color.red(intColor)/255;
		g=(float)Color.green(intColor)/255;
		b=(float)Color.blue(intColor)/255;
	}
	public Background(GameView view, String color) {
		this.view = view;

		setColor(color);


		float[] tmp = { -view.screenWidth/2, 1.5f*view.screenHeight, 0,
				        -view.screenWidth/2, -view.screenHeight/2, 0,
				        1.5f*view.screenWidth, 1.5f*view.screenHeight, 0,
				         1.5f*view.screenWidth, -view.screenHeight/2, 0

		};
		vertices = tmp;
		ByteBuffer vertByte = ByteBuffer.allocateDirect(vertices.length*4);
		vertByte.order(ByteOrder.nativeOrder());
		vertBuffer = vertByte.asFloatBuffer();
		vertBuffer.put(vertices);
		vertBuffer.position(0);

		ByteBuffer textByte = ByteBuffer.allocateDirect(textVertices.length*4);
		textByte.order(ByteOrder.nativeOrder());
	    textBuffer = textByte.asFloatBuffer();
	    textBuffer.put(textVertices);
	    textBuffer.position(0);


	}

	public void setMove(float x, float y)
	{moveX+=x;
	moveY+=y;}

	public void loadTexture(GL10 gl)
	{
		//bitmap =BitmapFactory.decodeResource(view.getResources(), R.drawable.space);

		gl.glGenTextures(1, textures,0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		//GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		//bitmap.recycle();
	}

	public void draw(GL10 gl)
	{

		gl.glLoadIdentity();


		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);


		gl.glColor4f(r, g, b, a);
		gl.glTranslatef(moveX, moveY, 0);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length/3);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);


	}

}
