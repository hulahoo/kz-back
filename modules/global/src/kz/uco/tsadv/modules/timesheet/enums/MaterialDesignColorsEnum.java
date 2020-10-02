package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum MaterialDesignColorsEnum implements EnumClass<Integer> {

    RED(1, "#f44336", "white"),
    PINK(2, "#E91E63", "white"),
    PURPLE(3, "#9C27B0", "white"),
    DEEP_PURPLE(4, "#673AB7", "white"),
    INDIGO(5, "#3F51B5", "white"),
    BLUE(6, "#2196F3", "white"),
    LIGHT_BLUE(7, "#03A9F4", "white"),
    CYAN(8, "#00BCD4", "white"),
    TEAL(9, "#009688", "white"),
    GREEN(10, "#4CAF50", "white"),
    LIGHT_GREEN(11, "#8BC34A", "white"),
    LIME(12, "#CDDC39", "black"),
    YELLOW(13, "#FFEB3B", "black"),
    AMBER(14, "#FFC107", "black"),
    ORANGE(15, "#FF9800", "white"),
    DEEP_ORANGE(16, "#FF5722", "white"),
    BROWN(17, "#795548", "white"),
    GREY(18, "#9E9E9E", "white"),
    BLUE_GREY(19, "#607D8B", "white");

    private Integer id;
    private String colorHex;
    private String fontColor;

    MaterialDesignColorsEnum(Integer id, String colorHex, String fontColor) {
        this.id = id;
        this.colorHex = colorHex;
        this.fontColor = fontColor;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    @Nullable
    public static MaterialDesignColorsEnum fromId(Integer id) {
        for (MaterialDesignColorsEnum at : MaterialDesignColorsEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}

