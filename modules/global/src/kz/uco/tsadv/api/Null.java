package kz.uco.tsadv.api;

/**
 * Содержит статические методы для работы с null объектами. Например обёртка для (obj1 != null ? obj1 : obj2)
 * @author  Felix Kamalov
 * @version 1.0
 */
public final class Null {
    /**
     * Возвращает первый объект если он не null, иначе второй объект
     * @param value         Основное (приоритетное) значение
     * @param defaultValue  Запасное значение (если первое - null)
     */
    public static <T> T nullReplace(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Возвращает первый объект если он не null, иначе второй если он не пустой, иначе третий объект
     * @param value         Основное (приоритетное) значение
     * @param defaultValue  Запасное значение (если первое - null)
     * @param defaultValue2 Запасное значение 2 (если и второе тоже - null)
     */
    public static <T> T nullReplace(T value, T defaultValue, T defaultValue2) {
        return value != null ? value : (defaultValue != null ? defaultValue : defaultValue2);
    }

    /**
     * Возвращает первый объект если он не пустой, иначе второй объект
     * @param value         Основное (приоритетное) значение
     * @param defaultValue  Запасное значение (если первое - null или пустое "")
     */
    public static String emptyReplace(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * Возвращает первый объект если он не пустой, иначе второй объект
     * @param value         Основное (приоритетное) значение
     * @param defaultValue  Запасное значение (если первое - null или пустое "")
     * @param defaultValue2 Запасное значение 2 (если и второе тоже - пустое)
     */
    public static String emptyReplace(String value, String defaultValue, String defaultValue2) {
        return emptyReplace(value, emptyReplace(defaultValue, defaultValue2));
    }
}
