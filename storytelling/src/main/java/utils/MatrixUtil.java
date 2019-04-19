package utils;

import errors.MatrixError;
import errors.MatrixException;

public class MatrixUtil {

    public static Double[][] product(Double[][] m1, Double[][] m2) {
        if (m1 == null) throw new MatrixException(MatrixError.FIRST_NULL.getMessage());
        if (m2 == null) throw new MatrixException(MatrixError.LATTER_NULL.getMessage());
        if (!MatrixUtil.isRectangular(m1))
            throw new MatrixException(MatrixError.FIRST_NOT_RECTANGULAR.getMessage());
        if (!MatrixUtil.isRectangular(m2))
            throw new MatrixException(MatrixError.LATTER_NOT_RECTANGULAR.getMessage());
        int m = m1.length;
        int n = m1[0].length;
        int p = m2.length;
        int q = m2[0].length;
        if (n != p) throw new MatrixException(MatrixError.MISSHAPEN.getMessage());

        Double[][] res = new Double[m][q];
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < q; col++) {
                double sum = 0;
                for (int d = 0; d < n; d++) {
                    sum += m1[row][d] * m2[d][col];
                }
                res[row][col] = sum;
            }
        }
        return res;
    }

    public static Double[][] entrywiseProduct(Double[][] m1, Double[][] m2) {
        if (m1 == null) throw new MatrixException(MatrixError.FIRST_NULL.getMessage());
        if (m2 == null) throw new MatrixException(MatrixError.LATTER_NULL.getMessage());
        if (!MatrixUtil.isRectangular(m1))
            throw new MatrixException(MatrixError.FIRST_NOT_RECTANGULAR.getMessage());
        if (!MatrixUtil.isRectangular(m2))
            throw new MatrixException(MatrixError.LATTER_NOT_RECTANGULAR.getMessage());
        int m = m1.length;
        int n = m1[0].length;
        int p = m2.length;
        int q = m2[0].length;
        if (m != p || n != q) throw new MatrixException(MatrixError.MISSHAPEN.getMessage());

        Double[][] res = new Double[m][n];
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < n; col++) {
                res[row][col] = m1[row][col] * m2[row][col];
            }
        }
        return res;
    }

    public static boolean isRectangular(Double[][] m) {
        if (m == null) return false;
        if (m.length == 0) return false;
        if (m[0] == null) return false;
        if (m[0].length == 0) return false;
        int len = m[0].length;
        for (int i = 1; i < m.length; i++) {
            if (m[i].length != len) return false;
        }
        return true;
    }
}
