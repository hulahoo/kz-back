package kz.uco.tsadv.beans;

import com.haulmont.cuba.core.entity.annotation.Extends;

import javax.persistence.Entity;

/**
 * @author adilbekov.yernar
 */
@Extends(kz.uco.base.entity.extend.UserExt.class)
@Entity(name = "base$UserExt")
public class UserExt extends kz.uco.base.entity.extend.UserExt {
}
