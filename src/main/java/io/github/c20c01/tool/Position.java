package io.github.c20c01.tool;

public record Position(double x, double y, double z) {

    @Override
    public String toString() {
        return "Position{x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
