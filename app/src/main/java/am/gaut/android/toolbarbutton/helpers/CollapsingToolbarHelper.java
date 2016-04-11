package am.gaut.android.toolbarbutton.helpers;

import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Need to duplicate code here because folks at Google decided to make their classes and methods
 * private
 *
 * Requires Honeycomb+
 */
public class CollapsingToolbarHelper {
    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();
    private static final Matrix IDENTITY = new Matrix();

    CollapsingToolbarHelper() {
    }

    /**
     * Borrowed from {@link android.support.design.widget.FloatingActionButton}
     */

    public static void showView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            if (ViewCompat.isLaidOut(view)) {
                view.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(200L)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setListener(null);
            }
        }
    }

    public static void hideView(View view, boolean isHiding,
                                AnimatorListenerAdapter animatorListenerAdapter) {
        if (view.getVisibility() != View.GONE) {
            if(ViewCompat.isLaidOut(view) && !view.isInEditMode()) {
                if (!isHiding) {
                    view.animate().scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setDuration(200L)
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .setListener(animatorListenerAdapter);
                }
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Borrowed from {@link android.support.design.widget.AppBarLayout}
     */
    public static int getMinimumHeightForVisibleOverlappingContent(ViewGroup appBarLayout) {
        int topInset = 0;
        int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            return minHeight * 2 + topInset;
        } else {
            int childCount = appBarLayout.getChildCount();
            return childCount >= 1
                    ? ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2
                    + topInset
                    : 0;
        }
    }

    /**
     * Honeycomb IMPL borrowed from {@link android.support.design.widget.ViewGroupUtils} and
     * {@link android.support.design.widget.ViewGroupUtilsHoneycomb}
     */

    public static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        offsetDescendantRect(parent, descendant, out);
    }

    static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
        Matrix m = sMatrix.get();
        if (m == null) {
            m = new Matrix();
            sMatrix.set(m);
        } else {
            m.set(IDENTITY);
        }

        offsetDescendantMatrix(group, child, m);

        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
        }
        rectF.set(rect);
        m.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }

        m.preTranslate(view.getLeft(), view.getTop());

        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }
}
