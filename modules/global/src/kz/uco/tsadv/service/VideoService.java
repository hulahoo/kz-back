package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;

public interface VideoService {
    String NAME = "tsadv_VideoService";

    void startScheduler(FileDescriptor fileDescriptor);

    void convertVideos();

}