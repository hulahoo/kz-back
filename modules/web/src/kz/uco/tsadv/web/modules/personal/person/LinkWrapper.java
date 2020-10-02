package kz.uco.tsadv.web.modules.personal.person;

/**
 * @author Adilbekov Yernar
 */
public class LinkWrapper {

    private String id;
    private String screen;
    private String caption;
    private String icon;
    private boolean defaultPage;


    public LinkWrapper(String screen, String caption, String icon) {
        this.id = screen;
        this.screen = screen;
        this.caption = caption;
        this.icon = icon;
    }

    public LinkWrapper(String screen, String caption, String icon, boolean defaultPage) {
        this(screen, caption, icon);
        this.defaultPage = defaultPage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(boolean defaultPage) {
        this.defaultPage = defaultPage;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}
