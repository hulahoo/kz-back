package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.config.TsadvGlobalConfig;
import kz.uco.tsadv.modules.administration.PortalMenuCustomization;
import kz.uco.tsadv.modules.administration.enums.PortalAvailability;
import kz.uco.tsadv.modules.administration.enums.PortalMenuType;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.pojo.PortalMenuPojo;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.ExecutiveAssistantService;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.PositionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service(PortalHelperService.NAME)
public class PortalHelperServiceBean implements PortalHelperService {

    @Inject
    protected Metadata metadata;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TsadvGlobalConfig tsadvGlobalConfig;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected PositionService positionService;
    @Inject
    protected ExecutiveAssistantService assistantService;
    @Inject
    private PositionStructureConfig positionStructureConfig;

    @Override
    public <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName) {
        @SuppressWarnings("unchecked") T entity = (T) metadata.create(entityName);
        entity.setId(null);
        return entity;
    }

    public List<UUID> getCompaniesForLoadDictionary(UUID personGroupId) {
        DicCompany company = employeeService.getCompanyByPersonGroupId(personGroupId);
        UUID generalCompanyId = tsadvGlobalConfig.getGeneralCompanyId();

        List<UUID> companyIds = new ArrayList<>();
        if (company != null)
            companyIds.add(company.getId());
        if (generalCompanyId != null)
            companyIds.add
                    (generalCompanyId);
        return companyIds;
    }

    @Override
    public void initPortalMenu(List<PortalMenuPojo> menuList) {
        if (menuList.isEmpty()) return;
        PortalMenuType portalMenuType = PortalMenuType.fromId(menuList.get(0).getType());

        List<PortalMenuPojo> fullMenuPojoList = getFullMenuPojoList(new ArrayList<>(), menuList, null);

        List<PortalMenuCustomization> list = dataManager.load(PortalMenuCustomization.class)
                .query("select e from tsadv_PortalMenuCustomization e where e.menuType = :menuType")
                .viewProperties("menuItem", "active", "name1", "name2", "name3", "parent.id")
                .parameter("menuType", portalMenuType)
                .list();

        List<PortalMenuCustomization> menu = new ArrayList<>(list);

        fullMenuPojoList.stream().filter(menuPojo -> list.stream().noneMatch(portalMenuCustomization -> portalMenuCustomization.getMenuItem().equals(menuPojo.getId())))
                .map(this::parseToMenu)
                .forEach(menu::add);

        for (PortalMenuCustomization portalMenuCustomization : menu) {
            PortalMenuCustomization parent = findParent(portalMenuCustomization, menu, fullMenuPojoList);
            portalMenuCustomization.setParent(parent);
        }

        CommitContext context = new CommitContext();
        for (PortalMenuCustomization portalMenuCustomization : menu) {
            PortalMenuPojo portalMenuPojo = fullMenuPojoList.stream().filter(menuPojo -> menuPojo.getId().equals(portalMenuCustomization.getMenuItem())).findAny().orElse(null);
            if (portalMenuPojo == null)
                context.addInstanceToRemove(portalMenuCustomization);
            else {
                portalMenuCustomization.setName1(portalMenuPojo.getRu());
                portalMenuCustomization.setName3(portalMenuPojo.getEn());
                context.addInstanceToCommit(portalMenuCustomization);
            }
        }

        dataManager.commit(context);
    }

    @Override
    public List<PortalMenuCustomization> getPortalMenu(String menuType) {
        PositionExt position = positionService.getPosition(new View(PositionExt.class).addProperty("group"));

        if (position == null) return new ArrayList<>();

        boolean isManager = hierarchyService.isParent(position.getGroup().getId(), positionStructureConfig.getPositionStructureId());

        boolean isAssistant = !assistantService.getManagerList(position.getGroup().getId()).isEmpty();

        List<PortalAvailability> availabilities = new ArrayList<>();
        availabilities.add(PortalAvailability.FOR_ALL);

        if (isManager) availabilities.add(PortalAvailability.FOR_MANAGER);
        if (isAssistant) availabilities.add(PortalAvailability.FOR_ASSISTANT);

        return dataManager.load(PortalMenuCustomization.class)
                .query("select e from tsadv_PortalMenuCustomization e " +
                        "   where e.menuType = :menuType " +
                        "   and e.portalAvailability in :availabilities" +
                        "   and e.active = 'TRUE'")
                .viewProperties("menuItem")
                .parameter("menuType", PortalMenuType.fromId(menuType))
                .parameter("availabilities", availabilities)
                .list();
    }

    protected PortalMenuCustomization findParent(PortalMenuCustomization menu, List<PortalMenuCustomization> menuList, List<PortalMenuPojo> fullMenuPojoList) {
        PortalMenuPojo portalMenuPojo = fullMenuPojoList.stream().filter(menuPojo -> menuPojo.getId().equals(menu.getMenuItem())).findAny().orElse(null);
        if (portalMenuPojo == null || portalMenuPojo.getParent() == null) return null;
        return menuList.stream().filter(portalMenuCustomization -> portalMenuCustomization.getMenuItem().equals(portalMenuPojo.getParent().getId())).findAny().orElse(null);
    }

    protected List<PortalMenuPojo> getFullMenuPojoList(List<PortalMenuPojo> fullList, List<PortalMenuPojo> menuList, PortalMenuPojo parent) {
        menuList.forEach(menuPojo -> menuPojo.setParent(parent));
        fullList.addAll(menuList);

        for (PortalMenuPojo portalMenuPojo : menuList) {
            if (portalMenuPojo.getItems() != null)
                getFullMenuPojoList(fullList, portalMenuPojo.getItems(), portalMenuPojo);
        }

        return fullList;
    }

    protected PortalMenuCustomization parseToMenu(PortalMenuPojo menuPojo) {
        PortalMenuCustomization portalMenuCustomization = metadata.create(PortalMenuCustomization.class);
        portalMenuCustomization.setMenuType(PortalMenuType.fromId(menuPojo.getType()));
        portalMenuCustomization.setMenuItem(menuPojo.getId());
        portalMenuCustomization.setName1(menuPojo.getRu());
        portalMenuCustomization.setName3(menuPojo.getEn());
        return portalMenuCustomization;
    }
}