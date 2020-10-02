package kz.uco.tsadv.web.modules.recognition;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.widgets.CubaWindow;
import com.vaadin.event.ShortcutListener;
import kz.uco.tsadv.modules.recognition.dictionary.DicDeliveryAddress;
import kz.uco.tsadv.service.RecognitionService;

import java.util.Collections;
import java.util.List;

/**
 * @author adilbekov.yernar
 */
public class RecognitionHelper {

    protected ComponentsFactory componentsFactory;

    protected Messages messages;

    protected AppUI appUI;

    protected DataManager dataManager;

    protected DicDeliveryAddress dicDeliveryAddress;

    protected RecognitionService recognitionService;

    public interface RcgWindowCloseListener {
        void close(String action);
    }

    public RecognitionHelper() {
        this.componentsFactory = AppBeans.get(ComponentsFactory.class);
        this.appUI = AppUI.getCurrent();
        this.messages = AppBeans.get(Messages.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.recognitionService = AppBeans.get(RecognitionService.class);
    }

    public void showNotificationWithTitle(String title, String body) {
        CubaWindow cubaWindow = new CubaWindow();
        cubaWindow.setStyleName("rcg-notify-window rcg-notify-gt");
        cubaWindow.setModal(true);
        cubaWindow.setWidth("300px");
        cubaWindow.setHeightUndefined();
        VBoxLayout layout = componentsFactory.createComponent(VBoxLayout.class);
        layout.setAlignment(Component.Alignment.MIDDLE_CENTER);
        layout.setWidthFull();
        layout.setStyleName("rcg-notify-gt-content");
        layout.setSpacing(true);

        Label titleLabel = componentsFactory.createComponent(Label.class);
        titleLabel.setStyleName("rcg-notify-gt-title");
        titleLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        titleLabel.setWidthFull();
        titleLabel.setHtmlEnabled(true);
        titleLabel.setValue(title);

        layout.add(titleLabel);

        Label bodyLabel = componentsFactory.createComponent(Label.class);
        bodyLabel.setStyleName("rcg-notify-gt-text gt-text-left");
        bodyLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        bodyLabel.setWidthFull();
        bodyLabel.setHtmlEnabled(true);
        bodyLabel.setValue(body);
        layout.add(bodyLabel);

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setWidthFull();
        linkButton.setCaption("Ok");
        linkButton.setStyleName("rcg-notify-gt-btn rcg-btn rcg-blue-btn rcg-btn-md");
        linkButton.setAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                cubaWindow.close();
            }
        });
        layout.add(linkButton);

        cubaWindow.setContent(layout.unwrap(com.vaadin.ui.Component.class));

        cubaWindow.addAction(new ShortcutListener("Enter",
                com.vaadin.event.ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                cubaWindow.close();
            }
        });
        appUI.addWindow(cubaWindow);
        cubaWindow.center();
        cubaWindow.focus();
    }

    public void showNotificationWindow(String text, String imageUrl) {
        CubaWindow cubaWindow = new CubaWindow();
        cubaWindow.setStyleName("rcg-notify-window rcg-notify-gt");
        cubaWindow.setModal(true);
        cubaWindow.setWidth("300px");
        cubaWindow.setHeightUndefined();
        VBoxLayout layout = componentsFactory.createComponent(VBoxLayout.class);
        layout.setAlignment(Component.Alignment.MIDDLE_CENTER);
        layout.setWidthFull();
        layout.setStyleName("rcg-notify-gt-content");
        layout.setSpacing(true);

        Image image = componentsFactory.createComponent(Image.class);
        image.setAlignment(Component.Alignment.MIDDLE_CENTER);
        image.setStyleName("rcg-notify-gt-image");
        image.setScaleMode(Image.ScaleMode.FILL);
        image.setSource(ThemeResource.class).setPath(imageUrl);
        image.setWidth("70px");
        image.setHeight("70px");
        layout.add(image);

        Label label = componentsFactory.createComponent(Label.class);
        label.setStyleName("rcg-notify-gt-text");
        label.setAlignment(Component.Alignment.MIDDLE_CENTER);
        label.setWidthFull();
        label.setHtmlEnabled(true);
        label.setValue(text);
        layout.add(label);

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setWidthFull();
        linkButton.setCaption("Ok");
        linkButton.setStyleName("rcg-notify-gt-btn rcg-btn rcg-blue-btn rcg-btn-md");
        linkButton.setAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                cubaWindow.close();
            }
        });
        layout.add(linkButton);

        cubaWindow.setContent(layout.unwrap(com.vaadin.ui.Component.class));

        cubaWindow.addAction(new ShortcutListener("Enter",
                com.vaadin.event.ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                cubaWindow.close();
            }
        });
        appUI.addWindow(cubaWindow);
        cubaWindow.center();
        cubaWindow.focus();
    }

    public void showCheckoutConfirmWindow(Integer totalSum, RcgWindowCloseListener rcgWindowCloseListener) {
        CubaWindow cubaWindow = new CubaWindow();
        cubaWindow.setClosable(false);
        cubaWindow.setStyleName("rcg-notify-window rcg-notify-gt");
        cubaWindow.setModal(true);
        cubaWindow.setWidth("410px");
        cubaWindow.setHeightUndefined();

        VBoxLayout layout = componentsFactory.createComponent(VBoxLayout.class);
        layout.setAlignment(Component.Alignment.MIDDLE_CENTER);
        layout.setWidthFull();
        layout.setStyleName("rcg-notify-gt-content");
        layout.setSpacing(true);

        Label titleLabel = componentsFactory.createComponent(Label.class);
        titleLabel.setStyleName("rcg-notify-gt-title");
        titleLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        titleLabel.setWidthFull();
        titleLabel.setHtmlEnabled(true);
        titleLabel.setValue(getMessage("checkout.confirm.title"));

        layout.add(titleLabel);

        HBoxLayout formBox = componentsFactory.createComponent(HBoxLayout.class);
        formBox.setStyleName("rcg-cart-checkout-content");
        formBox.setWidthFull();

        Label confirmText = componentsFactory.createComponent(Label.class);
        confirmText.setStyleName("rcg-notify-gt-text gt-text-left rcg-cart-checkout-text");
        confirmText.setAlignment(Component.Alignment.MIDDLE_LEFT);
        confirmText.setHtmlEnabled(true);
        confirmText.setValue(getMessage("checkout.confirm.text"));
        formBox.add(confirmText);
        formBox.expand(confirmText);

        Label totalSumLabel = componentsFactory.createComponent(Label.class);
        totalSumLabel.setStyleName("");
        totalSumLabel.setAlignment(Component.Alignment.MIDDLE_RIGHT);
        totalSumLabel.setHtmlEnabled(true);
        totalSumLabel.setValue("<div class=\"rcg-cart-checkout-price\"><i></i>" + totalSum + "<span>HC</span></div>");
        formBox.add(totalSumLabel);

        HBoxLayout formBox2 = componentsFactory.createComponent(HBoxLayout.class);
        formBox2.setStyleName("rcg-delivery-address-form");
        formBox2.setWidthFull();

        Label deliveryAddressLabel = componentsFactory.createComponent(Label.class);
        deliveryAddressLabel.setStyleName("rcg-delivery-address-label");
        deliveryAddressLabel.setAlignment(Component.Alignment.MIDDLE_LEFT);
        deliveryAddressLabel.setValue(getMessage("checkout.address"));
        formBox2.add(deliveryAddressLabel);


        LoadContext loadContext = new LoadContext(DicDeliveryAddress.class);
        LoadContext.Query query = LoadContext.createQuery("select e from tsadv$DicDeliveryAddress e");
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);

        List<DicDeliveryAddress> list = dataManager.loadList(loadContext);

        LookupField<Object> lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setCaptionMode(CaptionMode.PROPERTY);
        lookupField.setRequired(true);
        lookupField.setWidth("240px");
        lookupField.setStyleName("");
        lookupField.setOptionsList(Collections.singletonList(list));

        lookupField.addValueChangeListener(valueChangeEvent -> {
            dicDeliveryAddress = (DicDeliveryAddress) valueChangeEvent.getValue();
        });
        formBox2.add(lookupField);
        formBox2.expand(deliveryAddressLabel);

        layout.add(formBox);
        layout.add(formBox2);
        if (recognitionService.isContainsVoucher(null)) {
            formBox2.setVisible(false);
        }
        LinkButton issueLink = componentsFactory.createComponent(LinkButton.class);
        issueLink.setAlignment(Component.Alignment.MIDDLE_CENTER);
        issueLink.setWidthFull();
        issueLink.setCaption(getMessage("checkout.confirm.issue"));
        issueLink.setStyleName("rcg-notify-gt-btn rcg-btn rcg-blue-btn rcg-btn-md");
        issueLink.setAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                rcgWindowCloseListener.close("issue");
                cubaWindow.close();
            }
        });
        layout.add(issueLink);

        LinkButton cancelLink = componentsFactory.createComponent(LinkButton.class);
        cancelLink.setAlignment(Component.Alignment.MIDDLE_CENTER);
        cancelLink.setWidthFull();
        cancelLink.setCaption(getMessage("checkout.confirm.cancel"));
        cancelLink.setStyleName("rcg-notify-gt-btn rcg-btn rcg-white-btn rcg-btn-md");
        cancelLink.setAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                rcgWindowCloseListener.close("cancel");
                cubaWindow.close();
            }
        });
        layout.add(cancelLink);

        cubaWindow.setContent(layout.unwrap(com.vaadin.ui.Component.class));

        appUI.addWindow(cubaWindow);
        cubaWindow.center();
        cubaWindow.focus();
    }

    public void showSuccessCheckout(RcgWindowCloseListener rcgWindowCloseListener) {
        CubaWindow cubaWindow = new CubaWindow();
        cubaWindow.setClosable(false);
        cubaWindow.addCloseListener(new com.vaadin.ui.Window.CloseListener() {
            @Override
            public void windowClose(com.vaadin.ui.Window.CloseEvent e) {
                rcgWindowCloseListener.close("close");
            }
        });
        cubaWindow.setStyleName("rcg-notify-window rcg-notify-gt");
        cubaWindow.setModal(true);
        cubaWindow.setWidth("300px");
        cubaWindow.setHeightUndefined();
        VBoxLayout layout = componentsFactory.createComponent(VBoxLayout.class);
        layout.setAlignment(Component.Alignment.MIDDLE_CENTER);
        layout.setWidthFull();
        layout.setStyleName("rcg-notify-gt-content");
        layout.setSpacing(true);

        Image image = componentsFactory.createComponent(Image.class);
        image.setAlignment(Component.Alignment.MIDDLE_CENTER);
        image.setStyleName("rcg-notify-gt-image");
        image.setScaleMode(Image.ScaleMode.FILL);
        image.setSource(ThemeResource.class).setPath("images/recognition/ch-success.png");
        image.setWidth("70px");
        image.setHeight("70px");
        layout.add(image);

        Label titleLabel = componentsFactory.createComponent(Label.class);
        titleLabel.setStyleName("rcg-notify-gt-title");
        titleLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        titleLabel.setWidthFull();
        titleLabel.setHtmlEnabled(true);
        titleLabel.setValue(getMessage("checkout.success.title"));

        layout.add(titleLabel);

        Label bodyLabel = componentsFactory.createComponent(Label.class);
        bodyLabel.setStyleName("rcg-notify-gt-text gt-text-left");
        bodyLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        bodyLabel.setWidthFull();
        bodyLabel.setHtmlEnabled(true);
        bodyLabel.setValue(getMessage("checkout.success.body"));
        layout.add(bodyLabel);

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setWidthFull();
        linkButton.setCaption("Ok");
        linkButton.setStyleName("rcg-notify-gt-btn rcg-btn rcg-blue-btn rcg-btn-md");
        linkButton.setAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                cubaWindow.close();
            }
        });
        layout.add(linkButton);

        cubaWindow.setContent(layout.unwrap(com.vaadin.ui.Component.class));

        cubaWindow.addAction(new ShortcutListener("Enter",
                com.vaadin.event.ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                cubaWindow.close();
            }
        });
        appUI.addWindow(cubaWindow);
        cubaWindow.center();
        cubaWindow.focus();
    }

    protected String getMessage(String key) {
        return messages.getMessage("kz.uco.tsadv.web.modules.recognition", key);
    }

    public DicDeliveryAddress getDicDeliveryAddress() {
        return dicDeliveryAddress;
    }
}
