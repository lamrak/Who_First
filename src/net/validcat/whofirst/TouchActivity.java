package net.validcat.whofirst;

import java.util.LinkedList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TouchActivity extends Activity {
	public static final String LOG_TAG = "TouchActivity";
	private static final int MIN_DXDY = 2;
	// Assume no more than 20 simultaneous touches
	final private static int MAX_TOUCHES = 20;
	// Pool of MarkerViews 
	final private static LinkedList<MarkerView> markersPoll = new LinkedList<MarkerView>();
	// Set of MarkerViews currently visible on the display
	final private static SparseArray<MarkerView> activeMarkers = new SparseArray<MarkerView>();
	private static final long ONE_SECOND = 1000;
	private FrameLayout mFrame;
	// Counter 
	private TextView hintView;
	private TextView contView;
	private Handler handlerCounter = new Handler();
	private boolean isFirstStart = true;
	private int counter = 3;
	private boolean isBreak = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touch_activity);
		mFrame = (FrameLayout) findViewById(R.id.frame);

		// Initialize pool of View.
		initUI();

		// Create and set on touch listener
		mFrame.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN: {
						if (isFirstStart) {
							isFirstStart = false;
							startCounter();
						} else 
							resetCounter();
						// Show new MarkerView
						int pointerIndex = event.getActionIndex();
						int pointerID = event.getPointerId(pointerIndex);
	
						MarkerView marker = markersPoll.remove();
	
						if (null != marker) {
							activeMarkers.put(pointerID, marker);
							marker.setXLoc(event.getX(pointerIndex));
							marker.setYLoc(event.getY(pointerIndex));
							mFrame.addView(marker);
						}
						break;
					}
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP: {
						// Remove one MarkerView
						int pointerIndex = event.getActionIndex();
						int pointerID = event.getPointerId(pointerIndex);
						MarkerView marker = activeMarkers.get(pointerID);
						activeMarkers.remove(pointerID);
						if (null != marker) {
							markersPoll.add(marker);
							mFrame.removeView(marker);
						}
						
						if (activeMarkers.size() == 0)
							setDeafultState();
						break;
					}
					case MotionEvent.ACTION_MOVE: {
						// Move all currently active MarkerViews
						for (int idx = 0; idx < event.getPointerCount(); idx++) {
							int ID = event.getPointerId(idx);
							MarkerView marker = activeMarkers.get(ID);
							if (null != marker) {
								// Redraw only if finger has travel ed a minimum distance   
								if (Math.abs(marker.getXLoc() - event.getX(idx)) > MIN_DXDY
										|| Math.abs(marker.getYLoc() - event.getY(idx)) > MIN_DXDY) {
									// Set new location and redraw
									marker.setXLoc(event.getX(idx));
									marker.setYLoc(event.getY(idx));
									marker.invalidate();
								}
							}
						}
						break;
					}
				}

				return true;
			}
		});
	}

	protected void setDeafultState() {
		hintView.setVisibility(View.VISIBLE);
		contView.setVisibility(View.GONE);
		
		counter = 3;
		isFirstStart = true;
		isBreak = true;
	}

	protected void resetCounter() {
		counter = 3; //TODO
		contView.setText(String.valueOf(counter));
	}

	protected void startCounter() {
		hintView.setVisibility(View.GONE);
		contView.setVisibility(View.VISIBLE);
		contView.setText(String.valueOf(counter));
		isBreak = false;
		
		handlerCounter.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (counter == 0 || isBreak) {
					//TODO delete all except one marker
					//TODO vibrate
					contView.setVisibility(View.GONE);
					return;
				}
				contView.setText(String.valueOf(--counter));
				handlerCounter.postDelayed(this, ONE_SECOND);
			}
		}, ONE_SECOND);
	}

	private void initUI() {
		// find ui 
		hintView = (TextView) findViewById(R.id.tv_hint);
		contView = (TextView) findViewById(R.id.tv_count);
		
		setDeafultState();
		
		// create markers
		for (int idx = 0; idx < MAX_TOUCHES; idx++)
			markersPoll.add(new MarkerView(this, -1, -1));
	}

	private class MarkerView extends View {
		private int size = 200;
		private float x, y;
		final private Paint mPaint = new Paint();

		public MarkerView(Context context, float x, float y) {
			super(context);
			this.x = x;
			this.y = y;
			mPaint.setStyle(Style.FILL);
			size = (int) getResources().getDimension(R.dimen.circle_size);
			Random rnd = new Random();
			mPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
		}

		float getXLoc() {return x;}
		float getYLoc() {return y;}
		void setXLoc(float x) {this.x = x;}
		void setYLoc(float y) {this.y = y;}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawCircle(x, y, size, mPaint);
		}
	}

}