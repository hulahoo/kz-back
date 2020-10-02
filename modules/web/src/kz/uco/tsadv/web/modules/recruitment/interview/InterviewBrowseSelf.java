package kz.uco.tsadv.web.modules.recruitment.interview;

import java.util.Map;

public class InterviewBrowseSelf extends InterviewBrowse {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected boolean isFromSelfService() {
        return true;
    }
}