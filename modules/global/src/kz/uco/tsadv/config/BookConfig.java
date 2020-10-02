package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface BookConfig extends Config {
    @Property("tal.learning.books.defaultImage")
    @DefaultString("images/default-book.jpeg")
    String getDefaultImage();


    @Property("tal.learning.books.fileSizeLimit")
    @DefaultString("100")
    String getFileSizeLimit();
}
