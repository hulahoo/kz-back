package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicBusinessTripType;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_BusinessTripListener")
@NamePattern(" %s %s %s %s|purpose,type,dateFrom,dateTo")
@Table(name = "TSADV_BUSINESS_TRIP")
@Entity(name = "tsadv$BusinessTrip")
public class BusinessTrip extends AbstractParentEntity {
    private static final long serialVersionUID = -6857890524192237948L;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;


    @Column(name = "REASON")
    protected String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "ORDER_NUM")
    protected String orderNum;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Column(name = "STATUS")
    protected String status;

    @Column(name = "TYPE_TRIP")
    protected String typeTrip;

    @Column(name = "CANCEL_ORDER_NUMBER")
    protected String cancelOrderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "CANCEL_ORDER_DATE")
    protected Date cancelOrderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_BUSINESS_TRIP_ID")
    protected BusinessTrip parentBusinessTrip;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Column(name = "PURPOSE", length = 1000)
    protected String purpose;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "TYPE_ID")
    protected DicBusinessTripType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_ASSIGNMENT_ID")
    protected OrdAssignment ordAssignment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "businessTrip")
    protected List<BusinessTripLines> businessTripLines;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setParentBusinessTrip(BusinessTrip parentBusinessTrip) {
        this.parentBusinessTrip = parentBusinessTrip;
    }

    public BusinessTrip getParentBusinessTrip() {
        return parentBusinessTrip;
    }


    public void setCancelOrderNumber(String cancelOrderNumber) {
        this.cancelOrderNumber = cancelOrderNumber;
    }

    public String getCancelOrderNumber() {
        return cancelOrderNumber;
    }

    public void setCancelOrderDate(Date cancelOrderDate) {
        this.cancelOrderDate = cancelOrderDate;
    }

    public Date getCancelOrderDate() {
        return cancelOrderDate;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setStatus(BusinessTripOrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public BusinessTripOrderStatus getStatus() {
        return status == null ? null : BusinessTripOrderStatus.fromId(status);
    }

    public void setTypeTrip(BusinessTripOrderType typeTrip) {
        this.typeTrip = typeTrip == null ? null : typeTrip.getId();
    }

    public BusinessTripOrderType getTypeTrip() {
        return typeTrip == null ? null : BusinessTripOrderType.fromId(typeTrip);
    }


    public OrdAssignment getOrdAssignment() {
        return ordAssignment;
    }

    public void setOrdAssignment(OrdAssignment ordAssignment) {
        this.ordAssignment = ordAssignment;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setBusinessTripLines(List<BusinessTripLines> businessTripLines) {
        this.businessTripLines = businessTripLines;
    }

    public List<BusinessTripLines> getBusinessTripLines() {
        return businessTripLines;
    }


    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }


    public DicBusinessTripType getType() {
        return type;
    }

    public void setType(DicBusinessTripType type) {
        this.type = type;
    }


    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPurpose() {
        return purpose;
    }

    @MetaProperty
    public String getRoute() {
        List<BusinessTripLines> businessTripLinesList = new ArrayList<>();
        List<String> cityList = new ArrayList<>();
        String route = "";

        Collections.sort(businessTripLines, new Comparator<BusinessTripLines>() {
            @Override
            public int compare(BusinessTripLines o1, BusinessTripLines o2) {
                return o1.getDateFrom().compareTo(o2.getDateFrom());
            }
        });

        for (BusinessTripLines businessTripLines : getBusinessTripLines()) {
            if (businessTripLines != null) {
                if (businessTripLines.getCityFrom() != null) {
                    cityList.add(businessTripLines.getCityFrom().getSettlementLangValue());
                }
                if (businessTripLines.getCityTo() != null) {
                    cityList.add(businessTripLines.getCityTo().getSettlementLangValue());
                }
            }
        }
        String city = "";
        for (String cityString : cityList) {
            if (cityString != city) {
                if (city == "") {
                    route = cityString;
                } else {
                    route = route + " - " + cityString;
                }
            }
            city = cityString;
        }
        return route;
    }

    @MetaProperty
    public Integer getAbsenceDays() {
        long diff = dateTo.getTime() - dateFrom.getTime();
        return (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
    }

    @MetaProperty
    public String getBusinessTripWithDate() {
        return getType().getLangValue() + " " + simpleDateFormat.format(getDateFrom()) + "-" + simpleDateFormat.format(getDateTo());
    }

    @MetaProperty
    public String getParentBusinessTripCaption() {
        if (parentBusinessTrip != null) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            StringBuilder sb = new StringBuilder("");
            if (parentBusinessTrip.getOrderNum() != null) {
                sb.append("№");
                sb.append(parentBusinessTrip.getOrderNum());
                sb.append(", ");
            }
            if (parentBusinessTrip.getOrderDate() != null) {
                sb.append(formatter.format(parentBusinessTrip.getOrderDate()));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}