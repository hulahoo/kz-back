package kz.uco.tsadv.web.book;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.config.BookConfig;
import kz.uco.tsadv.entity.Book;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

public class BookEdit extends AbstractEditor<Book> {
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField uploadField;
    @Inject
    protected Button clearImageBtn;
    @Inject
    protected Datasource<Book> bookDs;
    @Named("fieldGroup.pdf")
    protected FileUploadField pdfField;
    @Named("fieldGroup.mobi")
    protected FileUploadField mobiField;
    @Named("fieldGroup.kf8")
    protected FileUploadField kf8Field;
    @Named("fieldGroup.fb2")
    protected FileUploadField fb2Field;
    @Named("fieldGroup.epub")
    protected FileUploadField epubField;
    @Named("fieldGroup.djvu")
    protected FileUploadField djvuField;
    @Inject
    protected Image image;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected BookConfig bookConfig;

    @Override
    public void init(Map<String, Object> params) {  // todo anuar 0 родителя зачем удалил?
        addImageListeners();


        //Принимает максимальный размер файла в Мб  // todo anuar 1 вынести в 1 метод
        String configFileSizeLimit = bookConfig.getFileSizeLimit();
        int fileSizeLimit;
        try {
            fileSizeLimit = Integer.parseInt(configFileSizeLimit) * 1024 * 1024;
        } catch (NumberFormatException e) {
            fileSizeLimit = 100 * 1024 * 1024;
            showNotification("Введите числовое значение максимального размера файлов (Мб) в Свойствах приложения!",
                    NotificationType.TRAY);
        }

        pdfField.setFileSizeLimit(fileSizeLimit);
        pdfField.setPermittedExtensions(Collections.singleton(".pdf"));

        epubField.setFileSizeLimit(fileSizeLimit);
        epubField.setPermittedExtensions(Collections.singleton(".epub"));

        fb2Field.setFileSizeLimit(fileSizeLimit);
        fb2Field.setPermittedExtensions(Collections.singleton(".fb2"));

        kf8Field.setFileSizeLimit(fileSizeLimit);
        kf8Field.setPermittedExtensions(Collections.singleton(".kf8"));

        mobiField.setFileSizeLimit(fileSizeLimit);
        mobiField.setPermittedExtensions(Collections.singleton(".mobi"));

        djvuField.setFileSizeLimit(fileSizeLimit);
        djvuField.setPermittedExtensions(Collections.singleton(".djvu"));
    }

    protected void addImageListeners() {
        uploadField.addFileUploadSucceedListener(event -> {
            FileDescriptor fd = uploadField.getFileDescriptor();
            try {
                fileUploadingAPI.putFileIntoStorage(uploadField.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException("Error saving file to FileStorage", e);
            }
            getItem().setImage(dataSupplier.commit(fd));
            displayImage();
        });

        uploadField.addFileUploadErrorListener(event ->
                showNotification("Загрузка не удалась", NotificationType.HUMANIZED));

        bookDs.addItemPropertyChangeListener(event -> {
            if ("image".equals(event.getProperty())) updateImageButtons(event.getValue() != null);
        });
    }


    @Override
    protected void postInit() { // todo anuar 0 родителя вызов зачем удалил?
        displayImage();
        updateImageButtons(getItem().getImage() != null);
    }

    private void updateImageButtons(boolean enable) {
        clearImageBtn.setEnabled(enable);
    }

    protected void displayImage() {
        if (getItem().getImage() != null) {
            image.setSource(FileDescriptorResource.class).setFileDescriptor(getItem().getImage());
        } else {
            image.setSource(ThemeResource.class)
                    .setPath(bookConfig.getDefaultImage());
        }
    }

    public void onClearImageBtnClick() {
        getItem().setImage(null);
        displayImage();
    }
}