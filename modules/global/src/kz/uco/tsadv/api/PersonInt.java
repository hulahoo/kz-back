package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.List;
import java.util.UUID;

/**
 * Лицо (Интеграционный объект)
 */
@MetaClass(name = "tsadv$PersonInt")
public class PersonInt extends AbstractEntityInt {
    private static final long serialVersionUID = -3865968263051028996L;

    /**
     * Имя
     */
    @MetaProperty
    protected String firstName;

    /**
     * Фамилия
     */
    @MetaProperty
    protected String lastName;

    /**
     * Отчество
     */
    @MetaProperty
    protected String middleName;

    /**
     * Дата рождения
     */
    @MetaProperty
    protected String birthDate;

    /**
     * ИИН
     */
    @MetaProperty
    protected String nationalIdentifier;

    /**
     * Id пола
     */
    @MetaProperty
    protected UUID sex;

    /**
     * Пол
     */
    @MetaProperty
    protected String sexName;

    /**
     * Id национальности
     */
    @MetaProperty
    protected UUID nationality;

    /**
     * Национальность
     */
    @MetaProperty
    protected String nationalityName;

    /**
     * Id гражданства
     */
    @MetaProperty
    protected UUID citizenship;

    /**
     * Гражданство
     */
    @MetaProperty
    protected String citizenshipName;

    /**
     * Id семейного положения
     */
    @MetaProperty
    protected UUID maritalStatus;

    /**
     * Семейное положение
     */
    @MetaProperty
    protected String maritalStatusName;

    /**
     * Образование
     */
    @MetaProperty
    protected List<PersonEducationInt> education;

    /**
     * Контактная информация
     */
    @MetaProperty
    protected List<PersonContactInt> contacts;

    /**
     * Опыт работы
     */
    @MetaProperty
    protected List<PersonExperienceInt> experience;

    /**
     * Приложения
     */
    @MetaProperty
    protected List<PersonAttachmentInt> attachments;

    /**
     * Навыки
     */
    @MetaProperty
    protected List<PersonCompetenceInt> competences;

    /**
     * Фото
     */
    @MetaProperty
    protected String photo;

    /**
     * Город
     */
    @MetaProperty
    protected String cityName;




    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }


    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }


    public void setCompetences(List<PersonCompetenceInt> competences) {
        this.competences = competences;
    }

    public List<PersonCompetenceInt> getCompetences() {
        return competences;
    }


    public void setAttachments(List<PersonAttachmentInt> attachments) {
        this.attachments = attachments;
    }

    public List<PersonAttachmentInt> getAttachments() {
        return attachments;
    }


    public void setExperience(List<PersonExperienceInt> experience) {
        this.experience = experience;
    }

    public List<PersonExperienceInt> getExperience() {
        return experience;
    }


    public void setContacts(List<PersonContactInt> contacts) {
        this.contacts = contacts;
    }

    public List<PersonContactInt> getContacts() {
        return contacts;
    }


    public UUID getSex() {
        return sex;
    }

    public void setSex(UUID sex) {
        this.sex = sex;
    }


    public UUID getNationality() {
        return nationality;
    }

    public void setNationality(UUID nationality) {
        this.nationality = nationality;
    }


    public UUID getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(UUID citizenship) {
        this.citizenship = citizenship;
    }


    public UUID getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(UUID maritalStatus) {
        this.maritalStatus = maritalStatus;
    }


    public void setEducation(List<PersonEducationInt> education) {
        this.education = education;
    }

    public List<PersonEducationInt> getEducation() {
        return education;
    }


    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getSexName() {
        return sexName;
    }

    public void setNationalityName(String nationalityName) {
        this.nationalityName = nationalityName;
    }

    public String getNationalityName() {
        return nationalityName;
    }

    public void setCitizenshipName(String citizenshipName) {
        this.citizenshipName = citizenshipName;
    }

    public String getCitizenshipName() {
        return citizenshipName;
    }

    public void setMaritalStatusName(String maritalStatusName) {
        this.maritalStatusName = maritalStatusName;
    }

    public String getMaritalStatusName() {
        return maritalStatusName;
    }


    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }



}