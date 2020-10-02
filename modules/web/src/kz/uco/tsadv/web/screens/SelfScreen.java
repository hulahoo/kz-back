package kz.uco.tsadv.web.screens;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.service.RecognitionSchedulerService;

import javax.inject.Inject;
import java.util.Map;

public class SelfScreen extends AbstractWindow {

    @Inject
    private FieldGroup fg;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private WindowManager extWebWindowManager;

    @Inject
    private Metadata metadata;

    @Inject
    private RecognitionSchedulerService recognitionSchedulerService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        /*LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setStyleName("li-image-icon");

        *//*List<Integer> list = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            list.add(i);
        }

        lookupField.setOptionsList(list);*//*
        lookupField.setOptionIconProvider(new LookupField.OptionIconProvider<Integer>() {
            @Override
            public String getItemIcon(Integer item) {
                return "image-icon:./dispatch/person_image/default-avatar";
            }
        });


        add(lookupField);*/
    }

    public void openOrgTree() {
        openWindow("organization-tree", WindowManager.OpenType.DIALOG);
    }

    public void click() {
        recognitionSchedulerService.distributionOfPoints();
    }
}