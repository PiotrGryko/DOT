package pl.slapps.dot.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pl.slapps.dot.drawing.Quad;


public class Sprite {

	public float centerX; // used to track sprite position
	public float centerY;
	public float moveX; // used to sprite translation
	public float moveY;
	public float width;
	public float height;
	public float vertices[];
	public short indices[];
	public FloatBuffer bufferedVertex;
	public ShortBuffer bufferedIndices;

	public Quad quad;


	public float x; // used to increas/decreas sprite position
	public float y;


	public Sprite( float centerX, float centerY, float width,
			float height) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;

		quad = new Quad(centerX,centerY,width,height);


		initBuffers();

	}

	public void initBuffers()
	{
		vertices = quad.vertices;
		indices = quad.indices;


		ByteBuffer bytes = ByteBuffer.allocateDirect(vertices.length * 4);
		bytes.order(ByteOrder.nativeOrder());

		bufferedVertex = bytes.asFloatBuffer();
		bufferedVertex.put(vertices);
		bufferedVertex.position(0);

		bytes = ByteBuffer.allocateDirect(indices.length * 2);
		bytes.order(ByteOrder.nativeOrder());

		bufferedIndices = bytes.asShortBuffer();
		bufferedIndices.put(indices);
		bufferedIndices.position(0);
	}


	public float[] getVertices()
	{
		return quad.vertices;
	}
	public short[] getIndices()
	{
		return quad.indices;
	}


	public void setMove(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getMoveX() {
		return x;
	}

	public float getMoveY() {
		return y;
	}

	public void update() {
		moveX += x;
		moveY += y;
		centerX += x;
		centerY += y;

		quad.bottomLeft.update(x, y);
		quad.bottomRight.update(x, y);
		quad.topLeft.update(x, y);
		quad.topRight.update(x, y);
		quad.initSharedVerticles();
		this.bufferedVertex.position(0);
		this.bufferedVertex.put(quad.vertices);
		this.bufferedVertex.position(0);
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

}
