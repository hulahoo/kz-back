package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class BookPojo extends MaterialPojo implements Serializable {
    protected List<SimplePojo> fileTypes;

    public List<SimplePojo> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<SimplePojo> fileTypes) {
        this.fileTypes = fileTypes;
    }
}

