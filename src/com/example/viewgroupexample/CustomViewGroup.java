package com.example.viewgroupexample;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
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
	private float firstX;
	private float firstY;

	private int mMaxScrollX;
	private int mMinScrollX;

	private Scroller mScroller;
	private GestureDetector mGD;

	public CustomViewGroup(Context context) {
		super(context);
		mGD = new GestureDetector(getContext(), mGestureListener);
		mScroller = new Scroller(getContext());
		// TODO Auto-generated constructor stub
	}

	public CustomViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);

		mGD = new GestureDetector(getContext(), mGestureListener);
		mScroller = new Scroller(getContext());
	}

	public CustomViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGD = new GestureDetector(getContext(), mGestureListener);
		mScroller = new Scroller(getContext());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final View child1 = getChildAt(0);
		child1.layout(left, top, right, bottom);

		final View child2 = getChildAt(1);
		child2.layout(left - right, top, left, bottom);

		mMinScrollX = left - right;
		mMaxScrollX = right;
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
		int action = event.getAction();

		mGD.onTouchEvent(event);
		
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
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		// scrollTo invalidates, so until animation won't finish it will be
		// called
		// (used after a Scroller.fling() )
		if (mScroller.computeScrollOffset()) {
			getChildAt(0).setLeft(mScroller.getCurrX());
			getChildAt(1).setRight(mScroller.getCurrX());
			// scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		}
	}

	private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			if (!mScroller.isFinished()) { // is flinging
				mScroller.forceFinished(true); // to stop flinging on touch
			}
			return true; // else won't work
		}

		// ...
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			mScroller.fling(getScrollX(), getScrollY(), -(int) velocityX,
					-(int) velocityY, mMinScrollX, mMaxScrollX, 0, 0);
			invalidate(); // don't remember if it's needed
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// beware, it can scroll to infinity
			// scrollBy((int)distanceX, (int)distanceY);
			return true;
		}
	};
}
