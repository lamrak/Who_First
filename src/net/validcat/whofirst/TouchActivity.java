package net.validcat.whofirst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TouchActivity extends Activity {
	public static final String LOG_TAG = "TouchActivity";
	// Assume no more than 20 simultaneous touches
	final private static int MAX_TOUCHES = 20;
	// Pool of MarkerViews 
	final private static LinkedList<MarkerView> markersPoll = new LinkedList<MarkerView>();
	// Set of MarkerViews currently visible on the display
	@SuppressLint("UseSparseArrays")
	final private static Map<Integer, MarkerView> activeMarkers = new HashMap<Integer, MarkerView>();
	private static final long ONE_SECOND = 1000;
	private FrameLayout frame;
	// Counter 
	private TextView hintView;
	private TextView contView;
	private Handler handlerCounter = new Handler();
	private boolean isFirstStart = true;
	private boolean isGameEnd = false;
	private int counter = 3;
	private boolean isStopCounter = false;
	// Animation
	private Animation animimation;
	// Random
	private Random rnd;
	private Vibrator vib;
	private long[] patternTouch = {0, 100};
	private long[] patternEnd = {0, 200};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touch_activity);

		rnd = new Random();
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		if (vib.hasVibrator()) {
		    Log.v("Can Vibrate", "YES");
		} else {
		    Log.v("Can Vibrate", "NO");
		}
		// Initialize pool of View.
		initUI();
		initAnimation();

		// Create and set on touch listener
		frame.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN: {
						Log.i(LOG_TAG, "ACTION_DOWN");
						if (isFirstStart) {
							isGameEnd = false;
							Log.i(LOG_TAG, "Start new game");
							isFirstStart = false;
							isStopCounter = false;
							startCounter();
						} else resetCounter();
						
						if (isGameEnd) break;
						// Show new MarkerView
						int pointerIndex = event.getActionIndex();
						int pointerID = event.getPointerId(pointerIndex);
						MarkerView marker = markersPoll.remove();
						if (marker == null) break;
						activeMarkers.put(pointerID, marker);
						marker.setXLoc(event.getX(pointerIndex));
						marker.setYLoc(event.getY(pointerIndex));
						frame.addView(marker);
						vib.vibrate(patternTouch, -1);
						break;
					}
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP: {
						Log.i(LOG_TAG, "ACTION_UP");
						// Remove one MarkerView
						int pointerIndex = event.getActionIndex();
						int pointerID = event.getPointerId(pointerIndex);
						MarkerView marker = activeMarkers.get(pointerID);
						if (marker == null) break;
						activeMarkers.remove(pointerID);
						markersPoll.add(marker);
						frame.removeView(marker);
						
						if (activeMarkers.size() == 0) {
							isGameEnd = false;
							setDeafultState();
							isStopCounter = true; // stop counter
						}
						
						break;
					}
					case MotionEvent.ACTION_MOVE: {
						// Move all currently active MarkerViews
						for (int idx = 0; idx < event.getPointerCount(); idx++) {
							int ID = event.getPointerId(idx);
							MarkerView marker = activeMarkers.get(ID);
							if (marker == null) break;
							marker.setXLoc(event.getX(idx));
							marker.setYLoc(event.getY(idx));
							marker.invalidate();
						}
						break;
					}
					case MotionEvent.ACTION_CANCEL:
						Log.i(LOG_TAG, "ACTION_CANCEL");
				}

				return true;
			}
		});
	}

	private void initAnimation() {
		animimation = AnimationUtils.loadAnimation(this, R.anim.view_animation);
		animimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				contView.setText(String.valueOf(--counter));
			}
		});
	}

	protected void setDeafultState() {
		if (isFirstStart) return;
		Log.i(LOG_TAG, "setDeafultState()");
		// Make marker view inactive
		for (Entry<Integer, MarkerView> entry : activeMarkers.entrySet()) {
			markersPoll.add(entry.getValue());
		}
		activeMarkers.clear();
		// Handle ui views
		hintView.setVisibility(View.VISIBLE);
		contView.setVisibility(View.GONE);
		// reset values to default state
		counter = 3;
		isFirstStart = true;
		isStopCounter = false;
	}

	protected void resetCounter() {
		counter = 3; //TODO
		contView.setText(String.valueOf(counter));
		contView.startAnimation(animimation);
	}

	protected void startCounter() {
		hintView.setVisibility(View.GONE);
		contView.setVisibility(View.VISIBLE);
		contView.setText(String.valueOf(counter));
		contView.startAnimation(animimation);
		
		handlerCounter.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (isStopCounter) {
					setDeafultState();
					return;
				}
				if (counter == 1) {
					contView.startAnimation(animimation);
					selectWinner();
					contView.setVisibility(View.GONE);
					return;
				}
//				contView.setText(String.valueOf(--counter));
				contView.startAnimation(animimation);
				handlerCounter.postDelayed(this, ONE_SECOND);
			}
		}, ONE_SECOND);
	}

	protected void selectWinner() {
		int size = activeMarkers.size();
		if (size == 0) {
			Log.e(LOG_TAG, "Size is zero");
			return;
		}
		int index = rnd.nextInt(size);
		Log.i(LOG_TAG, "Random: " + index);
		int winnerPointerId = 0;
		for (Integer key : activeMarkers.keySet()) {
			if (winnerPointerId == index) {
				winnerPointerId = key;
				break;
			}
			winnerPointerId++;
		}
		Log.i(LOG_TAG, "Random pointerId:" + winnerPointerId);
		// Remove lost markers
		for (Integer key : activeMarkers.keySet()) {
			if (key == winnerPointerId) {
				Log.d(LOG_TAG, "Skip winner. Id=" + winnerPointerId);
				continue;
			}
			frame.removeView(activeMarkers.get(key));
		}
		isGameEnd = true;
		vib.vibrate(patternEnd, -1);
	}

	private void initUI() {
		// find ui 
		frame = (FrameLayout) findViewById(R.id.frame);
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

		void setXLoc(float x) {this.x = x;}
		void setYLoc(float y) {this.y = y;}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawCircle(x, y, size, mPaint);
		}
	}

}