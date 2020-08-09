import java.util.*;

public class Vector implements Comparable<Vector>
{
    public final double x;
    public final double y;
    public final double z;
    public final int hash;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        long bits = Double.doubleToLongBits(x+10097*y+300179*z);
        hash = (int)(bits ^ (bits >>> 32));
    }



    public Vector mul(double scale) {
        return new Vector(
        x * scale,
        y * scale,
        z * scale);
    }


    public Vector sub(Vector t2) {
        return new Vector(
        this.x - t2.x,
        this.y - t2.y,
        this.z - t2.z
        );
    }

    public Vector add(Vector t2) {
        return new Vector(
        this.x + t2.x,
        this.y + t2.y,
        this.z + t2.z
        );
    }

    public double length() {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }

    public Vector normalize() {
        double norm = 1.0 / length();
        return new Vector(
        this.x * norm,
        this.y * norm,
        this.z * norm
        );
    }

    public static Vector cross(Vector v1, Vector v2) {
        return new Vector(
        v1.y * v2.z - v1.z * v2.y,
        v2.x * v1.z - v2.z * v1.x,
        v1.x * v2.y - v1.y * v2.x
        );
    }


    public double dot(Vector v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vector) {
            Vector v = (Vector) obj;
            return (x == v.x) && (y == v.y) && (z == v.z);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public int compareTo(Vector v)
    {
        if(this.x != v.x)
            return (this.x - v.x) > 0 ? 1 : -1;
        if(this.y != v.y)
            return (this.y- v.y) > 0 ? 1 : -1;
        if(this.z - v.z == 0) return 0;
        return (this.z - v.z) > 0 ? 1 : -1;
    }

    @Override
    public String toString() {
        return "(" + (float)x + "," + (float)y + "," + (float)z + ")";
    }

    public static double getAngle(Vector a, Vector b){
        double AdotB = a.dot(b);
        double A = a.length();
        double B = b.length();
        return Math.acos(AdotB / (A * B));
    }

    public static Vector mean(Vector a, Vector b){
        return new Vector((a.x+b.x)/2, (a.y+b.y)/2, (a.z+b.z)/2);
    }
}

class Vertex implements Comparable<Vertex>
{
    Vector v;
    HashSet<Triangle> triangles;
    HashSet<Edge> edges;
    Matrix quadric;
    double angle = 0, area = 0, curv = -1;
    
    public Vertex(Vector _v)
    {
        v = _v;
        triangles = new HashSet<>();
        edges = new HashSet<>();
        quadric = new Matrix();
    }
    
    public int hashCode()
    {
        return v.hashCode();
    }
    
    public int compareTo(Vertex v)
    {
        return this.v.compareTo(v.v);
    }
    
    public boolean equals(Object o)
    {
        if(o instanceof Vertex)
            return this.v.equals(((Vertex)o).v);
        return false;
    }
    
    static double maxCurv = 0;
}
