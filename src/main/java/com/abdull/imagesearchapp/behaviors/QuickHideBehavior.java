package com.abdull.imagesearchapp.behaviors;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.abdull.imagesearchapp.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by Abdullah Alqahtani on 9/18/2020.
 */
public class QuickHideBehavior extends CoordinatorLayout.Behavior<View> {

    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = -1;

    //Track direction of user motion
    private int mScrollingDirection;
    //Tracking last threshold crossed
    private int mScrollTrigger;

    //Accumulated scroll distance
    private int mScrollDistance;
    //Distance threshold to trigger animation
    private int mScrollThreshold;

    private ObjectAnimator mAnimator;

    //Required to instantiate as a default behavior
    public QuickHideBehavior(){

    }
    //Required to attach behavior via xml
    public QuickHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {R.attr.actionBarSize});
        //Use half the standard action bar height
        mScrollThreshold = a.getDimensionPixelSize(0, 0) / 2;
        a.recycle();
    }

    //Called before a nested scroll event. Return true to declare interest and want to do something
    //It means the scrolling is about to happen do you care?
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull View child, @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        //We have to declare interest in the scroll to receive further events.
        //We only care if the scroll is vertical
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    //Called before the scrolling child consumes the event
    //We can steal all/part of the event by filling in the consumed array
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull View child,
                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //Determine direction changes here
        if(dy > 0 && mScrollingDirection != DIRECTION_UP){
            mScrollingDirection = DIRECTION_UP;
            mScrollDistance = 0;
        }else if(dy < 0 && mScrollingDirection != DIRECTION_DOWN) {
            mScrollingDirection = DIRECTION_DOWN;
            mScrollDistance = 0;

        }
    }

    //Called after the scrolling child consumes the event, with amount consumed
    //In this case it tells us exactly how far the recyclerview has scrolled
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        //Consumed distance is the actual distance travelled by the scrolling view
        mScrollDistance += dyConsumed;
        if (mScrollDistance > mScrollThreshold && mScrollTrigger != DIRECTION_UP) {
            //Hide the target view in this case the appbar or bottomnav
            mScrollTrigger = DIRECTION_UP;
            restartAnimator(child, getTargetHideValue(coordinatorLayout, child));
        }else if(mScrollDistance < -mScrollThreshold && mScrollTrigger != DIRECTION_DOWN){
            //Return the target view
            mScrollTrigger = DIRECTION_DOWN;
            restartAnimator(child, 0f);
        }
    }


    //Called after the scrolling child handles the fling
    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout,
                                 @NonNull View child, @NonNull View target,
                                 float velocityX, float velocityY, boolean consumed) {
        //We only care when the target view is already handling the fling
        if(consumed){
            if(velocityY > 0 && mScrollTrigger != DIRECTION_UP){
                mScrollTrigger = DIRECTION_UP;
                restartAnimator(child, getTargetHideValue(coordinatorLayout, child));
            }else if(velocityY < 0 && mScrollTrigger != DIRECTION_DOWN){
                mScrollTrigger = DIRECTION_DOWN;
                restartAnimator(child, 0f);
            }
        }

        return false;
    }


        /*Helper Methods*/
    // Helper to trigger hid/show animation
    private void restartAnimator(View target, float value){
        if(mAnimator != null){
            mAnimator.cancel();
            mAnimator = null;
        }

        mAnimator = ObjectAnimator
                .ofFloat(target, View.TRANSLATION_Y, value)
                .setDuration(250);
        mAnimator.start();
    }

    //This is for the restartAnimator() ObjectAnimator to determine which direction to go
    //i.e. If AppBarLayout then go up to hide. and bottom for the bottom nav
    private float getTargetHideValue(ViewGroup parent, View target){
        if(target instanceof AppBarLayout){
            return -target.getHeight();
        }else if(target instanceof BottomNavigationView) {
            //The parent.getHight returns the full height of the coordinatorlayout
            //The target.getTop it will returns the height between the top of bottomnave to the top of coordinatorlayout
            return parent.getHeight() - target.getTop();
        }else if(target instanceof FloatingActionButton){
            return parent.getHeight() - target.getTop();
        }

        return 0f;
    }

    /*//This is called to determine which views this behavior depends on
        @Override
        public boolean layoutDependsOn (@NonNull CoordinatorLayout parent, @NonNull View
        child, @NonNull View dependency){
            //We are watching changes in recyclerview
            return dependency instanceof RecyclerView;
        }

        //This is called for each change to the dependent view which in this case is the recyclerview moves
        @Override
        public boolean onDependentViewChanged (@NonNull CoordinatorLayout parent, @NonNull View
        child, @NonNull View dependency){
            return super.onDependentViewChanged(parent, child, dependency);
        }


        //This is called on each layout request
        @Override
        public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
            return super.onLayoutChild(parent, child, layoutDirection);
        }*/

    }


