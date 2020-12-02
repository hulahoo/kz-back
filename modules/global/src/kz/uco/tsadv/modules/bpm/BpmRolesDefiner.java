package kz.uco.tsadv.modules.bpm;

//import com.haulmont.bpm.entity.ProcModel;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_BPM_ROLES_DEFINER")
@Entity(name = "tsadv$BpmRolesDefiner")
public class BpmRolesDefiner extends StandardEntity {
    private static final long serialVersionUID = 2556837329709520964L;


    /*@NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROC_MODEL_ID", unique = true)
    protected ProcModel procModel;*/

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "bpmRolesDefiner")
    protected List<BpmRolesLink> links;


    /*public void setProcModel(ProcModel procModel) {
        this.procModel = procModel;
    }

    public ProcModel getProcModel() {
        return procModel;
    }*/


    public void setLinks(List<BpmRolesLink> links) {
        this.links = links;
    }

    public List<BpmRolesLink> getLinks() {
        return links;
    }



}