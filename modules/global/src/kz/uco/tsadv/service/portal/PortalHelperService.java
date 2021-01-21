package kz.uco.tsadv.service.portal;

public interface PortalHelperService {
    String NAME = "tsadv_PortalHelperService";

    <T> T getNewEntity(String entityName);
}