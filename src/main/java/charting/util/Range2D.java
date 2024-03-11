package charting.util;

public record Range2D(double startX, double startY, double endX, double endY) {
    public static final Range2D NAN = new Range2D(Double.NaN, Double.NaN, Double.NaN, Double.NaN);

    public double getWidth() {
        return endX - startX;
    }

    public double getHeight() {
        return endY - startY;
    }

    public double getCenterX() {
        return startX + getWidth() / 2;
    }

    public double getCenterY() {
        return startY + getHeight() / 2;
    }

    public Range2D scaled(double factorX, double factorY) {
        return scaled(factorX, factorY, getCenterX(), getCenterY());
    }

    public Range2D scaled(double factorX, double factorY, double pivotX, double pivotY) {
        double newStartX = (startX - pivotX) * factorX + pivotX;
        double newStartY = (startY - pivotY) * factorY + pivotY;
        double newEndX = (endX - pivotX) * factorX + pivotX;
        double newEndY = (endY - pivotY) * factorY + pivotY;
        return new Range2D(newStartX, newStartY, newEndX, newEndY);
    }

    public Range2D translated(double distX, double distY) {
        return new Range2D(startX + distX, startY + distY,
                endX + distX, endY + distY);
    }

    /**
     * Converts an x position from this source to a target.
     */
    public double toTargetX(double sourceX, Range2D target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getWidth() / getWidth() * (sourceX - startX()) + target.startX();
    }

    /**
     * Converts a y position from this source to a target.
     */
    public double toTargetY(double sourceY, Range2D target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getHeight() / getHeight() * (sourceY - startY()) + target.startY();
    }

    /**
     * Converts a width from this source to a target.
     */
    public double toTargetWidth(double sourceWidth, Range2D target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getWidth() / getWidth() * sourceWidth;
    }

    /**
     * Converts a height from this source to a target.
     */
    public double toTargetHeight(double sourceHeight, Range2D target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getHeight() / getHeight() * sourceHeight;
    }
}
