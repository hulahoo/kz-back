package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.List;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
@MetaClass(name = "tsadv$ProfilePojo")
public class ProfilePojo extends BaseUuidEntity {

    public ProfilePojo() {
        super();
        pgId = id.toString();
    }

    @MetaProperty
    private String pgId;

    @MetaProperty
    private String pId;

    @MetaProperty
    private Boolean showImageTooltip = false;

    @MetaProperty
    private String image;

    @MetaProperty
    private String firstName;

    @MetaProperty
    private String lastName;

    @MetaProperty
    private String fullName;

    @MetaProperty
    private String organization;

    @MetaProperty
    private String position;

    @MetaProperty
    private List<RecognitionTypePojo> recognitionTypes;

    @MetaProperty
    private List<PreferencePojo> preferences;

    @MetaProperty
    private String points;

    @MetaProperty
    private String coins;

    @MetaProperty
    private String heartAward;

    @MetaProperty
    private Long medalCount = 0L;

    @MetaProperty
    private Integer inTeam = 0;

    @MetaProperty
    private String employeeNumber;

    public Integer getInTeam() {
        return inTeam;
    }

    public void setInTeam(Integer inTeam) {
        this.inTeam = inTeam;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getMedalCount() {
        return medalCount;
    }

    public void setMedalCount(Long medalCount) {
        this.medalCount = medalCount;
    }

    public Boolean getShowImageTooltip() {
        return showImageTooltip;
    }

    public void setShowImageTooltip(Boolean showImageTooltip) {
        this.showImageTooltip = showImageTooltip;
    }

    @Override
    public void setId(UUID id) {
        super.setId(id);
        pgId = id.toString();
    }

    public String getHeartAward() {
        return heartAward;
    }

    public void setHeartAward(String heartAward) {
        this.heartAward = heartAward;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPgId() {
        return pgId;
    }

    public void setPgId(String pgId) {
        this.pgId = pgId;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public List<RecognitionTypePojo> getRecognitionTypes() {
        return recognitionTypes;
    }

    public void setRecognitionTypes(List<RecognitionTypePojo> recognitionTypes) {
        this.recognitionTypes = recognitionTypes;
    }

    public List<PreferencePojo> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferencePojo> preferences) {
        this.preferences = preferences;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        image = String.format("./dispatch/person_image/%s", pId);
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        fullName = firstName + " " + lastName;
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
