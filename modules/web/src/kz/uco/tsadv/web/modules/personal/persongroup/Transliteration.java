package kz.uco.tsadv.web.modules.personal.persongroup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yelaman on 17.01.2019.
 */
public class Transliteration {

    public static String toTranslit(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        if (detectTextLanguage(text)) {
            for (int i = 0; i < text.length(); i++) {
                String l = text.substring(i, i + 1);
                sb.append(ruLetters.getOrDefault(l, l));
            }
        } else {
            for (int i = 0; i < text.length(); i++) {
                String l = text.substring(i, i + 1);
                if (l.equalsIgnoreCase("y") && i - 1 >= 0 && text.substring(i - 1, i).equalsIgnoreCase("a")) {
                    sb.append("й");
                } else {
                    sb.append(lettersEn.getOrDefault(l, l));
                }
            }
        }
        return sb.toString();
    }

    protected static boolean detectTextLanguage(String text) {
        Pattern pattern = Pattern.compile(
                "[" +                   //начало списка допустимых символов
                        "а-яА-ЯёЁ" +    //буквы русского алфавита
                        "\\d" +         //цифры
                        "\\s" +         //знаки-разделители (пробел, табуляция и т.д.)
                        "\\p{Punct}" +  //знаки пунктуации
                        "]" +                   //конец списка допустимых символов
                        "*");                   //допускается наличие указанных символов в любом количестве
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private static final Map<String, String> ruLetters = new HashMap<>();

    static {
        ruLetters.put("А", "A");
        ruLetters.put("Б", "B");
        ruLetters.put("В", "V");
        ruLetters.put("Г", "G");
        ruLetters.put("Д", "D");
        ruLetters.put("Е", "E");
        ruLetters.put("Ё", "Yo");
        ruLetters.put("Ж", "Zh");
        ruLetters.put("З", "Z");
        ruLetters.put("И", "I");
        ruLetters.put("Й", "I");
        ruLetters.put("К", "K");
        ruLetters.put("Л", "L");
        ruLetters.put("М", "M");
        ruLetters.put("Н", "N");
        ruLetters.put("О", "O");
        ruLetters.put("П", "P");
        ruLetters.put("Р", "R");
        ruLetters.put("С", "S");
        ruLetters.put("Т", "T");
        ruLetters.put("У", "U");
        ruLetters.put("Ф", "F");
        ruLetters.put("Х", "Kh");
        ruLetters.put("Ц", "C");
        ruLetters.put("Ч", "Ch");
        ruLetters.put("Ш", "Sh");
        ruLetters.put("Щ", "Sch");
        ruLetters.put("Ъ", "'");
        ruLetters.put("Ы", "Y");
        ruLetters.put("Ъ", "'");
        ruLetters.put("Э", "E");
        ruLetters.put("Ю", "Yu");
        ruLetters.put("Я", "Ya");
        ruLetters.put("а", "a");
        ruLetters.put("б", "b");
        ruLetters.put("в", "v");
        ruLetters.put("г", "g");
        ruLetters.put("д", "d");
        ruLetters.put("е", "e");
        ruLetters.put("ё", "e");
        ruLetters.put("ж", "zh");
        ruLetters.put("з", "z");
        ruLetters.put("и", "i");
        ruLetters.put("й", "i");
        ruLetters.put("к", "k");
        ruLetters.put("л", "l");
        ruLetters.put("м", "m");
        ruLetters.put("н", "n");
        ruLetters.put("о", "o");
        ruLetters.put("п", "p");
        ruLetters.put("р", "r");
        ruLetters.put("с", "s");
        ruLetters.put("т", "t");
        ruLetters.put("у", "u");
        ruLetters.put("ф", "f");
        ruLetters.put("х", "h");
        ruLetters.put("ц", "c");
        ruLetters.put("ч", "ch");
        ruLetters.put("ш", "sh");
        ruLetters.put("щ", "sch");
        ruLetters.put("ъ", "'");
        ruLetters.put("ы", "y");
        ruLetters.put("ъ", "'");
        ruLetters.put("э", "e");
        ruLetters.put("ю", "yu");
        ruLetters.put("я", "ya");
    }

    private static final Map<String, String> lettersEn = new HashMap<String, String>();

    static {
        lettersEn.put("A", "A");
        lettersEn.put("B", "Б");
        lettersEn.put("V", "В");
        lettersEn.put("G", "Г");
        lettersEn.put("D", "Д");
        lettersEn.put("E", "Е");
        lettersEn.put("Yo", "Ё");
        lettersEn.put("Zh", "Ж");
        lettersEn.put("Z", "З");
        lettersEn.put("I", "И");
        lettersEn.put("J", "Й");
        lettersEn.put("K", "К");
        lettersEn.put("L", "Л");
        lettersEn.put("M", "М");
        lettersEn.put("N", "Н");
        lettersEn.put("O", "О");
        lettersEn.put("P", "П");
        lettersEn.put("R", "Р");
        lettersEn.put("S", "С");
        lettersEn.put("T", "Т");
        lettersEn.put("U", "У");
        lettersEn.put("F", "Ф");
        lettersEn.put("H", "X");
        lettersEn.put("TS", "Ц");
//        lettersEn.put("C", "Ц");
        lettersEn.put("Ch", "Ч");
        lettersEn.put("Sh", "Ш");
        lettersEn.put("Shch", "Щ");
        lettersEn.put("'", "Ъ");
        lettersEn.put("Y", "Ы");
        lettersEn.put("'", "Ъ");
        lettersEn.put("E", "Э");
        lettersEn.put("Yu", "Ю");
        lettersEn.put("Ya", "Я");
        lettersEn.put("a", "а");
        lettersEn.put("b", "б");
        lettersEn.put("v", "в");
        lettersEn.put("g", "г");
        lettersEn.put("d", "д");
        lettersEn.put("e", "е");
        lettersEn.put("yo", "ё");
        lettersEn.put("zh", "ж");
        lettersEn.put("z", "з");
        lettersEn.put("i", "и");
        lettersEn.put("j", "й");
        lettersEn.put("k", "к");
        lettersEn.put("l", "л");
        lettersEn.put("m", "м");
        lettersEn.put("n", "н");
        lettersEn.put("o", "о");
        lettersEn.put("p", "п");
        lettersEn.put("r", "р");
        lettersEn.put("s", "с");
        lettersEn.put("t", "т");
        lettersEn.put("u", "у");
        lettersEn.put("f", "ф");
        lettersEn.put("h", "х");
        lettersEn.put("ts", "ц");
        lettersEn.put("ch", "ч");
        lettersEn.put("sh", "ш");
        lettersEn.put("shch", "щ");
        lettersEn.put("'", "ъ");
        lettersEn.put("y", "ы");
        lettersEn.put("'", "ъ");
        lettersEn.put("e", "э");
        lettersEn.put("yu", "ю");
        lettersEn.put("ya", "я");
    }
}
