import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private final Picture pict;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Picture is null");
        this.width = picture.width();
        this.height = picture.height();
        this.pict = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                picture.set(x, y, pict.get(x, y));
        return picture;
    }
    // width of current picture

    public int width() {
        return width;
    }
    // height of current picture

    public int height() {
        return height;
    }
    // energy of pixel at column x and row y

    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException("Indices oob");

        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return BORDER_ENERGY;

        int deltaX = squaredGradient(x + 1, y, x - 1, y);
        int deltaY = squaredGradient(x, y + 1, x, y - 1);
        return Math.sqrt(deltaX + deltaY);
    }
    // sequence of indices for horizontal seam

    public int[] findHorizontalSeam() {
        int[][] parent = new int[width][height];
        double[][] tot = new double[2][height];
        for (int y = 0; y < height; y++) {
            tot[0][y] = BORDER_ENERGY;
            parent[0][y] = y;
        }

        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double tmp = tot[(x - 1) % 2][y];
                parent[x][y] = y;
                if (y > 0 && tot[(x - 1) % 2][y - 1] < tmp) {
                    tmp = tot[(x - 1) % 2][y - 1];
                    parent[x][y] = y - 1;
                }

                if (y < height - 1 && tot[(x - 1) % 2][y + 1] < tmp) {
                    tmp = tot[(x - 1) % 2][y + 1];
                    parent[x][y] = y + 1;
                }
                tot[x % 2][y] = energy(x, y) + tmp;
            }
        }

        int idx = 0;
        for (int y = 1; y < height; y++) if (tot[(width - 1) % 2][y] < tot[(width - 1) % 2][idx]) idx = y;
        int[] seam = new int[width];
        seam[width - 1] = idx;
        for (int x = width - 2; x >= 0; --x) {
            seam[x] = parent[x + 1][idx];
            idx = parent[x + 1][idx];
        }
        return seam;
    }
    // sequence of indices for vertical seam

    public int[] findVerticalSeam() {
        int[][] parent = new int[width][height];
        double[][] tot = new double[width][2];
        for (int x = 0; x < width; x++) {
            tot[x][0] = BORDER_ENERGY;
            parent[x][0] = x;
        }

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double tmp = tot[x][(y - 1) % 2];
                parent[x][y] = x;
                if (x > 0 && tot[x - 1][(y - 1) % 2] < tmp) {
                    tmp = tot[x - 1][(y - 1) % 2];
                    parent[x][y] = x - 1;
                }

                if (x < width - 1 && tot[x + 1][(y - 1) % 2] < tmp) {
                    tmp = tot[x + 1][(y - 1) % 2];
                    parent[x][y] = x + 1;
                }
                tot[x][y % 2] = energy(x, y) + tmp;
            }
        }

        int idx = 0;
        for (int x = 1; x < width; x++) if (tot[x][(height - 1) % 2] < tot[idx][(height - 1) % 2]) idx = x;
        int[] seam = new int[height];
        seam[height - 1] = idx;
        for (int y = height - 2; y >= 0; y--) {
            seam[y] = parent[idx][y + 1];
            idx = parent[idx][y + 1];
        }
        return seam;
    }
    // remove horizontal seam from current picture

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) throw new IllegalArgumentException("Illegal seam");

        if (height <= 1) throw new IllegalArgumentException("Already minimal picture");

        for (int x = 0; x < width; x++) {
            if (seam[x] < 0 || seam[x] >= height || (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1))
                throw new IllegalArgumentException("Illegal seam");
            for (int y = seam[x]; y < height - 1; y++)
                pict.set(x, y, pict.get(x, y + 1));
        }

        height--;
    }
    // remove vertical seam from current picture

    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height) throw new IllegalArgumentException();

        if (width <= 1) throw new IllegalArgumentException("Already minimal picture");

        for (int y = 0; y < height; y++) {
            if (seam[y] < 0 || seam[y] >= width || (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1))
                throw new IllegalArgumentException("Illegal seam");
            for (int x = seam[y]; x < width - 1; x++)
                pict.set(x, y, pict.get(x + 1, y));
        }

        width--;
    }

    private int squaredGradient(int x1, int y1, int x2, int y2) {
        Color c1 = pict.get(x1, y1);
        Color c2 = pict.get(x2, y2);
        int red = c1.getRed() - c2.getRed();
        int green = c1.getGreen() - c2.getGreen();
        int blue = c1.getBlue() - c2.getBlue();
        return red * red + green * green + blue * blue;
    }
}
