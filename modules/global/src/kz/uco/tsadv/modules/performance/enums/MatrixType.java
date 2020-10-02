package kz.uco.tsadv.modules.performance.enums;

/**
 * @author Adilbekov Yernar
 */
public enum MatrixType {
    PERFORMANCE,
    RISK;

    public static MatrixType find(String type) {
        for (MatrixType matrixType : MatrixType.values()) {
            if (matrixType.name().equalsIgnoreCase(type)) return matrixType;
        }
        return null;
    }
}
