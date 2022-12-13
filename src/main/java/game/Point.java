package game;

public class Point {
    public int height;
    public int width;

    public Point(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public Point(Point p) {
        this.height = p.height;
        this.width = p.width;
    }

    public boolean isSame(Point p) {
        return p.width == width && p.height == height;
    }

    public Point copy() {
        return new Point(height, width);
    }
}
