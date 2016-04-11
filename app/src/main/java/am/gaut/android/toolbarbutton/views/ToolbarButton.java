package am.gaut.android.toolbarbutton.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;

import am.gaut.android.toolbarbutton.helpers.CollapsingToolbarHelper;

/**
 * Toolbar buttons are used for a special type of promoted action. They are used in combination
 * with a FloatingActionButton anchored to a CollapsingToolbarLayout.
 *
 * Requires ICS+ (sdk 14+)
 */
@CoordinatorLayout.DefaultBehavior(ToolbarButton.Behavior.class)
public class ToolbarButton extends Button {

    private static final String LOG_TAG = "ToolbarButton";

    /**
     * Callback to be invoked when the visibility of a ToolbarButton changes.
     */
    public abstract static class OnVisibilityChangedListener {
        /**
         * Called when a ToolbarButton has been
         * {@link #show(OnVisibilityChangedListener) shown}.
         *
         * @param toolbarBtn the ToolbarButton that was shown.
         */
        public void onShown(ToolbarButton toolbarBtn) {}

        /**
         * Called when a ToolbarButton has been
         * {@link #hide(OnVisibilityChangedListener) hidden}.
         *
         * @param toolbarBtn the ToolbarButton that was hidden.
         */
        public void onHidden(ToolbarButton toolbarBtn) {}
    }

    private static final String XMLNS_ANDROID = "http://schemas.android.com/apk/res/android";

    private static final int SHOW_HIDE_ANIM_DURATION = 200;
    private static final Interpolator FAST_OUT_LINEAR_IN_INTERPOLATOR = new FastOutLinearInInterpolator();
    private static final Interpolator LINEAR_OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();

    private boolean mIsHiding;

    public ToolbarButton(Context context) {
        this(context, null);
    }

    public ToolbarButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolbarButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Hide if there's no visibility attribute
        if (attrs.getAttributeValue(XMLNS_ANDROID, "visibility") == null) {
            setVisibility(GONE);
        }

        // Add elevation if it's not set
        if (Build.VERSION.SDK_INT >= 21 && attrs.getAttributeValue(XMLNS_ANDROID, "elevation") == null) {
            setElevation(android.support.design.R.dimen.design_fab_elevation);
        }
    }

    /**
     * Shows the button.
     * <p>This method will animate the button show if the view has already been laid out.</p>
     */
    public void show() {
        show(null);
    }

    /**
     * Shows the button.
     * <p>This method will animate the button show if the view has already been laid out.</p>
     *
     * @param listener the listener to notify when this view is shown
     */
    public void show(@Nullable final OnVisibilityChangedListener listener) {
        if (mIsHiding || getVisibility() != View.VISIBLE) {
            if (ViewCompat.isLaidOut(this) && !isInEditMode()) {
                animate().cancel();
                if (getVisibility() != View.VISIBLE) {
                    // If the view isn't visible currently, we'll animate it from a single pixel
                    setAlpha(0f);
                    setScaleY(0f);
                    setScaleX(0f);
                }
                animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(SHOW_HIDE_ANIM_DURATION)
                        .setInterpolator(LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (listener != null) {
                                    listener.onShown(ToolbarButton.this);
                                }
                            }
                        });
            } else {
                setVisibility(View.VISIBLE);
                setAlpha(1f);
                setScaleY(1f);
                setScaleX(1f);
                if (listener != null) {
                    listener.onShown(this);
                }
            }
        }
    }

    /**
     * Hides the button.
     * <p>This method will animate the button hide if the view has already been laid out.</p>
     */
    public void hide() {
        hide(null);
    }

    private void hide(@Nullable final OnVisibilityChangedListener listener) {
        if (mIsHiding || getVisibility() != View.VISIBLE) {
            // A hide animation is in progress, or we're already hidden. Skip the call
            if (listener != null) {
                listener.onHidden(this);
            }
            return;
        }

        if (!ViewCompat.isLaidOut(this) || isInEditMode()) {
            // If the view isn't laid out, or we're in the editor, don't run the animation
            setVisibility(View.GONE);
            if (listener != null) {
                listener.onHidden(this);
            }
        } else {
            animate().cancel();
            animate().scaleX(0.0F)
                    .scaleY(0.0F)
                    .alpha(0.0F)
                    .setDuration(SHOW_HIDE_ANIM_DURATION)
                    .setInterpolator(FAST_OUT_LINEAR_IN_INTERPOLATOR)
                    .setListener(new AnimatorListenerAdapter() {
                        private boolean mCancelled;

                        @Override
                        public void onAnimationStart(Animator animation) {
                            mIsHiding = true;
                            mCancelled = false;
                            setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mIsHiding = false;
                            mCancelled = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mIsHiding = false;
                            if (!mCancelled) {
                                setVisibility(View.GONE);
                                if (listener != null) {
                                    listener.onHidden(ToolbarButton.this);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Behavior designed for use with {@link ToolbarButton} instances. It's main function
     * is to show/hide {@link ToolbarButton} views based on the layout they are associated with.
     */
    public static class Behavior extends CoordinatorLayout.Behavior<ToolbarButton> {
        private Rect mTmpRect;
        private boolean mIsHiding;

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
        }

        public boolean layoutDependsOn(CoordinatorLayout parent, ToolbarButton child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, ToolbarButton child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                this.updateButtonVisibility(parent, (AppBarLayout) dependency, child);
            }

            return false;
        }

        private boolean updateButtonVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout, final ToolbarButton child) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

            if (lp.getAnchorId() != appBarLayout.getId()) {
                // The anchor ID doesn't match the dependency, so we won't automatically
                // show/hide the button
                return false;
            }

            if (mTmpRect == null) {
                mTmpRect = new Rect();
            }

            final Rect rect = mTmpRect;
            CollapsingToolbarHelper.getDescendantRect(parent, appBarLayout, rect);

            // Hide show code logic borrowed from Android Support Library Floating Action Button
            if (rect.bottom <= CollapsingToolbarHelper.getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
                child.show();

                // Height should equal toolbar height
                // If android:fitsSystemWindows="true" is enabled, add appropriate top margin
                final int inset = CollapsingToolbarHelper.getTopInset(appBarLayout);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                params.topMargin = inset;
                params.height = rect.bottom - inset;
                child.setLayoutParams(params);

            } else {
                child.hide();
            }
            return true;

        }
    }
}