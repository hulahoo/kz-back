package kz.uco.tsadv.service;


import java.util.List;
import java.util.UUID;

public interface BookService {
    String NAME = "tsadv_BookService";


    List<UUID> getCategoryHierarchy(String categoryId);
}