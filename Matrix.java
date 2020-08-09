final public class Matrix
{
    public final double x00, x01, x02, x03,
                        x10, x11, x12, x13,
                        x20, x21, x22, x23,
                        x30, x31, x32, x33;
    Matrix inverse = null;
    Vector bestVector = null;
    Double quadrForm = null;
    // create M-by-N matrix of 0's
    public Matrix(){
        x00 = 0; x01 = 0; x02 = 0; x03 = 0;
        x10 = 0; x11 = 0; x12 = 0; x13 = 0;
        x20 = 0; x21 = 0; x22 = 0; x23 = 0;
        x30 = 0; x31 = 0; x32 = 0; x33 = 0;
    }

    public Matrix(  double a00, double a01, double a02, double a03,
                    double a10, double a11, double a12, double a13,
                    double a20, double a21, double a22, double a23,
                    double a30, double a31, double a32, double a33) {
        x00 = a00; x01 = a01; x02 = a02; x03 = a03;
        x10 = a10; x11 = a11; x12 = a12; x13 = a13;
        x20 = a20; x21 = a21; x22 = a22; x23 = a23;
        x30 = a30; x31 = a31; x32 = a32; x33 = a33;
    }

    // return C = A + B
    public Matrix plus(Matrix B)
    {
        return new Matrix(
        this.x00 + B.x00, this.x10 + B.x10, this.x20 + B.x20, this.x30 + B.x30,
        this.x01 + B.x01, this.x11 + B.x11, this.x21 + B.x21, this.x31 + B.x31,
        this.x02 + B.x02, this.x12 + B.x12, this.x22 + B.x22, this.x32 + B.x32,
        this.x03 + B.x03, this.x13 + B.x13, this.x23 + B.x23, this.x33 + B.x33);
    }
    
    public Matrix scalar(double s)
    {
        return new Matrix(
        this.x00   *s, this.x10   *s, this.x20   *s, this.x30   *s,
        this.x01   *s, this.x11   *s, this.x21   *s, this.x31   *s,
        this.x02   *s, this.x12   *s, this.x22   *s, this.x32   *s,
        this.x03   *s, this.x13   *s, this.x23   *s, this.x33   *s);
    }


    public double getDeterminant()
    {
        return (
        x00*x11*x22*x33 - x00*x11*x23*x32 +
        x00*x12*x23*x31 - x00*x12*x21*x33 +
        x00*x13*x21*x32 - x00*x13*x22*x31 -
        x01*x12*x23*x30 + x01*x12*x20*x33 -
        x01*x13*x20*x32 + x01*x13*x22*x30 -
        x01*x10*x22*x33 + x01*x10*x23*x32 +
        x02*x13*x20*x31 - x02*x13*x21*x30 +
        x02*x10*x21*x33 - x02*x10*x23*x31 +
        x02*x11*x23*x30 - x02*x11*x20*x33 -
        x03*x10*x21*x32 + x03*x10*x22*x31 -
        x03*x11*x22*x30 + x03*x11*x20*x32 -
        x03*x12*x20*x31 + x03*x12*x21*x30);
    }

    public Matrix getInverse()
    {
        if(inverse != null)
            return inverse;
        double r = 1/getDeterminant();
        if(Math.abs(r) > 10e6)
            return null;
        inverse = new Matrix(
        (x12*x23*x31 - x13*x22*x31 + x13*x21*x32 - x11*x23*x32 - x12*x21*x33 + x11*x22*x33) *r,
        (x03*x22*x31 - x02*x23*x31 - x03*x21*x32 + x01*x23*x32 + x02*x21*x33 - x01*x22*x33) *r,
        (x02*x13*x31 - x03*x12*x31 + x03*x11*x32 - x01*x13*x32 - x02*x11*x33 + x01*x12*x33) *r,
        (x03*x12*x21 - x02*x13*x21 - x03*x11*x22 + x01*x13*x22 + x02*x11*x23 - x01*x12*x23) *r,
        (x13*x22*x30 - x12*x23*x30 - x13*x20*x32 + x10*x23*x32 + x12*x20*x33 - x10*x22*x33) *r,
        (x02*x23*x30 - x03*x22*x30 + x03*x20*x32 - x00*x23*x32 - x02*x20*x33 + x00*x22*x33) *r,
        (x03*x12*x30 - x02*x13*x30 - x03*x10*x32 + x00*x13*x32 + x02*x10*x33 - x00*x12*x33) *r,
        (x02*x13*x20 - x03*x12*x20 + x03*x10*x22 - x00*x13*x22 - x02*x10*x23 + x00*x12*x23) *r,
        (x11*x23*x30 - x13*x21*x30 + x13*x20*x31 - x10*x23*x31 - x11*x20*x33 + x10*x21*x33) *r,
        (x03*x21*x30 - x01*x23*x30 - x03*x20*x31 + x00*x23*x31 + x01*x20*x33 - x00*x21*x33) *r,
        (x01*x13*x30 - x03*x11*x30 + x03*x10*x31 - x00*x13*x31 - x01*x10*x33 + x00*x11*x33) *r,
        (x03*x11*x20 - x01*x13*x20 - x03*x10*x21 + x00*x13*x21 + x01*x10*x23 - x00*x11*x23) *r,
        (x12*x21*x30 - x11*x22*x30 - x12*x20*x31 + x10*x22*x31 + x11*x20*x32 - x10*x21*x32) *r,
        (x01*x22*x30 - x02*x21*x30 + x02*x20*x31 - x00*x22*x31 - x01*x20*x32 + x00*x21*x32) *r,
        (x02*x11*x30 - x01*x12*x30 - x02*x10*x31 + x00*x12*x31 + x01*x10*x32 - x00*x11*x32) *r,
        (x01*x12*x20 - x02*x11*x20 + x02*x10*x21 - x00*x12*x21 - x01*x10*x22 + x00*x11*x22) *r);
        return inverse;
    }

    public Vector getBestVector()
    {
        if(bestVector != null)
            return bestVector;
        Matrix b = new Matrix(
            x00,x01,x02,x03,
            x10,x11,x12,x13,
            x20,x21,x22,x23,
            0, 0, 0, 1
        );
        Matrix inv = b.getInverse();
        if(inv == null || inv.x03!=inv.x03 ||inv.x13!=inv.x13 || inv.x23!=inv.x23)
            return null;
        bestVector = new Vector(inv.x03, inv.x13, inv.x23);
        return bestVector;
    }

    public double quadraticForm(Vector v)
    {
        if(quadrForm != null)
            return quadrForm;

        quadrForm = (v.x*this.x00*v.x + v.y*this.x10*v.x + v.z*this.x20*v.x + this.x30*v.x +
        v.x*this.x01*v.y + v.y*this.x11*v.y + v.z*this.x21*v.y + this.x31*v.y +
        v.x*this.x02*v.z + v.y*this.x12*v.z + v.z*this.x22*v.z + this.x32*v.z +
        v.x*this.x03 + v.y*this.x13 + v.z*this.x23 + this.x33);

        return quadrForm;
    }

}
