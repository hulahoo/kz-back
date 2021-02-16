package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonExperienceDataJson implements Serializable {

    protected ArrayList<PersonExperienceJson> personExperiences;

    public ArrayList<PersonExperienceJson> getPersonExperiences() {
        return personExperiences;
    }

    public void setPersonExperiences(ArrayList<PersonExperienceJson> personExperiences) {
        this.personExperiences = personExperiences;
    }
}
