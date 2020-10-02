package kz.uco.tsadv.web.gradessview;

import com.haulmont.cuba.gui.components.AbstractLookup;
import kz.uco.tsadv.global.common.CommonUtils;

import java.util.Map;

public class GradeSsViewBrowse extends AbstractLookup {
    @Override
    public void init(Map<String, Object> params) {
        params.putIfAbsent("date", CommonUtils.getSystemDate());
        super.init(params);
    }
}