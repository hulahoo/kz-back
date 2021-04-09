package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.TestHelper;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_CourseSectionAttemptListener")
public class CourseSectionAttemptListener implements BeforeDeleteEntityListener<CourseSectionAttempt>, BeforeDetachEntityListener<CourseSectionAttempt>, BeforeInsertEntityListener<CourseSectionAttempt>, BeforeUpdateEntityListener<CourseSectionAttempt>, AfterDeleteEntityListener<CourseSectionAttempt>, AfterInsertEntityListener<CourseSectionAttempt>, AfterUpdateEntityListener<CourseSectionAttempt>, BeforeAttachEntityListener<CourseSectionAttempt> {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;

    @Override
    public void onBeforeDelete(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeDetach(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeInsert(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
    }


    @Override
    public void onBeforeUpdate(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null &&
                persistence.getTools().isDirty(courseSectionAttempt, "testResult")
                && courseSectionAttempt.getTestResultPercent() == null
        ) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
    }


    @Override
    public void onAfterDelete(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterInsert(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterUpdate(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onBeforeAttach(CourseSectionAttempt courseSectionAttempt) {

    }

    @Inject
    protected Persistence persistence;
    @Inject
    protected TestHelper testHelper;

}