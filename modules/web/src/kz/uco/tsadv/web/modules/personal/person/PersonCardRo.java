package kz.uco.tsadv.web.modules.personal.person;

import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

public class PersonCardRo extends PersonCardNew {

    @Override
    protected void initVisibleComponent() {
        if (currentFrame != null) {
            EditableFrame editableFrame = (EditableFrame) currentFrame;
            editableFrame.editable(false);
        }
    }
}