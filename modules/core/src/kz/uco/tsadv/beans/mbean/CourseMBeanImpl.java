package kz.uco.tsadv.beans.mbean;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.app.Authenticated;
import kz.uco.tsadv.entity.ImageSize;
import kz.uco.tsadv.service.ImageResizeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component("tsadv_CourseMBean")
public class CourseMBeanImpl implements CourseMBean {

    @Inject
    private DataManager dataManager;

    @Inject
    private ImageResizeService imageResizeService;

    @Authenticated
    @Override
    public String updateCoursesLogo() {
        final Integer createdNewFiles = dataManager.loadList(LoadContext.create(ImageSize.class)
                .setQuery(LoadContext.createQuery("" +
                        "select e " +
                        "from tsadv_ImageSize e"))
                .setView(View.LOCAL))
                .stream()
                .map(is -> {
                    final List<FileDescriptor> notResizedLogos = dataManager.loadList(LoadContext.create(FileDescriptor.class)
                            .setQuery(LoadContext.createQuery("" +
                                    "select c.logo " +
                                    "from tsadv$Course c " +
                                    "where c.logo is not null and c.logo.id not in (" +
                                    "   select i.originalImage.id " +
                                    "   from tsadv_ResizedImage i " +
                                    "   where i.size.id = :sizeId )")
                                    .setParameter("sizeId", is.getId()))
                            .setView(View.MINIMAL));
                    notResizedLogos.forEach(f ->imageResizeService.resizeImage(is, f));
                    return notResizedLogos.size();
                })
                .reduce(0, Integer::sum);
        return String.format("Created new images: %d", createdNewFiles);
    }
}