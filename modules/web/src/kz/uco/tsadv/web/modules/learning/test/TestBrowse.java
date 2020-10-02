package kz.uco.tsadv.web.modules.learning.test;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.config.TestPublishConfig;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningObjectType;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.CourseSectionObject;
import kz.uco.tsadv.modules.learning.model.Test;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestBrowse extends AbstractLookup {
    @Inject
    protected Table<Test> testsTable;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected TestPublishConfig testPublishConfig;
    @Inject
    protected DataManager dataManager;

    public void publishBtnClick(Component source) {
        Test test = testsTable.getSingleSelected();
        String courseCategoryCode = testPublishConfig.getCourseCategoryCode();
        if (StringUtils.isBlank(courseCategoryCode)) {
            showNotification("courseCategoryCode.error");
        } else if (test != null && test.getName() != null) {
            DicCategory dicCategory = commonService.getEntity(DicCategory.class, courseCategoryCode);
            DicCourseFormat dicCourseFormat = commonService.getEntity(DicCourseFormat.class, "online");
            DicLearningObjectType dicLearningObjectType = commonService.getEntity(DicLearningObjectType.class, "TEST");
            if (test.getCourse() == null
                    && dicCategory != null
                    && dicCourseFormat != null
                    && dicLearningObjectType != null) {
                Course course = metadata.create(Course.class);
                course.setCategory(dicCategory);
                course.setName(test.getName());
                course.setActiveFlag(true);
                course.setSelfEnrollment(true);
                course.setShortDescription(test.getDescription() != null ? test.getDescription() : null);
                CourseSection courseSection = metadata.create(CourseSection.class);
                courseSection.setCourse(course);
                List<CourseSection> courseSections = new ArrayList<>();
                courseSections.add(courseSection);
                course.setSections(courseSections);
                courseSection.setSectionName(test.getName());
                courseSection.setMandatory(true);
                courseSection.setFormat(dicCourseFormat);
                courseSection.setOrder(1);
                CourseSectionObject courseSectionObject = metadata.create(CourseSectionObject.class);
                courseSectionObject.setTest(test);
                courseSectionObject.setObjectType(dicLearningObjectType);
                courseSection.setSectionObject(courseSectionObject);
                courseSection.setDescription(test.getDescription() != null ? test.getDescription() : null);
                test.setCourse(course);
                CommitContext commitContext = new CommitContext();
                commitContext.addInstanceToCommit(course);
                commitContext.addInstanceToCommit(courseSection);
                commitContext.addInstanceToCommit(courseSectionObject);
                commitContext.addInstanceToCommit(test);

                Set<Entity> commit = dataManager.commit(commitContext);
                if (!commit.isEmpty()) {
                    showNotification(getMessage("publish.ok"));
                }

            } else {
                showNotification((test.getCourse() != null ? getMessage("course.exist") + " \"" + test.getCourse().getName() + "\"\n" : "")
                                + (dicCategory == null ? getMessage("dicCategory.no.exist") + courseCategoryCode + '\n' : "")
                                + (dicCourseFormat == null ? getMessage("dicCourseFormat.no.exist") + '\n' : "")
                                + (dicLearningObjectType == null ? getMessage("dicLearningObjectType.no.exist") + '\n' : "")
                        , NotificationType.HUMANIZED_HTML);
            }
        }
    }
}