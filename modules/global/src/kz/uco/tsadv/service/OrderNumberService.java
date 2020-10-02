package kz.uco.tsadv.service;


import javax.annotation.Nonnull;

public interface OrderNumberService {
    String NAME = "tsadv_OrderNumberService";

    @Nonnull
    Integer getLastAssignmentOrderNumber();

    @Nonnull
    Integer getLastDismissalOrderNumber();

    @Nonnull
    Integer getLastAbsenceOrderNumber();
}