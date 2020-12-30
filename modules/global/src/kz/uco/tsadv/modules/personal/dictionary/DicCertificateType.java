package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_CERTIFICATE_TYPE")
@Entity(name = "tsadv_DicCertificateType")
public class DicCertificateType extends AbstractDictionary {
    private static final long serialVersionUID = 923653567863674214L;
}