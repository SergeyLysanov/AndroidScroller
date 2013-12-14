package com.example.viewgroupexample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CustomViewGroup extends ViewGroup {
	
	//Gestures
	private float firstX;
	private float firstY;

	private int mMinScrollX;

	private Scroller mScroller;
	private GestureDetector mGD;
	
	//colors
	private int firstColor = Color.BLUE;
	private int secondColor = Color.RED;
	private int thirdColor = Color.GREEN;
	private int fourthColor = Color.MAGENTA;
	
	//attributes
	private int marginRight = 0;

	public CustomViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);

		mGD = new GestureDetector(getContext(), mGestureListener);
		mScroller = new Scroller(getContext());
		setWillNotDraw(false);
		
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
		marginRight = (int) arr.getDimension(R.styleable.SlidingMenu_rightMargin, marginRight);
		arr.recycle();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final View child1 = getChildAt(0);
		child1.layout(left, top, right, bottom);
		child1.setBackgroundColor(firstColor);
		
		final View child2 = getChildAt(1);
		child2.layout(left - right, top, left, bottom);
		child2.setBackgroundColor(secondColor);

		mMinScrollX = left - right + marginRight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.v("TOUCH", "onInterceptTouchEvent ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			Log.v("TOUCH", "onInterceptTouchEvent ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}

		return false;
		// return super.onInterceptTouchEvent(ev);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		/*int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.v("TOUCH", "onTouchEvent ACTION_DOWN");
			firstX = event.getX();
			firstY = event.getY();
			return true;

			// break;
		case MotionEvent.ACTION_MOVE:
			Log.v("TOUCH", "onTouchEvent ACTION_MOVE");

			float x = event.getX();
			float y = event.getY();

			float dX = x - firstX;
			float dY = y - firstY;

			if (Math.abs(dX) > Math.abs(dY)) {
				if (Math.abs(dX) > ViewConfiguration.getTouchSlop()) {
					Log.v("TOUCH", "Horizontal scroll");

					//mGD.onTouchEvent(event);
					// getChildAt(0).setLeft((int)x);
					// getChildAt(1).setRight((int)x);
					return true;
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}*/

		return mGD.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mScroller.computeScrollOffset()) {
	    	//Log.d("GEST", "onDraw X " + Integer.toString(mScroller.getCurrX()));
	        scrollTo(mScroller.getCurrX(), 0);
	    }
	}

	private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onDown(MotionEvent e) {
			if (!mScroller.isFinished()) { // is flinging
				mScroller.forceFinished(true); // to stop flinging on touch
			}
			//Log.d("GEST", "onDown");
			return true; // else won't work
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			Log.d("GEST", "onSingleTapConfirmed");
			int scrollX = Math.abs(getScrollX());
			int tapX = (int)e.getX();
			
			if(tapX > scrollX){
				///Log.d("GEST", "Tap first view");
				View firstView = getChildAt(0);
				ColorDrawable drawable = (ColorDrawable)firstView.getBackground();
				if(drawable.getColor() == firstColor){
					firstView.setBackgroundColor(thirdColor);
				}
				else{
					firstView.setBackgroundColor(firstColor);
				}
			}
			else{
				///Log.d("GEST", "Tap second view");
				View firstView = getChildAt(1);
				ColorDrawable drawable = (ColorDrawable)firstView.getBackground();
				if(drawable.getColor() == secondColor){
					firstView.setBackgroundColor(fourthColor);
				}
				else{
					firstView.setBackgroundColor(secondColor);
				}
			}
			
			return true;
		};
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			
			//Log.d("GEST", "onFling");
			//Log.d("GEST", "velocityX" + Float.toString(velocityY));

			mScroller.fling(getScrollX(), getScrollY(),
	                -(int)velocityX,-(int)velocityY, mMinScrollX, 0, 0, 0);
			invalidate();
		    
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//Log.d("GEST", "onScroll");
			//Log.d("GEST", "distanceX " + distanceX);
			//Log.d("GEST", "getX " + getScrollX());
			
			// beware, it can scroll to infinity
			if(distanceX + getScrollX() > 0){ //close left menu distanceX < 0
				distanceX = -getScrollX();
			}
			if(distanceX + getScrollX() < mMinScrollX){ //open left menu
				distanceX = -getScrollX() + mMinScrollX;
			}
			
			scrollBy((int)distanceX, 0);
			
			return true;
		}
	};
	
}
