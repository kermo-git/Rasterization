package rasterization.utils;

public class Vector {
    public double x = 0, y = 0, z = 0;

    public Vector() {}
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector(Vector startPoint, Vector endPoint) {
        this(
            endPoint.x - startPoint.x,
            endPoint.y - startPoint.y,
            endPoint.z - startPoint.z
        );
    }
    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof Vector) {
            Vector v = (Vector) other;
            return x == v.x && y == v.y && z == v.z;
        }
        return false;
    }
    @Override
    public String toString() {
        return "<" + x + ", " + y+ ", " + z + ">";
    }


    public void normalize() {
        double len = length();
        x /= len;
        y /= len;
        z /= len;
    }
    public void add(Vector v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }
    public void add(double scalar, Vector v) {
        x += scalar * v.x;
        y += scalar * v.y;
        z += scalar * v.z;
    }
    public void add(double scalar, Vector v1, Vector v2) {
        x += scalar * v1.x * v2.x;
        y += scalar * v1.y * v2.y;
        z += scalar * v1.z * v2.z;
    }
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }
    public Vector getScaled(double scalar) {
        return new Vector(
            x * scalar,
            y * scalar,
            z * scalar
        );
    }

    
    public void filter(Vector filter) {
        x *= filter.x;
        y *= filter.y;
        z *= filter.z;
    }
    public Vector getFiltered(Vector filter) {
        return new Vector(
            x * filter.x,
            y * filter.y,
            z * filter.z
        );
    }

    
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    public double dot(Vector v) {
        return x*v.x + y*v.y + z*v.z;
    }
    public Vector cross(Vector v) {
        return new Vector(
            y * v.z - z * v.y, 
            z * v.x - x * v.z, 
            x * v.y - y * v.x
        );
    }
}