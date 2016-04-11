package am.gaut.android.toolbarbutton.controls;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import am.gaut.android.toolbarbutton.helpers.CollapsingToolbarHelper;

public class ToolbarButtonBehaviour extends CoordinatorLayout.Behavior<TextView> {
    private Rect mTmpRect;
    private boolean mIsHiding;

    public ToolbarButtonBehaviour() {
    }

    public ToolbarButtonBehaviour(Context context, AttributeSet attrs) {
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            this.updateButtonVisibility(parent, (AppBarLayout) dependency, child);
        }

        return false;
    }

    private boolean updateButtonVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout, final TextView child) {
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
            CollapsingToolbarHelper.showView(child);

            // Height should equal toolbar height
            // If android:fitsSystemWindows="true" is enabled, add appropriate top margin
            int inset = CollapsingToolbarHelper.getTopInset(appBarLayout);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            params.topMargin = inset;
            params.height = rect.bottom - inset;
            child.setLayoutParams(params);

        } else {
            CollapsingToolbarHelper.hideView(child, this.mIsHiding, new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    ToolbarButtonBehaviour.this.mIsHiding = true;
                }

                public void onAnimationCancel(Animator animation) {
                    ToolbarButtonBehaviour.this.mIsHiding = false;
                }

                public void onAnimationEnd(Animator animation) {
                    ToolbarButtonBehaviour.this.mIsHiding = false;
                    child.setVisibility(View.GONE);
                }
            });
        }
        return true;

    }
}
