package kz.uco.tsadv.web.modules.recognition.entity.awardprogram;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.AwardProgram;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class AwardProgramBrowse extends AbstractLookup {

    @Inject
    private Button showPersonAwardsBtn;

    @Inject
    private GroupDatasource<AwardProgram, UUID> awardProgramsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        awardProgramsDs.addItemChangeListener(e -> showPersonAwardsBtn.setEnabled(e.getItem() != null));
    }

    public void showPersonAwards() {
        AwardProgram awardProgram = awardProgramsDs.getItem();
        if (awardProgram != null) {
            openWindow("award-program-persons",
                    WindowManager.OpenType.DIALOG,
                    ParamsMap.of("awardProgram", awardProgram));
        }
    }
}