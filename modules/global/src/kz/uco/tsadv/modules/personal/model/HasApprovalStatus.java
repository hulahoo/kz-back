package kz.uco.tsadv.modules.personal.model;

import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;

/**
 * @author veronika.buksha
 */
public interface HasApprovalStatus {
    DicApprovalStatus getStatus();

    void setStatus(DicApprovalStatus status);
}
