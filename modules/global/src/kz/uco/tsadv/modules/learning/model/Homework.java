package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_HOMEWORK")
@Entity(name = "tsadv_Homework")
public class Homework extends AbstractParentEntity {
    private static final long serialVersionUID = -3785067486450885836L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Lob
    @NotNull
    @Column(name = "INSTRUCTIONS", nullable = false)
    protected String instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUCTION_FILE_ID")
    protected FileDescriptor instructionFile;

    public FileDescriptor getInstructionFile() {
        return instructionFile;
    }

    public void setInstructionFile(FileDescriptor instructionFile) {
        this.instructionFile = instructionFile;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}