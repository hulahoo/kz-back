package kz.uco.tsadv.modules.personal.model;

import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

/**
 * @author veronika.buksha
 */
public interface HasPersonGroup {
    PersonGroupExt getPersonGroup();

    void setPersonGroup(PersonGroupExt personGroup);
}
