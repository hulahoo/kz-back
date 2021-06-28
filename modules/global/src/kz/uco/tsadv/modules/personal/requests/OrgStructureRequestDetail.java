package kz.uco.tsadv.modules.personal.requests;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.tsadv.modules.personal.enums.OrgRequestChangeType;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table(name = "TSADV_ORG_STRUCTURE_REQUEST_DETAIL")
@Entity(name = "tsadv_OrgStructureRequestDetail")
public class OrgStructureRequestDetail extends StandardEntity {
    private static final long serialVersionUID = -6122499357953929703L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORG_STRUCTURE_REQUEST_ID")
    @NotNull
    protected OrgStructureRequest orgStructureRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private OrgStructureRequestDetail parent;

    @Column(name = "CHANGE_TYPE", nullable = false)
    @NotNull
    protected String changeType;

    @Column(name = "ORGANIZATION_NAME_RU", length = 1000)
    private String organizationNameRu;

    @Column(name = "ORGANIZATION_NAME_EN", length = 1000)
    private String organizationNameEn;

    @Column(name = "POSITION_NAME_RU", length = 1000)
    private String positionNameRu;

    @Column(name = "POSITION_NAME_EN", length = 1000)
    private String positionNameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    private OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORGANIZATION_GROUP_ID")
    private OrganizationGroupExt parentOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    private PositionGroupExt positionGroup;

    @NotNull
    @Column(name = "ELEMENT_TYPE", nullable = false)
    private Integer elementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    private GradeGroup gradeGroup;

    @Column(name = "HEAD_COUNT")
    private BigDecimal headCount;

    @Column(name = "MIN_SALARY")
    private BigDecimal minSalary;

    @Column(name = "MAX_SALARY")
    private BigDecimal maxSalary;

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public OrganizationGroupExt getParentOrganizationGroup() {
        return parentOrganizationGroup;
    }

    public void setParentOrganizationGroup(OrganizationGroupExt parentOrganizationGroup) {
        this.parentOrganizationGroup = parentOrganizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrgStructureRequestDetail getParent() {
        return parent;
    }

    public void setParent(OrgStructureRequestDetail parent) {
        this.parent = parent;
    }

    public ElementType getElementType() {
        return elementType == null ? null : ElementType.fromId(elementType);
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType == null ? null : elementType.getId();
    }

    public void setChangeType(OrgRequestChangeType changeType) {
        this.changeType = changeType == null ? null : changeType.getId();
    }

    public OrgRequestChangeType getChangeType() {
        return changeType == null ? null : OrgRequestChangeType.fromId(changeType);
    }

    public BigDecimal getHeadCount() {
        return headCount;
    }

    public void setHeadCount(BigDecimal headCount) {
        this.headCount = headCount;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public String getPositionNameEn() {
        return positionNameEn;
    }

    public void setPositionNameEn(String positionNameEn) {
        this.positionNameEn = positionNameEn;
    }

    public String getPositionNameRu() {
        return positionNameRu;
    }

    public void setPositionNameRu(String positionNameRu) {
        this.positionNameRu = positionNameRu;
    }

    public String getOrganizationNameEn() {
        return organizationNameEn;
    }

    public void setOrganizationNameEn(String organizationNameEn) {
        this.organizationNameEn = organizationNameEn;
    }

    public String getOrganizationNameRu() {
        return organizationNameRu;
    }

    public void setOrganizationNameRu(String organizationNameRu) {
        this.organizationNameRu = organizationNameRu;
    }

    public OrgStructureRequest getOrgStructureRequest() {
        return orgStructureRequest;
    }

    public void setOrgStructureRequest(OrgStructureRequest orgStructureRequest) {
        this.orgStructureRequest = orgStructureRequest;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }
}