package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.JavaScript;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.common.TokenHelper;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.components.RcgEventBean;
import kz.uco.tsadv.config.RecognitionConfig;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.RecognitionProfileSetting;
import kz.uco.tsadv.service.RecognitionService;
import kz.uco.tsadv.service.UserService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RecognitionWindow extends AbstractTopLevelWindow {

    private static final String PROFILE_SETTINGS_CHECKED = "PROFILE_SETTINGS_CHECKED";
    private static final String BETA_TEST_ROLE = "BETA_TEST";
    private final String ORDERS_MENU_ID = "orders";
    private final String SHOP_MENU_ID = "shop";
    private final String FEEDBACK_MENU_ID = "feedback";
    private final String HEART_AWARDS_MENU_ID = "heartAwards";
    private final String TEAM_MENU_ID = "team";
    private final String MAIN_MENU_ID = "main";
    private final String PROFILE_MENU_ID = "profile";

    @Inject
    private Label brandName;
    @Inject
    private RecognitionConfig recognitionConfig;
    @Inject
    private Image rcgLogo;
    @Inject
    private LinkButton helpLink;
    @Inject
    private CssLayout contentWrapper;
    @Inject
    private LinkButton sidebarToggle;
    @Inject
    private HBoxLayout mainWrapper;
    @Inject
    private UserSession userSession;
    @Inject
    private Image userImage;
    @Inject
    private SideMenu sideMenu;
    @Inject
    private VBoxLayout sideMenuBox;
    @Inject
    private LinkButton toLogout;
    @Inject
    private LinkButton toProfile;
    @Inject
    private LinkButton toSettings;
    @Inject
    private RecognitionService recognitionService;
    @Inject
    private UserService userService;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    protected Events events;
    @Inject
    protected RcgEventBean rcgEventBean;

    private String selectedSideMenuItemId = "main";

    private UserExt userExt;

    private boolean isTablet = false, isMobile = false;

    private boolean hasTeamProfiles = false;

    private LayoutEvents.LayoutClickListener mobileClickListener = null;

    private String defaultWindowId = "main";

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        /**
         * save new token
         * */
        try {
            TokenHelper.saveCurrentUserToken(userSession);
        } catch (Exception e) {
            showNotification(e.getMessage());
            return;
        }

        if (userSession.getAttribute("assignment") == null) {
            showNotification(getMessage("user.person.assignment.null"));
            return;
        }

        checkAndCreateLoginLog();

        rcgLogo.addClickListener(event -> initActiveMenu("main"));

        userExt = userSession.getAttribute(StaticVariable.USER_EXT);

        hasTeamProfiles = recognitionService.hasTeamProfiles();

        helpLink.setCaption("");
        helpLink.setAction(new BaseAction("rcg-faq") {
            @Override
            public void actionPerform(Component component) {
                String activeWindowId = sideMenu.getMenuItems().stream().filter(e -> e.getStyleName().equalsIgnoreCase("rcg-menu-item-active")).map(SideMenu.MenuItem::getId).collect(Collectors.joining(""));
                if (activeWindowId.equalsIgnoreCase("feedback")) {
                    String url;
                    if (userSessionSource.getLocale().getLanguage().equalsIgnoreCase("ru")) {
                        url = recognitionConfig.getFeedbackFaqUrlRu();
                    } else {
                        url = recognitionConfig.getFeedbackFaqUrlEn();
                    }
                    if (StringUtils.isNotBlank(url)) {
                        showWebPage(url, ParamsMap.of("target", "_blank"));
                    } else {
                        showNotification("Error", "FAQ url is blank!", NotificationType.ERROR);
                    }
                } else {
                    openFrame(contentWrapper, "rcg-faq");
                    sideMenu.getMenuItems().forEach(mi -> mi.removeStyleName("rcg-menu-item-active"));
                }
            }
        });

        toSettings.setAction(new BaseAction("settings") {
            @Override
            public void actionPerform(Component component) {
                RecognitionProfileSetting profileSetting = recognitionService.loadProfileSettings();

                AbstractEditor abstractEditor =
                        openEditor("rcg-profile-settings", profileSetting, WindowManager.OpenType.DIALOG);

                abstractEditor.addCloseWithCommitListener(() -> {
                    initActiveMenu(selectedSideMenuItemId);
                });
            }
        });

        if (userSession.getAttribute(PROFILE_SETTINGS_CHECKED) == null) {
            userSession.setAttribute(PROFILE_SETTINGS_CHECKED, true);
            RecognitionProfileSetting setting = recognitionService.findProfileSettings(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
            if (setting == null) {
                toSettings.getAction().actionPerform(null);
            }
        }

        toProfile.setAction(new BaseAction("profile") {
            @Override
            public void actionPerform(Component component) {
                initActiveMenu("profile");
            }
        });

        toLogout.setAction(new BaseAction("logout") {
            @Override
            public void actionPerform(Component component) {
                logout();
            }
        });

        mobileClickListener = event -> mainWrapper.removeStyleName("mobile-menu-open");

        Page page = Page.getCurrent();

        sidebarToggle.setAction(new BaseAction("sidebar-toggle") {
            @Override
            public void actionPerform(Component component) {
                findDevice();

                String styles = mainWrapper.getStyleName();

                if (!isMobile && !isTablet || (isTablet && !isMobile)) {
                    if (styles.contains("rcg-tablet")) {
                        initPage(false, false);
                    } else {
                        initPage(true, false);
                    }
                } else { // mobile
                    if (styles.contains("mobile-menu-open")) {
                        mainWrapper.removeStyleName("mobile-menu-open");
                        contentWrapper.removeLayoutClickListener((Consumer<LayoutClickNotifier.LayoutClickEvent>) mobileClickListener);
                    } else {
                        mainWrapper.addStyleName("mobile-menu-open");
                        contentWrapper.addLayoutClickListener((Consumer<LayoutClickNotifier.LayoutClickEvent>) mobileClickListener);
                    }
                }

                JavaScript.eval("$(window).trigger('sidemenu-resize')");
            }
        });

        findDevice();

        initPage(isTablet, isMobile);

        page.addBrowserWindowResizeListener((Page.BrowserWindowResizeListener) event -> {
            int width = event.getWidth();

            if (!isTablet) {
                if (width >= 768 && width <= 962) {
                    isTablet = true;
                    isMobile = false;
                    initPage(true, false);
                }
            }

            if (!isMobile) {
                if (width < 768) {
                    isTablet = false;
                    isMobile = true;
                    initPage(false, true);
                }
            }

            if (width > 962 && (isTablet || isMobile)) {
                isTablet = false;
                isMobile = false;
                initPage(false, false);
            }

        });

        if (params.containsKey("defaultWindowId")) {
            defaultWindowId = (String) params.get("defaultWindowId");
        }

        initSideMenu();

        FileDescriptor fileDescriptor = getUserImage();
        WebCommonUtils.setImage(fileDescriptor, userImage, "35px");
        if (fileDescriptor != null) {
            FileDescriptorResource fileDescriptorResource = (FileDescriptorResource) userImage.getSource();
            fileDescriptorResource.setCacheTime(0);
        }

        Page.getCurrent().getStyles().add(new ThemeResource("grid.css"));
    }

    private void checkAndCreateLoginLog() {
        if (!recognitionService.hasRecognitionLoginLog()) {
            recognitionService.createRecognitionLoginLog();
        }
    }

    private void findDevice() {
        Page page = Page.getCurrent();
        int browserWidth = page.getBrowserWindowWidth();

        isTablet = browserWidth >= 768 && browserWidth <= 962;
        if (!isTablet) {
            isMobile = browserWidth < 768;
        }
    }

    private void initPage(boolean isTablet, boolean isMobile) {
        contentWrapper.removeLayoutClickListener((Consumer<LayoutClickNotifier.LayoutClickEvent>) mobileClickListener);

        if (isTablet) {
            mainWrapper.removeStyleName("rcg-mobile mobile-menu-open");
            mainWrapper.addStyleName("rcg-tablet");
            sideMenuBox.setWidth("60px");
        } else if (isMobile) {
            mainWrapper.removeStyleName("rcg-tablet");
            mainWrapper.addStyleName("rcg-mobile");

            sideMenuBox.setWidth("0px");

            contentWrapper.addLayoutClickListener((Consumer<LayoutClickNotifier.LayoutClickEvent>) mobileClickListener);
        } else {
            sideMenuBox.setWidth("200px");

            mainWrapper.removeStyleName("rcg-mobile mobile-menu-open rcg-tablet");
        }
    }

    private FileDescriptor getUserImage() {
        PersonExt personExt = userSession.getAttribute(StaticVariable.USER_PERSON);
        if (personExt != null) {
            return personExt.getImage();
        }
        return userExt.getImage();
    }

    private void initSideMenu() {
        Consumer<SideMenu.MenuItem> menuItemConsumer = this::initActiveMenu;

        SideMenu.MenuItem mainMenuItem = sideMenu.createMenuItem(MAIN_MENU_ID, getMessage("rcg.menu.main"), "font-icon:HOME", menuItemConsumer);
        sideMenu.addMenuItem(mainMenuItem);

        SideMenu.MenuItem profileMenuItem = sideMenu.createMenuItem(PROFILE_MENU_ID, getMessage("rcg.menu.profile"), "font-icon:USER", menuItemConsumer);
        sideMenu.addMenuItem(profileMenuItem);

        if (hasTeamProfiles) {
            SideMenu.MenuItem teamMenuItem = sideMenu.createMenuItem(TEAM_MENU_ID, getMessage("rcg.menu.team"), "font-icon:USERS", menuItemConsumer);
            sideMenu.addMenuItem(teamMenuItem);

            if (checkMenuItemAccess(ORDERS_MENU_ID)) {
                SideMenu.MenuItem ordersMenuItem = sideMenu.createMenuItem(ORDERS_MENU_ID, getMessage("rcg.menu.orders"), "font-icon:USERS", menuItemConsumer);
                rcgEventBean.setOrdersMenuItem(ordersMenuItem);
                Long countNewOrders = recognitionService.loadGoodsOrdersCount();
                if (countNewOrders > 0) {
                    ordersMenuItem.setBadgeText(String.format("+%d NEW", countNewOrders));
                }
                sideMenu.addMenuItem(ordersMenuItem);
            }
        }
        if (checkMenuItemAccess(SHOP_MENU_ID)) {
            SideMenu.MenuItem shopMenuItem = sideMenu.createMenuItem(SHOP_MENU_ID, getMessage("rcg.menu.shop"), "font-icon:SHOPPING_CART", menuItemConsumer);
            sideMenu.addMenuItem(shopMenuItem);
        }

        SideMenu.MenuItem heartAwardsMenuItem = sideMenu.createMenuItem(HEART_AWARDS_MENU_ID, getMessage("rcg.menu.ha"), "images/recognition/ha-gray-menu.svg", menuItemConsumer);
        sideMenu.addMenuItem(heartAwardsMenuItem);

        if (checkMenuItemAccess(FEEDBACK_MENU_ID)) {
            SideMenu.MenuItem feedbackItem = sideMenu.createMenuItem(FEEDBACK_MENU_ID, getMessage("rcg.menu.feedback"), "font-icon:COMMENTS", menuItemConsumer);
            sideMenu.addMenuItem(feedbackItem);
        }

        sideMenu.getMenuItems().forEach(el -> {
            if (el.getId().equals(defaultWindowId)) el.getCommand().accept(el);
        });
    }

    private boolean checkMenuItemAccess(String menuId) {
        if (StringUtils.isNotBlank(menuId)) {
            switch (menuId) {
                case FEEDBACK_MENU_ID: {
                    if (recognitionConfig.getBetaTestFeedback()) {
                        return userService.hasRole(BETA_TEST_ROLE);
                    }
                    break;
                }
                case ORDERS_MENU_ID:
                case SHOP_MENU_ID: {
                    if (recognitionConfig.getBetaTestShop()) {
                        return userService.hasRole(BETA_TEST_ROLE);
                    }
                    break;
                }
            }
        }
        return true;
    }

    public void initActiveMenu(String sideMenuItemId) {
        if (isMobile) mainWrapper.removeStyleName("mobile-menu-open");

        if (sideMenuItemId.equalsIgnoreCase("heartAwards")) {
            sideMenu.getMenuItemNN("heartAwards").setIcon("images/recognition/ha-blue-menu.svg");
        } else {
            sideMenu.getMenuItemNN("heartAwards").setIcon("images/recognition/ha-gray-menu.svg");
        }

        String brandNameText = "KC Recognition";

        switch (sideMenuItemId) {
            case "main": {
                openFrame(contentWrapper, "rcg-main");
                break;
            }
            case "profile": {
                openFrame(contentWrapper, "rcg-profile");
                break;
            }
            case "team": {
                openFrame(contentWrapper, "rcg-team");
                break;
            }
            case "orders": {
                openFrame(contentWrapper, "rcg-orders");
                break;
            }
            case "shop": {
                openFrame(contentWrapper, BooleanUtils.isTrue(recognitionConfig.getActivateShop()) ? "rcg-shop" : "rcg-temp-page");
                break;
            }
            case "heartAwards": {
                openFrame(contentWrapper, BooleanUtils.isTrue(recognitionConfig.getActivateHeartAwards()) ? "rcg-heart-awards" : "rcg-temp-page");
                break;
            }
            case "feedback": {
                openFrame(contentWrapper, "rcg-feedback-page");

                brandNameText = getMessage("rcg.menu.feedback");
                break;
            }
            default: {
                openFrame(contentWrapper, "rcg-main");
            }
        }

        brandName.setValue(brandNameText);

        sideMenu.getMenuItems().forEach(new Consumer<SideMenu.MenuItem>() {
            @Override
            public void accept(SideMenu.MenuItem menuItem) {
                menuItem.removeStyleName("rcg-menu-item-active");

                if (menuItem.getId().equals(sideMenuItemId)) {
                    menuItem.addStyleName("rcg-menu-item-active");
                }
            }
        });
    }

    private void initActiveMenu(SideMenu.MenuItem menuItem) {
        String sideMenuItemId = menuItem.getId();
        this.selectedSideMenuItemId = sideMenuItemId;
        initActiveMenu(sideMenuItemId);
    }

    public void logout() {
        Window window = ComponentsHelper.getWindow(this);
        if (window == null) {
            throw new IllegalStateException("Unable to find Frame for logout button");
        }
        window.saveSettings();

//        final WebWindowManager wm = (WebWindowManager) window.getWindowManager();
//        wm.checkModificationsAndCloseAll(() -> {
//            Connection connection = wm.getApp().getConnection();
//            connection.logout();
//        });
    }
}