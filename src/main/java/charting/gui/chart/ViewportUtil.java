package charting.gui.chart;

import javafx.geometry.Bounds;

public final class ViewportUtil {
    private ViewportUtil() {
    }

    /**
     * Converts an x point from a source bound to a target bound.
     */
    public static double toTargetX(Bounds source, double sourceX, Bounds target) {
        if (source == null || target == null) {
            return Double.NaN;
        }

        return target.getWidth() / source.getWidth() * (sourceX - source.getMinX()) + target.getMinX();
    }

    /**
     * Converts a y point from a source bound to a target bound.
     */
    public static double toTargetY(Bounds source, double sourceY, Bounds target) {
        if (source == null || target == null) {
            return Double.NaN;
        }

        return target.getHeight() / source.getHeight() * (sourceY - source.getMinY()) + target.getMinY();
    }

    /**
     * Converts a width value from a source bound to a target bound.
     */
    public static double toTargetWidth(Bounds source, double sourceWidth, Bounds target) {
        if (source == null || target == null) {
            return Double.NaN;
        }

        return target.getWidth() / source.getWidth() * sourceWidth;
    }

    /**
     * Converts a height value from a source bound to a target bound.
     */
    public static double toTargetHeight(Bounds source, double sourceHeight, Bounds target) {
        if (source == null || target == null) {
            return Double.NaN;
        }

        return target.getHeight() / source.getHeight() * sourceHeight;
    }
}
