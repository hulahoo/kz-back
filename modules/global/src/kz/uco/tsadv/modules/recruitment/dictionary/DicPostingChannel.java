package kz.uco.tsadv.modules.recruitment.dictionary;

import kz.uco.tsadv.modules.recruitment.model.RequisitionPostingChannel;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_DIC_POSTING_CHANNEL")
@Entity(name = "tsadv$DicPostingChannel")
public class DicPostingChannel extends AbstractDictionary {
    private static final long serialVersionUID = 7468481399105676738L;

    @NotNull
    @Column(name = "CHANNAL_NAME", nullable = false)
    protected String channalName;

    @Column(name = "USER_NAME", length = 30)
    protected String userName;

    @Column(name = "PASSWORD", length = 30)
    protected String password;

    @Column(name = "CONNECTION_URL")
    protected String connectionURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUISITION_POSTING_CHANNEL_ID")
    protected RequisitionPostingChannel requisitionPostingChannel;


    public void setChannalName(String channalName) {
        this.channalName = channalName;
    }

    public String getChannalName() {
        return channalName;
    }


    public void setRequisitionPostingChannel(RequisitionPostingChannel requisitionPostingChannel) {
        this.requisitionPostingChannel = requisitionPostingChannel;
    }

    public RequisitionPostingChannel getRequisitionPostingChannel() {
        return requisitionPostingChannel;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getConnectionURL() {
        return connectionURL;
    }


}