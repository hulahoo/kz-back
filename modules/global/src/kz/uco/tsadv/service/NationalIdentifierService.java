package kz.uco.tsadv.service;


import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import java.util.Date;

public interface NationalIdentifierService {
    String NAME = "tsadv_NationalIdentifierService";

    boolean checkNationalIdentifier (String nationalIdentifier, Date dateOfBirth, DicSex dicSex);

    boolean hasDuplicate (String nationalIdentifier);

    boolean hasDuplicate (PersonExt personExt);
}