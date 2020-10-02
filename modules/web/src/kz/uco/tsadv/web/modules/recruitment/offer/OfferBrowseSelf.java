package kz.uco.tsadv.web.modules.recruitment.offer;

import java.util.Map;

public class OfferBrowseSelf extends OfferBrowse {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected boolean isSelfService() {
        return true;
    }
}