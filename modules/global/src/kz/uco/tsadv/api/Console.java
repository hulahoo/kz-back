package kz.uco.tsadv.api;

/**
 * Содержит статический метод для вывода текста на консоль. Обёртка для System.out.println(String s)
 * @author  Felix Kamalov
 * @version 1.0
 */
public final class Console {
    /**
     * Вывод объекта на консоль (System.out.println)
     * @param object Объект для вывода на консоль
     */
    public static void out(Object object) {
        System.out.println(object);
    }
}
