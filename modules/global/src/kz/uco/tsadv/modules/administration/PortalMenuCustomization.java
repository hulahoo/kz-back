package kz.uco.tsadv.modules.administration;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.enums.PortalAvailability;
import kz.uco.tsadv.modules.administration.enums.PortalMenuType;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static kz.uco.base.common.MultiLanguageUtils.getCurrentLanguageValue;

@Table(name = "TSADV_PORTAL_MENU_CUSTOMIZATION")
@Entity(name = "tsadv_PortalMenuCustomization")
@NamePattern("%s - %s|menuItem,portalAvailability")
public class PortalMenuCustomization extends StandardEntity {
    private static final long serialVersionUID = 4375521647826289462L;

    @NotNull
    @Column(name = "MENU_ITEM", nullable = false, unique = true)
    private String menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private PortalMenuCustomization parent;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    private Boolean active = false;

    @NotNull
    @Column(name = "MENU_TYPE", nullable = false)
    private String menuType;

    @Column(name = "NAME1")
    private String name1;

    @Column(name = "NAME2")
    private String name2;

    @Column(name = "NAME3")
    private String name3;

    @NotNull
    @Column(name = "PORTAL_AVAILABILITY", nullable = false)
    private String portalAvailability;

    @MetaProperty(related = {"name1", "name2", "name3"})
    public String getName() {
        return getCurrentLanguageValue(name1, name2, name3);
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public PortalMenuType getMenuType() {
        return menuType == null ? null : PortalMenuType.fromId(menuType);
    }

    public void setMenuType(PortalMenuType menuType) {
        this.menuType = menuType == null ? null : menuType.getId();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PortalMenuCustomization getParent() {
        return parent;
    }

    public void setParent(PortalMenuCustomization parent) {
        this.parent = parent;
    }

    public PortalAvailability getPortalAvailability() {
        return portalAvailability == null ? null : PortalAvailability.fromId(portalAvailability);
    }

    public void setPortalAvailability(PortalAvailability portalAvailability) {
        this.portalAvailability = portalAvailability == null ? null : portalAvailability.getId();
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    @PostConstruct
    public void postConstruct() {
        this.setPortalAvailability(PortalAvailability.FOR_ALL);
        this.active = true;
    }
}