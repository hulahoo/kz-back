package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicOperatorCode;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(UcoCommonService.NAME)
public class UcoCommonServiceBean implements UcoCommonService {

    @Inject
    private DataManager dataManager;

    @Override
    public List<DicOperatorCode> fillPhoneCodes() {
        LoadContext<DicOperatorCode> loadContext = LoadContext.create(DicOperatorCode.class);
        loadContext.setQuery(loadContext.createQuery(
                "select e from tsadv$DicOperatorCode e where :sysDate between e.startDate and e.endDate")
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }

    @Override
    public FileDescriptor getFileDescriptor(UUID fileDescriptorId) {
        LoadContext<FileDescriptor> loadContext = LoadContext.create(FileDescriptor.class).setId(fileDescriptorId).setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

}