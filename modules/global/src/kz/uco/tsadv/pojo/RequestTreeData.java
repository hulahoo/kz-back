package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author adilbekov.yernar
 */
public class RequestTreeData implements Serializable {

    private String rId;
    private String rdId;
    private String pRdId;
    private String rowKey;
    private String orgGroupId;
    private String pOrgGroupId;
    private String posGroupId;
    private String groupId;
    private String pGroupId;
    private String gradeGroupId;
    private String[] nameRu;
    private String[] nameEn;
    private String[] grade;
    private BigDecimal[] headCount;
    private BigDecimal[] baseSalary;
    private BigDecimal[] mtPayrollPer;
    private BigDecimal[] mtPayroll;
    private int elementType;
    private String changeType;
    private List<RequestTreeData> children;
    private boolean root;

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getRdId() {
        return rdId;
    }

    public void setRdId(String rdId) {
        this.rdId = rdId;
    }

    public String getpRdId() {
        return pRdId;
    }

    public void setpRdId(String pRdId) {
        this.pRdId = pRdId;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getOrgGroupId() {
        return orgGroupId;
    }

    public void setOrgGroupId(String orgGroupId) {
        this.orgGroupId = orgGroupId;
    }

    public String getpOrgGroupId() {
        return pOrgGroupId;
    }

    public void setpOrgGroupId(String pOrgGroupId) {
        this.pOrgGroupId = pOrgGroupId;
    }

    public String getPosGroupId() {
        return posGroupId;
    }

    public void setPosGroupId(String posGroupId) {
        this.posGroupId = posGroupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getpGroupId() {
        return pGroupId;
    }

    public void setpGroupId(String pGroupId) {
        this.pGroupId = pGroupId;
    }

    public String[] getNameRu() {
        return nameRu;
    }

    public void setNameRu(String[] nameRu) {
        this.nameRu = nameRu;
    }

    public String[] getNameEn() {
        return nameEn;
    }

    public void setNameEn(String[] nameEn) {
        this.nameEn = nameEn;
    }

    public String getGradeGroupId() {
        return gradeGroupId;
    }

    public void setGradeGroupId(String gradeGroupId) {
        this.gradeGroupId = gradeGroupId;
    }

    public String[] getGrade() {
        return grade;
    }

    public void setGrade(String[] grade) {
        this.grade = grade;
    }

    public BigDecimal[] getHeadCount() {
        return headCount;
    }

    public void setHeadCount(BigDecimal[] headCount) {
        this.headCount = headCount;
    }

    public BigDecimal[] getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal[] baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal[] getMtPayrollPer() {
        return mtPayrollPer;
    }

    public void setMtPayrollPer(BigDecimal[] mtPayrollPer) {
        this.mtPayrollPer = mtPayrollPer;
    }

    public BigDecimal[] getMtPayroll() {
        return mtPayroll;
    }

    public void setMtPayroll(BigDecimal[] mtPayroll) {
        this.mtPayroll = mtPayroll;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public List<RequestTreeData> getChildren() {
        return children;
    }

    public void setChildren(List<RequestTreeData> children) {
        this.children = children;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }
}
