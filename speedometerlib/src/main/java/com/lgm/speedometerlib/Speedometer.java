package com.lgm.speedometerlib;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Speedometer extends View {
	public static final float DEFAULT_MAX_SPEED = 300;
	private float READING_SIZE = 50f;

	private float mMaxSpeed;
	private float mCurrentSpeed;

	private Paint onPaint;
	private Paint offPaint;
	private Paint readingPaint;

	private int OFF_COLOR = 0xff566a83;

	private int READING_COLOR =0xff566a83;
	private float mCenterX;
	private float mCenterY;
	private int radius;

	private List<Integer> colors = new ArrayList<>();


	public Speedometer(Context context) {
		super(context);
	}

	public Speedometer(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.Speedometer,
				0, 0);
		try {
			mMaxSpeed = a.getFloat(R.styleable.Speedometer_maxSpeed, DEFAULT_MAX_SPEED);
			mCurrentSpeed = a.getFloat(R.styleable.Speedometer_currentSpeed, 0);
			OFF_COLOR = a.getColor(R.styleable.Speedometer_offColor, OFF_COLOR);
			READING_COLOR = a.getColor(R.styleable.Speedometer_textColor, READING_COLOR);
			READING_SIZE = a.getDimension(R.styleable.Speedometer_textSize, READING_SIZE);
		} finally {
			a.recycle();
		}
		initDrawingTools();
	}

	private void initDrawingTools() {

		setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速，开启会导致刻度圆角无效
		onPaint = new Paint();
		onPaint.setStyle(Paint.Style.STROKE);
		onPaint.setAntiAlias(true);
		onPaint.setStrokeJoin(Paint.Join.ROUND);
		onPaint.setStrokeCap(Paint.Cap.ROUND);
		onPaint.setStrokeWidth(10);

		offPaint = new Paint();
		onPaint.setStyle(Paint.Style.STROKE);
		offPaint.setColor(OFF_COLOR);
		offPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		offPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);
		onPaint.setStrokeJoin(Paint.Join.ROUND);
		onPaint.setStrokeCap(Paint.Cap.ROUND);
		offPaint.setAntiAlias(true);


		readingPaint = new Paint();
		readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		offPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
		readingPaint.setTextSize(READING_SIZE);
		readingPaint.setTypeface(Typeface.SANS_SERIF);
		readingPaint.setColor(READING_COLOR);
		readingPaint.setAntiAlias(true);

	}

	public void addColor(Integer color) {
		colors.add(color);
	}


	public float getCurrentSpeed() {
		return mCurrentSpeed;
	}

	public void setCurrentSpeed(float mCurrentSpeed) {
		if (mCurrentSpeed > this.mMaxSpeed)
			this.mCurrentSpeed = mMaxSpeed;
		else if (mCurrentSpeed < 0)
			this.mCurrentSpeed = 0;
		else
			this.mCurrentSpeed = mCurrentSpeed;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {

		if (width > height) {
			radius = height / 2 - 20;
		} else {
			radius = width / 2 - 20;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		mCenterX = chosenDimension / 2;
		mCenterY = chosenDimension / 2;
		setMeasuredDimension(chosenDimension, chosenDimension);
	}

	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		}
	}

	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawScaleBackground(canvas);
		drawScale(canvas);
		drawReading(canvas);
	}

	/**
	 * Draws the segments in their OFF state
	 *
	 * @param canvas
	 */
	private void drawScaleBackground(Canvas canvas) {
		for (float i = -220; i <= 40; i += 10) {
			float[] point1 = getCoordinatePoint(radius, i);
			float[] point2 = getCoordinatePoint(radius - 30, i);
			canvas.drawLine(point1[0], point1[1], point2[0], point2[1], offPaint);
		}
	}

	private void drawScale(Canvas canvas) {
		canvas.save();
		if (colors == null) {
			colors = new ArrayList<>();
		}
		if (colors.isEmpty()) {
			colors.add(0xff59B4D5);
			colors.add(0xfff6d022);
			colors.add(0xffD82400);
		}
		if (colors.size() == 1) {
			colors.add(colors.get(0));
		}
		int interval = 260 / (colors.size() - 1);
		ArgbEvaluator argbEvaluator = new ArgbEvaluator();
		for (int i = -220; i <= (mCurrentSpeed / mMaxSpeed) * 260 - 220; i += 10) {
			int distance = i + 220;
			int index = distance / interval;

			float i1 = (distance % interval) / (interval * 1.0f);
			int evaluate;
			if (index == colors.size() - 1) {
				evaluate = colors.get(index);
			} else {
				evaluate = (Integer) argbEvaluator.evaluate(i1, colors.get(index), colors.get(index + 1));
			}
			onPaint.setColor(evaluate);
			System.out.println(evaluate);
			float[] point1 = getCoordinatePoint(radius, i);
			float[] point2 = getCoordinatePoint(radius - 30, i);
			canvas.drawLine(point1[0], point1[1], point2[0], point2[1], onPaint);
		}
	}

	private void drawReading(Canvas canvas) {

		String s = String.valueOf(mCurrentSpeed);
		String string = new String(s + "km/h");
		float[] widths = new float[string.length()];
		readingPaint.getTextWidths(string, widths);
		float advance = 0;
		for (double width : widths)
			advance += width;

		canvas.drawText(string, 0, string.length(), mCenterX - advance / 2,
				mCenterY, readingPaint);


	}


	/**
	 * 依圆心坐标，半径，扇形角度，计算出扇形终射线终点xy坐标
	 */
	public float[] getCoordinatePoint(int radius, float cirAngle) {
		float[] point = new float[2];

		double arcAngle = Math.toRadians(cirAngle); //将角度转换为弧度
		if (cirAngle < 90) {
			point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
			point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
		} else if (cirAngle == 90) {
			point[0] = mCenterX;
			point[1] = mCenterY + radius;
		} else if (cirAngle > 90 && cirAngle < 180) {
			arcAngle = Math.PI * (180 - cirAngle) / 180.0;
			point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
			point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
		} else if (cirAngle == 180) {
			point[0] = mCenterX - radius;
			point[1] = mCenterY;
		} else if (cirAngle > 180 && cirAngle < 270) {
			arcAngle = Math.PI * (cirAngle - 180) / 180.0;
			point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
			point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
		} else if (cirAngle == 270) {
			point[0] = mCenterX;
			point[1] = mCenterY - radius;
		} else {
			arcAngle = Math.PI * (360 - cirAngle) / 180.0;
			point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
			point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
		}

		return point;
	}
}
