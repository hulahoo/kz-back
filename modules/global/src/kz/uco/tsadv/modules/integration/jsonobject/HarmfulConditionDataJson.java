package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class HarmfulConditionDataJson implements Serializable {

    protected ArrayList<HarmfulConditionJson> harmfulConditions;

    public ArrayList<HarmfulConditionJson> getHarmfulConditions() {
        return harmfulConditions;
    }

    public void setHarmfulConditions(ArrayList<HarmfulConditionJson> harmfulConditions) { this.harmfulConditions = harmfulConditions; }
}
