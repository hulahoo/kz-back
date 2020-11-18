package kz.uco.tsadv.web.modules.personal.common;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author veronika.buksha
 */
//todo
public class Utils {
    protected static Messages messages = AppBeans.get(Messages.class);

    public static Embedded getPersonImageTemplate(PersonExt person, String imageHeight, Embedded displayComponent) {
        getPersonImageEmbedded(person, imageHeight, displayComponent);
        displayComponent.setStyleName("");
        return displayComponent;
    }

    public static Embedded getPersonImageEmbedded(PersonExt person, String imageHeight, Embedded displayComponent) {
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        if (person == null) {
            if (displayComponent != null)
                displayComponent.resetSource();
            return null;
        }

        Embedded embedded = displayComponent;
        if (embedded == null)
            embedded = (Embedded) componentsFactory.createComponent(Embedded.NAME);

        /*if (person.getPhoto() == null)
            embedded.setSource("theme://images/no-avatar.png");
        else
            embedded.setSource("pi-" + person.getId(), new ByteArrayInputStream(person.getPhoto()));*/
        embedded.setType(Embedded.Type.IMAGE);

        embedded.addStyleName("b-user-image");

        if (imageHeight != null)
            embedded.setHeight(imageHeight);
        embedded.setWidth(imageHeight);

        return embedded;
    }

    public static Component getFileDownload(FileDescriptor fd, Frame frame) {
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        CssLayout layout = componentsFactory.createComponent(CssLayout.class);

        if (fd != null) {
            Label l = componentsFactory.createComponent(Label.class);
            l.setValue(fd.getName());
            l.setStyleName("tal-filename-label");
            Button b = componentsFactory.createComponent(Button.class);
            b.setAction(new BaseAction("theAction") {
                @Override
                public void actionPerform(Component component) {
                    AppConfig.createExportDisplay(frame).show(fd, null);
                }

                @Override
                public String getCaption() {
                    return "";
                }
            });
            b.setIcon("icons/download.png");

            layout.add(b);
            layout.add(l);
        }

        return layout;
    }


    public static Embedded getCourseImageEmbedded(Course course, String imageHeight, Embedded displayComponent) {
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        if (course == null) {
            if (displayComponent != null)
                displayComponent.resetSource();
            return null;
        }

        Embedded embedded = displayComponent;
        if (embedded == null)
            embedded = (Embedded) componentsFactory.createComponent(Embedded.NAME);

        if (course.getLogo() == null)
            embedded.setSource("theme://images/course-min.png");
        else
            embedded.setSource("ci-" + course.getId(), new ByteArrayInputStream(course.getLogo()));
        embedded.setType(Embedded.Type.IMAGE);

        embedded.addStyleName("b-course-image");

        if (imageHeight != null)
            embedded.setHeight(imageHeight);
        embedded.setWidth(imageHeight);

        return embedded;
    }

    public static Embedded getCourseCategoryImageEmbedded(DicCategory dicCategory, String imageHeight, Embedded displayComponent) {
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        if (dicCategory == null) {
            if (displayComponent != null)
                displayComponent.resetSource();
            return null;
        }

        Embedded embedded = displayComponent;
        if (embedded == null)
            embedded = (Embedded) componentsFactory.createComponent(Embedded.NAME);

        if (dicCategory.getImage() == null)
            embedded.setSource("theme://images/default-category.jpg");
        else
            embedded.setSource("cci-" + dicCategory.getId(), new ByteArrayInputStream(dicCategory.getImage()));
        embedded.setType(Embedded.Type.IMAGE);
        embedded.addStyleName("b-course-category-image");

        if (imageHeight != null) {
            embedded.setHeight(imageHeight);
            embedded.setWidth(imageHeight);
        }

        return embedded;
    }

    public static void customizeLookup(Component component, String lookupScreen, WindowManager.OpenType openType, Map<String, Object> lookupParams) {
        if (component instanceof PickerField) {
            PickerField.LookupAction lookupAction = ((PickerField) component).getLookupAction();
            lookupAction.setLookupScreenOpenType(openType);
            lookupAction.setLookupScreenParams(lookupParams);
            if (lookupScreen != null)
                lookupAction.setLookupScreen(lookupScreen);
        }
    }

    public static void customizeLookupPickerField(Component component, String lookupScreen, WindowManager.OpenType openType, Map<String, Object> lookupParams) {
        if (component instanceof LookupPickerField) {
            LookupPickerField.LookupAction lookupAction = ((LookupPickerField) component).getLookupAction();
            lookupAction.setLookupScreenOpenType(openType);
            lookupAction.setLookupScreenParams(lookupParams);
            if (lookupScreen != null)
                lookupAction.setLookupScreen(lookupScreen);
        }
    }

    public static <T extends AbstractTimeBasedEntity & IGroupedEntity, C extends AbstractFrame, E extends AbstractHrEditor<T>> void
    editHistory(T timeBaseEntity, List<T> timeBaseEntityList, C controller, CollectionDatasource groupDs) {
        timeBaseEntityList.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (timeBaseEntity != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", timeBaseEntityList.indexOf(timeBaseEntity) == 0);
            paramsMap.put("lastRow", timeBaseEntityList.indexOf(timeBaseEntity) == timeBaseEntityList.size() - 1);

            E editor = (E) controller.openEditor("tsadv$" + timeBaseEntity.getClass().getSimpleName() + ".edit", timeBaseEntity, WindowManager.OpenType.THIS_TAB, paramsMap);
            editor.addCloseListener(actionId -> groupDs.refresh());
        }
    }

    public static <T extends AbstractTimeBasedEntity, C extends AbstractFrame> void
    removeHistory(CollectionDatasource<T, UUID> timeBaseEntityDs, C controller, CollectionDatasource groupDs) {
        T item = timeBaseEntityDs.getItem();
        List<T> items = new ArrayList<>(timeBaseEntityDs.getItems());
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            controller.showNotification(messages.getMessage("kz.uco.tsadv.web", "removeHistory.deny"));
        } else {
            controller.showOptionDialog(messages.getMessage("kz.uco.tsadv.web", "removeDialog.confirm.title"), messages.getMessage("kz.uco.tsadv.web", "removeDialog.confirm.text"),
                    Frame.MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        timeBaseEntityDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        timeBaseEntityDs.modifyItem(items.get(index + 1));
                                    }

                                    timeBaseEntityDs.removeItem(item);

                                    timeBaseEntityDs.getDsContext().commit();
                                    if (timeBaseEntityDs.getItems().isEmpty()) {
                                        groupDs.removeItem(groupDs.getItem());
                                        groupDs.getDsContext().commit();
                                    }

                                    groupDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public static Component generateLanguageField(Datasource datasource, String fieldId) {
        LookupField lookupField = AppBeans.get(ComponentsFactory.class).createComponent(LookupField.class);
        lookupField.setDatasource(datasource, fieldId);
        lookupField.setOptionsMap(CommonUtils.getSystemLangsMap());
        lookupField.setNullOptionVisible(false);
        return lookupField;
    }

    public static void resetDsContext(DsContext dsContext) {
        for (Datasource datasource : dsContext.getAll()) {
            ((AbstractDatasource) datasource).setModified(false);
        }
    }
}
