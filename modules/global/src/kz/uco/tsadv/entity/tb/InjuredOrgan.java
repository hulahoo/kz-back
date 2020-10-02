package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import java.util.UUID;

@Table(name = "TSADV_INJURED_ORGAN")
@Entity(name = "tsadv$InjuredOrgan")
public class InjuredOrgan extends AbstractParentEntity {
    private static final long serialVersionUID = 8892939874033956783L;


    @Column(name = "INJURED_ORGAN")
    protected String injuredOrgan;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MICROTRAUMA_INJURED_ID")
    protected MicrotraumaInjured microtraumaInjured;

    public void setMicrotraumaInjured(MicrotraumaInjured microtraumaInjured) {
        this.microtraumaInjured = microtraumaInjured;
    }

    public MicrotraumaInjured getMicrotraumaInjured() {
        return microtraumaInjured;
    }


    public void setInjuredOrgan(String injuredOrgan) {
        this.injuredOrgan = injuredOrgan;
    }

    public String getInjuredOrgan() {
        return injuredOrgan;
    }


}