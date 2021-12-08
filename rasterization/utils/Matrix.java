package rasterization.utils;

public class Matrix {
    private final double[][] mat = {
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 1}
    };
    public Matrix() {}
    public Matrix(Matrix other) {
        int i, j;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                mat[i][j] = other.mat[i][j];
            }
        }
    }


    public static Matrix coordinateSystem(
        Vector right, Vector up, Vector forward, Vector translation) {

        Matrix result = translate(translation.x, translation.y, translation.z);

        result.mat[0][0] = right.x;
        result.mat[0][1] = right.y;
        result.mat[0][2] = right.z;

        result.mat[1][0] = up.x;
        result.mat[1][1] = up.y;
        result.mat[1][2] = up.z;

        result.mat[2][0] = forward.x;
        result.mat[2][1] = forward.y;
        result.mat[2][2] = forward.z;

        return result;
    }
    /*
     *  1  0  0  0
     *  0  1  0  0
     *  0  0  1  0
     *  x  y  z  1
     */
    public static Matrix translate(double x, double y, double z) {
        Matrix result = new Matrix();
        result.mat[3][0] = x;
        result.mat[3][1] = y;
        result.mat[3][2] = z;
        return result;
    }
    /*
     *  scale   0     0   0
     *    0   scale   0   0
     *    0     0   scale 0
     *    0     0     0   1
     */
    public static Matrix scale(double scalar) {
        Matrix result = new Matrix();
        result.mat[0][0] = scalar;
        result.mat[1][1] = scalar;
        result.mat[2][2] = scalar;
        return result;
    }
    
    public static Matrix rotateDeg(double degX, double degY, double degZ) {
        double radX = Math.toRadians(degX);
        double radY = Math.toRadians(degY);
        double radZ = Math.toRadians(degZ);
        return rotateRad(radX, radY, radZ);
    }

    public static Matrix rotateRad(double radX, double radY, double radZ) {
        return 
            rotateAroundX(radX).combine(
            rotateAroundY(radY)).combine(
            rotateAroundZ(radZ));
    }
    /*
     *  1   0   0  0
     *  0  cos sin 0
     *  0 -sin cos 0
     *  0   0   0  1
     */
    public static Matrix rotateAroundX(double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        Matrix result = new Matrix();
        result.mat[1][1] = cos;
        result.mat[1][2] = sin;
        result.mat[2][1] = -sin;
        result.mat[2][2] = cos;
        return result;
    }
    /*
     *  cos 0 -sin 0
     *   0  1   0  0
     *  sin 0  cos 0
     *   0  0   0  1
     */
    public static Matrix rotateAroundY(double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        Matrix result = new Matrix();
        result.mat[0][0] = cos;
        result.mat[0][2] = -sin;
        result.mat[2][0] = sin;
        result.mat[2][2] = cos;
        return result;
    }
    /*
     *   cos sin 0 0
     *  -sin cos 0 0
     *    0   0  1 0
     *    0   0  0 1
     */
    public static Matrix rotateAroundZ(double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        Matrix result = new Matrix();
        result.mat[0][0] = cos;
        result.mat[0][1] = sin;
        result.mat[1][0] = -sin;
        result.mat[1][1] = cos;
        return result;
    }

    public Matrix combine(Matrix other) {
        Matrix result = new Matrix();
        double[][] mat1 = this.mat;
        double[][] mat2 = other.mat;
        double[][] mat3 = result.mat;

        int i, j, k; double sum;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                sum = 0;
                for (k = 0; k < 4; k++) {
                    sum += mat1[i][k] * mat2[k][j];
                }
                mat3[i][j] = sum;
            }
        }
        return result;
    }


    public void transform(Vector vector) {
        double x = vector.x, y = vector.y, z = vector.z;

        vector.x = x * mat[0][0] + y * mat[1][0] + z * mat[2][0] + mat[3][0];
        vector.y = x * mat[0][1] + y * mat[1][1] + z * mat[2][1] + mat[3][1];
        vector.z = x * mat[0][2] + y * mat[1][2] + z * mat[2][2] + mat[3][2];
    }
    public Vector getTransformation(Vector vector) {
        double x = vector.x, y = vector.y, z = vector.z;

        return new Vector(
            x * mat[0][0] + y * mat[1][0] + z * mat[2][0] + mat[3][0],
            x * mat[0][1] + y * mat[1][1] + z * mat[2][1] + mat[3][1],
            x * mat[0][2] + y * mat[1][2] + z * mat[2][2] + mat[3][2]
        );
    }


    private void swapRows(int row1, int row2) {
        double temp;
        for (int i = 0; i < 4; i++) {
            temp = mat[row1][i];
            mat[row1][i] = mat[row2][i];
            mat[row2][i] = temp;
        }
    }
    private void addRows(double scale, int rowFrom, int rowTo) {
        for (int i = 0; i < 4; i++) {
            mat[rowTo][i] += scale * mat[rowFrom][i];
        }
    }
    private void scaleRow(double scale, int row) {
        for (int i = 0; i < 4; i++) {
            mat[row][i] *= scale;
        }
    }


    public Matrix inverse() {
        Matrix leftMat = new Matrix(this);
        Matrix rightMat = new Matrix();
        
        double[][] left = leftMat.mat;

        for (int col = 0; col < 4; col++) {
            double pivot = left[col][col];
            if (pivot == 0) {
                double colHighest = 0;
                int rowToSwap = col;

                for (int row = 0; row < 4; row++) {
                    double value = left[row][col];
                    if (Math.abs(value) > colHighest) {
                        colHighest = value;
                        rowToSwap = row;
                    }
                }
                if (rowToSwap == col)
                    return null;
                leftMat.swapRows(col, rowToSwap);
                rightMat.swapRows(col, rowToSwap);
                pivot = left[col][col];
            }
            for (int row = 0; row < 4; row++) {
                if (row == col)
                    continue;
                double value = left[row][col];
                if (value == 0)
                    continue;
                leftMat.addRows(-value / pivot, col, row);
                rightMat.addRows(-value / pivot, col, row);
                left[row][col] = 0;
            }
        }
        for (int row = 0; row < 4; row++) {
            rightMat.scaleRow(1.0/left[row][row], row);
        }
        return rightMat;
    }
}