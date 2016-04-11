package am.gaut.android.toolbarbutton.helpers;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Need to duplicate code here because folks at Google decided to make their classes and methods
 * private.
 */
public class CollapsingToolbarHelper {
    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();
    private static final Matrix IDENTITY = new Matrix();

    /**
     * Borrowed from {@link android.support.design.widget.AppBarLayout}
     */
    public static int getMinimumHeightForVisibleOverlappingContent(ViewGroup appBarLayout) {
        final int topInset = getTopInset(appBarLayout);
        final int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            // If this layout has a min height, use it (doubled)
            return (minHeight * 2) + topInset;
        }

        // Otherwise, we'll use twice the min height of our last child
        final int childCount = appBarLayout.getChildCount();
        return childCount >= 1
                ? (ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2) + topInset
                : 0;
    }

    /**
     * Hack since we don't have access to the private
     * {@link android.support.design.widget.AppBarLayout getTopInset()} method
     */
    public static int getTopInset(ViewGroup appBarLayout) {
        int inset = 0;

        int resourceId = appBarLayout.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (ViewCompat.getFitsSystemWindows(appBarLayout) && resourceId > 0) {
            inset = appBarLayout.getResources().getDimensionPixelSize(resourceId);
        }

        return inset;
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
