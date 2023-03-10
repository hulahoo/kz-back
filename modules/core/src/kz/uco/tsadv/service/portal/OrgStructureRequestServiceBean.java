package kz.uco.tsadv.service.portal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.tsadv.api.OrgStructureRequestChangeType;
import kz.uco.tsadv.api.OrgStructureRequestDisplayType;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.OrgRequestChangeType;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.requests.OrgStructureRequest;
import kz.uco.tsadv.modules.personal.requests.OrgStructureRequestDetail;
import kz.uco.tsadv.pojo.OrgRequestSaveModel;
import kz.uco.tsadv.pojo.OrganizationRequestSaveModel;
import kz.uco.tsadv.pojo.PositionRequestSaveModel;
import kz.uco.tsadv.pojo.RequestTreeData;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service(OrgStructureRequestService.NAME)
public class OrgStructureRequestServiceBean implements OrgStructureRequestService {

    @Inject
    private EmployeeService employeeService;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private Metadata metadata;

    @Inject
    private Persistence persistence;

    @Inject
    private EmployeeNumberService employeeNumberService;

    @Inject
    private Gson gson;

    @Inject
    private DataManager dataManager;

    @Inject
    protected ViewRepository viewRepository;

    @SuppressWarnings("all")
    @Override
    public String getMergedOrgStructure(UUID requestId) {
        final Optional<RequestTreeData> mergedOrgStructureData = this.getMergedOrgStructureData(requestId);
        if (mergedOrgStructureData.isPresent()) {
            return String.format("[%s]", gson.toJson(mergedOrgStructureData.get()));
        } else {
            return "[]";
        }
    }

    @SuppressWarnings("all")
    @Override
    public String getMergedOrgStructure(UUID requestId, OrgStructureRequestDisplayType displayFilter) {
        final Optional<RequestTreeData> mergedOrgStructureData = this.getMergedOrgStructureData(requestId);
        if (mergedOrgStructureData.isPresent()) {
            RequestTreeData requestTreeData = mergedOrgStructureData.get();
            switch (displayFilter) {
                case CHANGES: {
                    Predicate<RequestTreeData> changeTypeFilter = c -> c.getChangeType() != null;
                    requestTreeData.setChildren(filterTreeData(requestTreeData.getChildren(), changeTypeFilter));
                    if (CollectionUtils.isNotEmpty(requestTreeData.getChildren())) {
                        return String.format("[%s]", gson.toJson(requestTreeData));
                    }
                }
                default: {
                    return String.format("[%s]", gson.toJson(requestTreeData));
                }
            }
        }
        return "[]";
    }

    @SuppressWarnings("all")
    @Override
    public String getMergedOrgStructure(UUID requestId, OrgStructureRequestChangeType changeTypeFilter) {
        final Optional<RequestTreeData> mergedOrgStructureData = this.getMergedOrgStructureData(requestId);
        if (mergedOrgStructureData.isPresent()) {
            RequestTreeData requestTreeData = mergedOrgStructureData.get();
            switch (changeTypeFilter) {
                case NEW:
                case EDIT:
                case CLOSE: {
                    Predicate<RequestTreeData> searchByChangeType = c -> c.getChangeType() != null && c.getChangeType().equalsIgnoreCase(changeTypeFilter.getId());
                    requestTreeData.setChildren(filterTreeData(requestTreeData.getChildren(), searchByChangeType));
                    break;
                }
            }
            if (CollectionUtils.isNotEmpty(requestTreeData.getChildren())) {
                return String.format("[%s]", gson.toJson(requestTreeData));
            }
        }
        return "[]";
    }

    protected List<RequestTreeData> filterTreeData(List<RequestTreeData> childes, Predicate<RequestTreeData> conditionToFilter) {
        if (CollectionUtils.isEmpty(childes)) {
            return childes;
        }

        return childes
                .stream()
                .peek(c -> c.setChildren(filterTreeData(c.getChildren(), conditionToFilter)))
                .filter(c -> (c.getChildren() != null && c.getChildren().size() > 0) || conditionToFilter.test(c))
                .collect(Collectors.toList());
    }

    private List<RequestTreeData> hideColumnsByGrade(List<RequestTreeData> treeDataList) {
        if (!this.availableSalary()) {
            return treeDataList.stream()
                    .map(e -> {
                        RequestTreeData newElement = new RequestTreeData();
                        BeanUtils.copyProperties(e, newElement, "baseSalary", "mtPayrollPer", "mtPayroll");
                        return newElement;
                    }).collect(Collectors.toList());
        }

        return new ArrayList<>(treeDataList);
    }

    private void fillHeadCount(RequestTreeData requestTreeData) {
        List<RequestTreeData> children = requestTreeData.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (RequestTreeData child : children) {
                fillHeadCount(child);
            }

            BigDecimal[] totalHeadCounts = new BigDecimal[]
                    {
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    };
            BigDecimal[] totalBaseSalary = new BigDecimal[]
                    {
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    };
            BigDecimal[] totalMtPayrollPer = new BigDecimal[]
                    {
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    };
            BigDecimal[] totalMtPayroll = new BigDecimal[]
                    {
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    };

            for (RequestTreeData child : children) {
                BigDecimal[] headCounts = child.getHeadCount();
                BigDecimal[] baseSalary = child.getBaseSalary();
                BigDecimal[] mtPayrollPer = child.getMtPayrollPer();
                BigDecimal[] mtPayroll = child.getMtPayroll();
                for (int i = 0; i < 3; i++) {
                    if (headCounts != null && headCounts.length == 3) {
                        totalHeadCounts[i] = totalHeadCounts[i].add(ObjectUtils.defaultIfNull(headCounts[i], BigDecimal.ZERO));
                    }
                    if (baseSalary != null && baseSalary.length == 3) {
                        totalBaseSalary[i] = totalBaseSalary[i].add(ObjectUtils.defaultIfNull(baseSalary[i], BigDecimal.ZERO));
                    }
                    if (mtPayrollPer != null && mtPayrollPer.length == 3) {
                        totalMtPayrollPer[i] = totalMtPayrollPer[i].add(ObjectUtils.defaultIfNull(mtPayrollPer[i], BigDecimal.ZERO));
                    }
                    if (mtPayroll != null && mtPayroll.length == 3) {
                        totalMtPayroll[i] = totalMtPayroll[i].add(ObjectUtils.defaultIfNull(mtPayroll[i], BigDecimal.ZERO));
                    }
                }
            }

            requestTreeData.setHeadCount(totalHeadCounts);
            requestTreeData.setBaseSalary(totalBaseSalary);
            requestTreeData.setMtPayrollPer(totalMtPayrollPer);
            requestTreeData.setMtPayroll(totalMtPayroll);
        }

        if (StringUtils.isNotEmpty(requestTreeData.getChangeType())) {
            if (requestTreeData.getChangeType().equalsIgnoreCase(OrgRequestChangeType.CLOSE.getId())) {
                requestTreeData.getNameRu()[1] = null;
                requestTreeData.getNameEn()[1] = null;
                requestTreeData.getGrade()[1] = null;
                requestTreeData.getNameRu()[2] = null;
                requestTreeData.getNameEn()[2] = null;
                requestTreeData.getGrade()[2] = null;
            }
        }
    }

    private void collectChildren(RequestTreeData parent, List<RequestTreeData> treeDataList) {
        if (CollectionUtils.isNotEmpty(treeDataList)) {
            List<RequestTreeData> foundChildren = new ArrayList<>();
            treeDataList.removeIf(requestTreeData -> {
                boolean found = (requestTreeData.getpRdId() != null && parent.getRdId() != null && requestTreeData.getpRdId().equals(parent.getRdId()))
                        || (requestTreeData.getpGroupId() != null && requestTreeData.getpGroupId().equals(parent.getGroupId()));
                if (found) {
                    foundChildren.add(requestTreeData);
                }
                return found;
            });

            if (!foundChildren.isEmpty()) {
                foundChildren.sort(Comparator.comparing(requestTreeData ->
                        Optional.ofNullable(requestTreeData.getGrade()).map(strings -> strings[0]).orElse(""),Comparator.reverseOrder()));
                foundChildren.forEach(requestTreeData -> collectChildren(requestTreeData, treeDataList));
                parent.setChildren(foundChildren);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected Optional<RequestTreeData> getMergedOrgStructureData(UUID requestId) {
        final UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (personGroupId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(persistence.callInTransaction(em -> {
            final OrgStructureRequest orgStructureRequest = em.find(OrgStructureRequest.class, requestId, ViewBuilder.of(OrgStructureRequest.class)
                    .add("author", View.MINIMAL)
                    .build());
            Query query = em.createNativeQuery(
                    "SELECT td.* FROM request_tree_data(?1,?2) td");
            query.setParameter(1, orgStructureRequest.getAuthor().getId());
            query.setParameter(2, requestId);

            PGobject singleResult = (PGobject) query.getFirstResult();

            String jsonData = singleResult != null ? singleResult.getValue() : "[]";

            List<RequestTreeData> treeDataList = gson.fromJson(jsonData, new TypeToken<List<RequestTreeData>>() {
            }.getType());

            if (CollectionUtils.isEmpty(treeDataList)) {
                return null;
            }

            List<RequestTreeData> filteredTreeDataList = hideColumnsByGrade(treeDataList);
            RequestTreeData root = filteredTreeDataList.stream()
                    .filter(RequestTreeData::isRoot)
                    .findFirst()
                    .orElseThrow(() -> new PortalException("Root element is not found!"));
            collectChildren(root, filteredTreeDataList);

            fillHeadCount(root);
            return root;
        }));
    }

    @Override
    public OrgStructureRequest initialCreate() {
        return persistence.callInTransaction(em -> {
            /**
             * detect next request number
             * */
            Long nextRequestNumber = em.createQuery(
                    "select coalesce(max(e.requestNumber),0) + 1 " +
                            "from tsadv_OrgStructureRequest e", Long.class)
                    .getSingleResult();

            /**
             * try find DicRequestStatus by DRAFT code
             * */
            DicRequestStatus dicRequestStatus = em.createQuery(
                    "select e from tsadv$DicRequestStatus e " +
                            "where e.code = 'DRAFT'", DicRequestStatus.class)
                    .getFirstResult();
            if (dicRequestStatus == null) {
                throw new NullPointerException("DicRequestStatus for the 'DRAFT' code is not found!");
            }

            final UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
            if (personGroupId == null) {
                throw new NullPointerException("PersonGroup by current user is not found!");
            }

            OrgStructureRequest orgStructureRequest = metadata.create(OrgStructureRequest.class);
            orgStructureRequest.setRequestDate(new Date());
            orgStructureRequest.setRequestNumber(nextRequestNumber);
            orgStructureRequest.setStatus(dicRequestStatus);
            orgStructureRequest.setAuthor(em.getReference(PersonGroupExt.class, personGroupId));
            orgStructureRequest.setCompany(employeeService.getCompanyByPersonGroupId(personGroupId));
            orgStructureRequest.setDepartment(employeeService.getOrganizationGroupByPersonGroupId(personGroupId, View.MINIMAL));
            //em.persist(orgStructureRequest);
            return orgStructureRequest;
        });
    }

    @Override
    public OrgStructureRequest saveRequest(OrgRequestSaveModel orgRequestSaveModel) {
        return persistence.callInTransaction(em -> {
            OrgStructureRequest orgStructureRequest;
            UUID id = orgRequestSaveModel.getId();
            if (id == null) {
                orgStructureRequest = metadata.create(OrgStructureRequest.class);
                orgStructureRequest.setCompany(em.getReference(DicCompany.class, orgRequestSaveModel.getCompany()));
                orgStructureRequest.setDepartment(em.getReference(OrganizationGroupExt.class, orgRequestSaveModel.getDepartment()));
                orgStructureRequest.setStatus(em.getReference(DicRequestStatus.class, orgRequestSaveModel.getStatus()));
                orgStructureRequest.setAuthor(em.getReference(PersonGroupExt.class, orgRequestSaveModel.getAuthor()));
                orgStructureRequest.setRequestDate(orgRequestSaveModel.getRequestDate());
                orgStructureRequest.setRequestNumber(employeeNumberService.generateNextRequestNumber());

            } else {
                orgStructureRequest = em.find(OrgStructureRequest.class, id);
                if (orgStructureRequest == null) {
                    throw new PortalException(String.format("OrgStructureRequest by ID: [%s] is not found!", id));
                }
            }

            orgStructureRequest.setModifyDate(orgRequestSaveModel.getModifyDate());
            orgStructureRequest.setComment(orgRequestSaveModel.getComment());

            ArrayList<FileDescriptor> files = new ArrayList<>();

            if (orgRequestSaveModel.getFile() != null)
                orgRequestSaveModel.getFile().stream()
                        .map(fileModel -> em.getReference(FileDescriptor.class, fileModel.getId()))
                        .forEach(files::add);

            orgStructureRequest.setFile(files);

            if (PersistenceHelper.isNew(orgStructureRequest)) {
                em.persist(orgStructureRequest);
            } else {
                em.merge(orgStructureRequest);
            }
            em.flush();
            return orgStructureRequest;
        });
    }

    @Override
    public String saveOrganization(OrganizationRequestSaveModel organizationRequestSaveModel) {
        return persistence.callInTransaction(em -> {
            OrgStructureRequestDetail detail;
            UUID rdId = organizationRequestSaveModel.getRdId();
            boolean isNew = rdId == null;
            if (isNew) {
                UUID rId = organizationRequestSaveModel.getrId();
                if (rId == null) {
                    throw new PortalException("Request ID is null or empty!");
                }

                detail = metadata.create(OrgStructureRequestDetail.class);
                detail.setOrgStructureRequest(em.getReference(OrgStructureRequest.class, rId));
                detail.setChangeType(OrgRequestChangeType.NEW);
                detail.setElementType(ElementType.ORGANIZATION);
            } else {
                detail = em.find(OrgStructureRequestDetail.class, rdId);
                if (detail == null) {
                    throw new NullPointerException(String.format("OrgStructureRequestDetail by ID: [%s] is not found!", rdId));
                }
            }

            detail.setOrganizationNameRu(organizationRequestSaveModel.getNameRu());
            detail.setOrganizationNameEn(organizationRequestSaveModel.getNameEn());

            /**
             *
             * */
            UUID parentRdId = organizationRequestSaveModel.getParentRdId();
            OrgStructureRequestDetail parentDetail = null;
            if (parentRdId != null) {
                parentDetail = em.find(OrgStructureRequestDetail.class, parentRdId);
                if (parentDetail == null) {
                    throw new NullPointerException(String.format("Parent OrgStructureRequestDetail by ID: [%s] is not found!", parentRdId));
                }
            }
            detail.setParent(parentDetail);

            /**
             *
             * */
            if (isNew) {
                UUID organizationGroupId = organizationRequestSaveModel.getOrganizationGroupId();
                OrganizationGroupExt organizationGroup = null;
                if (organizationGroupId != null) {
                    organizationGroup = em.find(OrganizationGroupExt.class, organizationGroupId);
                    if (organizationGroup == null) {
                        throw new NullPointerException(String.format("OrganizationGroup by ID: [%s] is not found!", organizationGroupId));
                    }
                    detail.setChangeType(OrgRequestChangeType.EDIT);
                }
                detail.setOrganizationGroup(organizationGroup);
            }

            /**
             *
             * */
            UUID parentOrganizationGroupId = organizationRequestSaveModel.getParentOrganizationGroupId();
            OrganizationGroupExt parentOrganizationGroup = null;
            if (parentOrganizationGroupId != null) {
                parentOrganizationGroup = em.find(OrganizationGroupExt.class, parentOrganizationGroupId);
                if (parentOrganizationGroup == null) {
                    throw new NullPointerException(String.format("OrganizationGroup by ID: [%s] is not found!", parentOrganizationGroupId));
                }
            }
            detail.setParentOrganizationGroup(parentOrganizationGroup);

            if (isNew) {
                em.persist(detail);
            } else {
                em.merge(detail);
            }
            return String.valueOf(detail.getId());
        });
    }

    @Override
    public String savePosition(PositionRequestSaveModel positionRequestSaveModel) {
        return persistence.callInTransaction(em -> {
            OrgStructureRequestDetail detail;
            UUID rdId = positionRequestSaveModel.getRdId();
            boolean isNew = rdId == null;
            if (isNew) {
                UUID rId = positionRequestSaveModel.getrId();
                if (rId == null) {
                    throw new PortalException("Request ID is null or empty!");
                }

                detail = metadata.create(OrgStructureRequestDetail.class);
                detail.setOrgStructureRequest(em.getReference(OrgStructureRequest.class, rId));
                detail.setChangeType(OrgRequestChangeType.NEW);
                detail.setElementType(ElementType.POSITION);
            } else {
                detail = em.find(OrgStructureRequestDetail.class, rdId);
                if (detail == null) {
                    throw new NullPointerException(String.format("OrgStructureRequestDetail by ID: [%s] is not found!", rdId));
                }
            }

            detail.setPositionNameRu(positionRequestSaveModel.getNameRu());
            detail.setPositionNameEn(positionRequestSaveModel.getNameEn());
            detail.setHeadCount(ObjectUtils.defaultIfNull(positionRequestSaveModel.getHeadCount(), BigDecimal.ZERO));
            detail.setMinSalary(positionRequestSaveModel.getMinSalary());
            detail.setMaxSalary(positionRequestSaveModel.getMaxSalary());
            /**
             *
             * */
            UUID parentRdId = positionRequestSaveModel.getParentRdId();
            OrgStructureRequestDetail parentDetail = null;
            if (parentRdId != null) {
                parentDetail = em.find(OrgStructureRequestDetail.class, parentRdId);
                if (parentDetail == null) {
                    throw new NullPointerException(String.format("Parent OrgStructureRequestDetail by ID: [%s] is not found!", parentRdId));
                }
            }
            detail.setParent(parentDetail);

            /**
             *
             * */
            if (isNew) {
                UUID positionGroupId = positionRequestSaveModel.getPositionGroupId();
                PositionGroupExt positionGroup = null;
                if (positionGroupId != null) {
                    positionGroup = em.find(PositionGroupExt.class, positionGroupId);
                    if (positionGroup == null) {
                        throw new NullPointerException(String.format("PositionGroup by ID: [%s] is not found!", positionGroupId));
                    }
                    detail.setChangeType(OrgRequestChangeType.EDIT);
                }
                detail.setPositionGroup(positionGroup);
            }

            /**
             *
             * */
            UUID parentOrganizationGroupId = positionRequestSaveModel.getParentOrganizationGroupId();
            OrganizationGroupExt parentOrganizationGroup = null;
            if (parentOrganizationGroupId != null) {
                parentOrganizationGroup = em.find(OrganizationGroupExt.class, parentOrganizationGroupId);
                if (parentOrganizationGroup == null) {
                    throw new NullPointerException(String.format("OrganizationGroup by ID: [%s] is not found!", parentOrganizationGroupId));
                }
            }
            detail.setParentOrganizationGroup(parentOrganizationGroup);

            /**
             *
             * */
            UUID gradeGroupId = positionRequestSaveModel.getGradeGroupId();
            GradeGroup gradeGroup = null;
            if (gradeGroupId != null) {
                gradeGroup = em.find(GradeGroup.class, gradeGroupId);
                if (gradeGroup == null) {
                    throw new NullPointerException(String.format("GradeGroup by ID: [%s] is not found!", parentOrganizationGroupId));
                }
            }
            detail.setGradeGroup(gradeGroup);

            if (isNew) {
                em.persist(detail);
            } else {
                em.merge(detail);
            }
            return String.valueOf(detail.getId());
        });
    }

    @Override
    public String getGrades() {
        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery(
                    "SELECT " +
                            "    JSON_AGG(JSON_BUILD_OBJECT( " +
                            "            'id', g.id, " +
                            "            'groupId', g.group_id, " +
                            "            'name', g.grade_name " +
                            "        )) " +
                            "FROM tsadv_grade g " +
                            "WHERE g.delete_ts IS NULL " +
                            "  AND NOW() BETWEEN g.start_date AND g.end_date");
            PGobject singleResult = (PGobject) query.getFirstResult();

            return singleResult != null ? singleResult.getValue() : "[]";
        });
    }

    @Override
    public void exclude(UUID requestId, RequestTreeData data) {
        try (Transaction transaction = persistence.getTransaction()) {

            String groupIdS = ObjectUtils.firstNonNull(data.getPosGroupId(), data.getOrgGroupId());
            UUID groupId = groupIdS != null ? UUID.fromString(groupIdS) : null;
            this.exclude(requestId, data.getRdId() != null ? UUID.fromString(data.getRdId()) : null, groupId, data.getElementType());

            if (CollectionUtils.isNotEmpty(data.getChildren())) {
                data.getChildren().forEach(requestTreeData -> this.exclude(requestId, requestTreeData));
            }

            transaction.commit();
        }
    }

    @Override
    public void exclude(UUID requestId, UUID requestDetailId, UUID elementGroupId, int elementType) {
        try (Transaction transaction = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();

            if (requestDetailId != null) {
                OrgStructureRequestDetail foundRequestDetail = em.find(OrgStructureRequestDetail.class,
                        requestDetailId,
                        viewRepository.getView(OrgStructureRequestDetail.class, View.LOCAL)
                                .addProperty("parentOrganizationGroup", viewRepository.getView(OrganizationGroupExt.class, View.MINIMAL))
                );
                if (foundRequestDetail == null) {
                    throw new PortalException(String.format("OrgStructureRequestDetail by ID: [%s] is not found!", requestDetailId));
                }

//                foundRequestDetail.getChildren()
//                        .forEach(child -> exclude(requestId, child.getId(), elementGroupId, elementType));

                foundRequestDetail.setHeadCount(BigDecimal.ZERO);
                foundRequestDetail.setChangeType(OrgRequestChangeType.CLOSE);
                foundRequestDetail.setParent(null);
                foundRequestDetail.setParentOrganizationGroup(null);

                em.merge(foundRequestDetail);
            } else if (elementGroupId != null) {
                ElementType foundElementType = ElementType.fromId(elementType);
                if (foundElementType == null) {
                    throw new PortalException(String.format("Element type by ID: [%s] is not found!", elementType));
                }

                OrgStructureRequestDetail requestDetail = metadata.create(OrgStructureRequestDetail.class);
                requestDetail.setOrgStructureRequest(em.getReference(OrgStructureRequest.class, requestId));
                requestDetail.setElementType(foundElementType);
                requestDetail.setHeadCount(BigDecimal.ZERO);
                requestDetail.setChangeType(OrgRequestChangeType.CLOSE);

                if (foundElementType.equals(ElementType.ORGANIZATION)) {
                    requestDetail.setOrganizationGroup(em.getReference(OrganizationGroupExt.class, elementGroupId));
                } else {
                    requestDetail.setPositionGroup(em.getReference(PositionGroupExt.class, elementGroupId));
                }
                em.persist(requestDetail);
            }

//            em.flush();

            transaction.commit();
        }
    }

    @Override
    public boolean availableSalary() {
        UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        Objects.requireNonNull(personGroupId, "Person group id is empty!");

        final PersonGroupExt currentPersonGroup = dataManager.load(LoadContext.create(PersonGroupExt.class)
                .setId(personGroupId)
                .setView("personGroup.currentAssignment"));
        Objects.requireNonNull(currentPersonGroup, "Can not find person group by id: " + currentPersonGroup);

        final AssignmentExt currentAssignment = dataManager.reload(currentPersonGroup.getCurrentAssignment(), "assignment.gradeGroup");
        return this.employeeService.hasHrRole("C&B_COMPANY")
                || this.employeeService.hasHrRole("C&B_GROUP")
                || (currentAssignment.getGradeGroup() != null
                ? currentAssignment.getGradeGroup().getAvailableSalary()
                : currentAssignment.getPositionGroup().getGradeGroup() != null
                ? currentAssignment.getPositionGroup().getGradeGroup().getAvailableSalary()
                : false);
    }

    @Override
    public boolean hasPermitToCreate() {
        return this.employeeService.hasHrRole("ORG_MANAGER_C&B");
    }
}