package kz.uco.tsadv.service;


import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import java.util.Date;

public interface SelfService {
    String NAME = "tsadv_SelfService";

    void checkDocumentEndExpiredDate();

    PositionSsView getPositionSsView(PositionGroupExt positionGroupExt, Date date);

    OrganizationSsView getOrganizationSsView(OrganizationGroupExt organizationGroupExt, Date date);
}