package kz.uco.tsadv.global.common;

import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

/**
 * Process which can perform some action
 * Or/And which can perform the list of subProcesses
 * And which could be a part of another process
 * @author Felix Kamalov
 * @version 1.0
 */
public class PersonRemoveProcess extends AbstractProcess {

    /** constructor */
    public PersonRemoveProcess(PersonGroupExt personGroup) {
        super(personGroup);
    }

    /** Perform all added checks and actions for various modules (such as HR or CRM)
     * and perform remove Assignment and group< and Persona and group */
    @Override
    public ProcessResult performAction() {
        ProcessResult result = super.performAction();

        if (!result.isSuccess()) {
            return result;
        }

        // del Ass
        // del AssGr
        // del Per
        // del PerGr

        return result;
    }

}
