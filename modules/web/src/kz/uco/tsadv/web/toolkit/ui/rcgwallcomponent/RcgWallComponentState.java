package kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent;

import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.modules.recognition.pojo.RcgQuestionPojo;
import kz.uco.tsadv.web.toolkit.ui.RcgComponentState;

public class RcgWallComponentState extends RcgComponentState {

    public int wallType;

    public ProfilePojo currentProfilePojo;

    public ProfilePojo profilePojo;

    public RcgQuestionPojo questionPojo;

    public int accessNominee = 0;

    public String chartFeedbackTypes;
}
