/*
 * Copyright (C) 2014 emmasuzuki <emma11suzuki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.emmasuzuki.quickreturnlistview.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

/**
 * QuickReturnListView for API level 9+
 * This class take AbsListView for list view and any View for quick return view.
 *
 * Created by emmasuzuki on 7/21/14.
 */
public class QuickReturnListView extends FrameLayout {

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;

    // Accessible variables
    private AbsListView mAbsListView;
    private View mQuickReturnView;

    private boolean mQuickReturnEnabled = true;
    private boolean mFlingEnabled = true;
    private int mSettleAnimationDuration = 300;

    // Internally used variables
    private int mState = STATE_ONSCREEN;
    private int mQuickReturnHeight;
    private int mPrevY, mDeltaY;
    private boolean mSettleEnabled, mAllItemsVisible;

    private ScrollSettleHandler mScrollSettleHandler;
    private GestureDetectorCompat mGestureListener;

    public QuickReturnListView(Context context) {
        super(context);
    }

    public QuickReturnListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuickReturnListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // ---------------- Public API ---------------- //

    public AbsListView getAbsListView() {
        return mAbsListView;
    }

    public View getQuickReturnView() {
        return mQuickReturnView;
    }

    public void setAdapter(ListAdapter adapter) {
        // mAbsListView.setAdapter will produce NoSuchMethodException.
        // Workaround is to cast to AdapterView
        ((AdapterView<ListAdapter>) mAbsListView).setAdapter(adapter);

        updateAllItemVisible();
    }

    public void setQuickReturnEnabled(boolean quickReturnEnabled) {
        mQuickReturnEnabled = quickReturnEnabled;
    }

    public void setFlingEnabled(boolean flingEnabled) {
        mFlingEnabled = flingEnabled;
    }

    public void setSettleAnimationDuration(int duration) {
        mSettleAnimationDuration = duration;
    }

    // ---------------- Public API ---------------- //

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isViewValid()) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view instanceof AbsListView) {
                    mAbsListView = (AbsListView) view;
                } else {
                    mQuickReturnView = view;
                }
            }

            // Make sure quick return view is on front
            mQuickReturnView.bringToFront();

            initialize();
        } else {
            throw new IllegalArgumentException("QuickReturnListView requires to have only 1 quick return view and 1 AbsListView");
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSate = super.onSaveInstanceState();

        // Preserve quick return state for after device rotation
        SavedState state = new SavedState(superSate);
        state.mState = mState;

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());

            // Move quick return view from restored state
            mState = savedState.mState;

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // If items are all visible or first item is visible, do nothing for quick return view
        if (mAllItemsVisible || !mQuickReturnEnabled) {
            return super.dispatchTouchEvent(ev);
        }

        mGestureListener.onTouchEvent(ev);

        if (mAbsListView.getFirstVisiblePosition() == 0) {
            mState = STATE_ONSCREEN;
            animateQuickReturnViewToDest(0);

        } else {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mSettleEnabled = false;
                    mPrevY = (int) ev.getY();

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Settle quick return view with animation
                    mSettleEnabled = true;
                    mScrollSettleHandler.onScroll();

                    break;

                case MotionEvent.ACTION_MOVE:
                    // Move quick return view according to touch move
                    mDeltaY = (int) ev.getY() - mPrevY;

                    handleMove();

                    mPrevY = (int) ev.getY();

                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private GestureListener.OnFlingListener mOnFlingListener = new GestureListener.OnFlingListener() {

        @Override
        public void onFling(float velocityY) {
            if(!mFlingEnabled) {
                return;
            }

            final int destY;

            // If fling to down, show quick return view completely
            if(velocityY > 0) {
                destY = 0;
                mState = STATE_ONSCREEN;

                // Else hide completely
            } else {
                destY = -mQuickReturnHeight;
                mState = STATE_OFFSCREEN;
            }

            animateQuickReturnViewToDest(destY);
        }
    };

    private void initialize() {
        mScrollSettleHandler = new ScrollSettleHandler();
        mGestureListener = new GestureDetectorCompat(getContext(), new GestureListener(mOnFlingListener));

        mScrollSettleHandler.setOnSettleHandlerListener(new ScrollSettleHandler.OnSettleHandlerListener() {

            @Override
            public void onHandleMessage() {
                // If first item is visible, do nothing
                if (mSettleEnabled && mState == STATE_RETURNING) {
                    final int destY;

                    // If more than half of quick return view is on screen, settle to show completely
                    if (mQuickReturnView.getTop() > getTop() - mQuickReturnHeight / 2) {
                        destY = 0;
                        mState = STATE_ONSCREEN;

                        // Else settle to hide completely
                    } else {
                        destY = -mQuickReturnHeight;
                        mState = STATE_OFFSCREEN;
                    }

                    animateQuickReturnViewToDest(destY);
                }
            }
        });

        refreshViews();
    }

    private void refreshViews() {
        updateAllItemVisible();

        mQuickReturnView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mQuickReturnHeight = mQuickReturnView.getHeight();

                // Add padding on top so that quick return view would not overlap on top of list view
                mAbsListView.setPadding(
                        mAbsListView.getPaddingLeft(),
                        mQuickReturnHeight + mAbsListView.getPaddingTop(),
                        mAbsListView.getPaddingRight(),
                        mAbsListView.getPaddingBottom());

                setQuickReturnPositionFromState();

                if (Build.VERSION.SDK_INT > 15) {
                    mQuickReturnView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mQuickReturnView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void animateQuickReturnViewToDest(final int destY) {
        // Pre-honeycomb style
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destY - mQuickReturnView.getTop());
        animation.setFillEnabled(true);
        animation.setDuration(mSettleAnimationDuration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Noop
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TranslateAnimation does not change view's position, so
                // after the animation end, manually set quick return view position to destination
                setQuickReturnViewY(destY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Noop
            }
        });

        mQuickReturnView.startAnimation(animation);
    }

    private void setQuickReturnPositionFromState() {
        if (mState == STATE_ONSCREEN) {
            setQuickReturnViewY(0);
        } else if (mState == STATE_OFFSCREEN) {
            setQuickReturnViewY(-mQuickReturnHeight);
        }
    }

    private void handleMove() {
        switch (mState) {
            case STATE_OFFSCREEN:
                if (mDeltaY >= 0) {
                    mState = STATE_RETURNING;
                }

                break;

            case STATE_ONSCREEN:
                if (mDeltaY <= 0) {
                    mState = STATE_RETURNING;
                }

                break;

            case STATE_RETURNING:
                if (mQuickReturnView.getTop() < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN;
                }

                if (mQuickReturnView.getTop() > 0) {
                    mState = STATE_ONSCREEN;
                }

                int destY = mQuickReturnView.getTop() + mDeltaY;

                // Y position for mQuickReturnView must be between -mQuickReturnViewHeight and 0
                destY = Math.min(0, Math.max(-mQuickReturnHeight, destY));

                // Cancel currently running animation and set new position
                if (mQuickReturnView.getAnimation() != null) {
                    mQuickReturnView.getAnimation().cancel();
                }

                setQuickReturnViewY(destY);

                break;
        }
    }

    /**
     * Check if QuickReturnListView has valid child layout
     * QuickReturnListView requires to have exactly 1 quick return view and 1 AbsListView as children
     */
    private boolean isViewValid() {
        return getChildCount() == 2;
    }

    private void updateAllItemVisible() {
        if (mAbsListView.getAdapter() != null) {
            // True if list items are all visible
            mAllItemsVisible = mAbsListView.getLastVisiblePosition() >= mAbsListView.getAdapter().getCount() - 1;
        }
    }

    /**
     * Set quick return view's Y position.  pre-honeycomb style.
     */
    private void setQuickReturnViewY(int y) {
        LayoutParams layoutParams = new LayoutParams(
                mQuickReturnView.getWidth(), mQuickReturnHeight);
        layoutParams.topMargin = y;
        mQuickReturnView.setLayoutParams(layoutParams);
    }

    /**
     * Scroll handler to send message on scroll settled
     */
    private static class ScrollSettleHandler extends Handler {

        private OnSettleHandlerListener mListener;

        public void onScroll() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        public void setOnSettleHandlerListener(OnSettleHandlerListener listener) {
            mListener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListener != null) {
                mListener.onHandleMessage();
            }
        }

        public interface OnSettleHandlerListener {
            public void onHandleMessage();
        }
    }

    /**
     * Fling detector
     */
    private static class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int MIN_VELOCITY_Y = 100;

        private OnFlingListener mListener;

        public GestureListener(OnFlingListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(Math.abs(velocityY) > MIN_VELOCITY_Y) {
                mListener.onFling(velocityY);

                return true;
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        public interface OnFlingListener {
            public void onFling(float velocityY);
        }
    }

    private static class SavedState extends BaseSavedState {

        private int mState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            mState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(mState);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
