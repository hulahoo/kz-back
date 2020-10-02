package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.UUID;

/**
 * Приложения (Интеграционный объект)
 */
@MetaClass(name = "tsadv$PersonAttachmentInt")
public class PersonAttachmentInt extends AbstractEntityInt {
    private static final long serialVersionUID = 2943442100008133194L;

    /**
     * Id категории
     */
    @MetaProperty
    protected UUID category;

    /**
     * Категория
     */
    @MetaProperty
    protected String categoryName;

    /**
     * Имя файла
     */
    @MetaProperty
    protected String filename;

    /**
     * Id файла
     */
    @MetaProperty
    protected UUID file;

    /**
     * Примечание
     */
    @MetaProperty
    protected String description;



    public void setCategory(UUID category) {
        this.category = category;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFile(UUID file) {
        this.file = file;
    }

    public UUID getFile() {
        return file;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}