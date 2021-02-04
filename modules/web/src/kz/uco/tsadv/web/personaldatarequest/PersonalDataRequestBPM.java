package kz.uco.tsadv.web.personaldatarequest;

import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;

public class PersonalDataRequestBPM<T extends PersonalDataRequest> extends AbstractBpmEditor<T> {

    /*public static final String PROCESS_NAME = "personalData";

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected VBoxLayout vData;
    @Inject
    protected BpmService bpmService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    protected Table<FileDescriptor> attachmentTable;
    @Inject
    protected CollectionDatasource<FileDescriptor, UUID> attachmentsDs;

    @Override
    protected void postInit() {
        super.postInit();
        drawChangedValue();
    }

    @Override
    public void ready() {
        super.ready();

        FileDescriptor attachment = getItem().getAttachment();
        if (attachment != null && attachmentsDs.getItems().stream().noneMatch(fileDescriptor -> fileDescriptor.equals(attachment)))
            attachmentsDs.addItem(attachment);

        attachmentTable.setVisible(!attachmentsDs.getItems().isEmpty());
    }

    protected void drawChangedValue() {
        final PersonalDataRequest item = getItem();
        final PersonExt person = employeeService.getPersonByPersonGroup(item.getPersonGroup().getId(),
                bpmService.getDate(item.getUpdateTs() != null ? item.getUpdateTs() : item.getCreateTs(), item.getStatus()), "person.full");

        *//** FIO *//*
        if (isChanged(person.getFirstName(), item.getFirstName()))
            draw(person.getFirstName(), item.getFirstName(), "FirstName");
        if (isChanged(person.getMiddleName(), item.getMiddleName()))
            draw(person.getMiddleName(), item.getMiddleName(), "MiddleName");
        if (isChanged(person.getLastName(), item.getLastName()))
            draw(person.getLastName(), item.getLastName(), "LastName");
        *//** FIO Latin*//*
        if (isChanged(person.getFirstNameLatin(), item.getFirstNameLatin()))
            draw(person.getFirstNameLatin(), item.getFirstNameLatin(), "FirstNameLatin");
        if (isChanged(person.getMiddleNameLatin(), item.getMiddleNameLatin()))
            draw(person.getMiddleNameLatin(), item.getMiddleNameLatin(), "MiddleNameLatin");
        if (isChanged(person.getLastNameLatin(), item.getLastNameLatin()))
            draw(person.getLastNameLatin(), item.getLastNameLatin(), "LastNameLatin");
        *//** birth_date and marital_status *//*
        if (isChanged(person.getDateOfBirth(), item.getDateOfBirth()))
            draw(person.getDateOfBirth(), item.getDateOfBirth(), "DateOfBirth");
        if (isChanged(person.getMaritalStatus(), item.getMaritalStatus()))
            draw(person.getMaritalStatus(), item.getMaritalStatus(), "MaritalStatus");
    }

    protected boolean isChanged(Object o1, Object o2) {
        return !Objects.equals(o1, o2);
    }

    protected void draw(Object o1, Object o2, String name) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setWidth("100%");
        hBoxLayout.setSpacing(true);

        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(getMessage(name));
        label.setWidth("100%");

        TextField textField1 = componentsFactory.createComponent(TextField.class);
        textField1.setValue(o1);
        textField1.setWidth("100%");
        textField1.setEditable(false);

        TextField textField2 = componentsFactory.createComponent(TextField.class);
        textField2.setValue(o2);
        textField2.setWidth("100%");
        textField2.setEditable(false);

        hBoxLayout.add(label);
        hBoxLayout.add(textField1);
        hBoxLayout.add(textField2);

        vData.add(hBoxLayout);
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }

    public Component generatorName(FileDescriptor fd) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(fd.getName());
        linkButton.setAction(new BaseAction("export") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                exportDisplay.show(fd, ExportFormat.OCTET_STREAM);
            }
        });
        return linkButton;
    }*/
}