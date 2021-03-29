package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_PORTAL_FEEDBACK_QUESTION")
@Entity(name = "tsadv_DicPortalFeedbackQuestion")
public class DicPortalFeedbackQuestion extends AbstractDictionary {
    private static final long serialVersionUID = -407950754725617254L;
}