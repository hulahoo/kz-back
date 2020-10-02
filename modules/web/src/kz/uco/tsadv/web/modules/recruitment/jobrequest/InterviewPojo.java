package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;

import java.util.Date;

/**
 * @author Adilbekov Yernar
 */
public class InterviewPojo {

    public InterviewPojo() {
    }

    public InterviewPojo(Interview interview, DateField dateField, TimeField timeFromField, TimeField timeToField, LookupField addressLookupField, OptionsGroup optionsGroup, LookupField lookupField, TextArea textArea, TextArea textAreaComment) {
        this.interview = interview;
        this.dateField = dateField;
        this.timeFromField = timeFromField;
        this.timeToField = timeToField;
        this.addressLookupField = addressLookupField;
        this.optionsGroup = optionsGroup;
        this.lookupField = lookupField;
        this.textArea = textArea;
        this.textAreaComment = textAreaComment;
    }

    private Interview interview;
    private DateField<Date> dateField;
    private TimeField<Date> timeFromField;
    private TimeField<Date> timeToField;
    private LookupField<Entity> addressLookupField;
    private OptionsGroup<Object, Object> optionsGroup;
    private LookupField<Entity> lookupField;
    private TextArea textArea;
    private TextArea<String> textAreaComment;
    private boolean deleted;
    private boolean find;

    public Interview getLatestInterview() {
        interview.setInterviewDate(dateField.getValue());
        interview.setTimeFrom(timeFromField.getValue());
        interview.setTimeTo(timeToField.getValue());
        interview.setPlace((DicLocation) addressLookupField.getValue());
        interview.setInterviewStatus((InterviewStatus) optionsGroup.getValue());
        interview.setInterviewReason((DicInterviewReason) lookupField.getValue());
//        interview.setReason(textArea.getValue());
        interview.setComment(textAreaComment.getValue());
        return interview;
    }

    public boolean isFind() {
        return find;
    }

    public void setFind(boolean find) {
        this.find = find;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public DateField getDateField() {
        return dateField;
    }

    public void setDateField(DateField dateField) {
        this.dateField = dateField;
    }

    public TimeField getTimeFromField() {
        return timeFromField;
    }

    public void setTimeFromField(TimeField timeFromField) {
        this.timeFromField = timeFromField;
    }

    public TimeField getTimeToField() {
        return timeToField;
    }

    public void setTimeToField(TimeField timeToField) {
        this.timeToField = timeToField;
    }

    public LookupField getAddressLookupField() {
        return addressLookupField;
    }

    public void setAddressLookupField(LookupField addressLookupField) {
        this.addressLookupField = addressLookupField;
    }

    public OptionsGroup getOptionsGroup() {
        return optionsGroup;
    }

    public void setOptionsGroup(OptionsGroup optionsGroup) {
        this.optionsGroup = optionsGroup;
    }

    public LookupField getLookupField() {
        return lookupField;
    }

    public void setLookupField(LookupField lookupField) {
        this.lookupField = lookupField;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public TextArea getTextAreaComment() {
        return textAreaComment;
    }

    public void setTextAreaComment(TextArea textAreaComment) {
        this.textAreaComment = textAreaComment;
    }
}
