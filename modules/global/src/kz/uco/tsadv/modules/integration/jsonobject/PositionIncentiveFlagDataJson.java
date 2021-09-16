package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PositionIncentiveFlagDataJson implements Serializable {

    protected ArrayList<PositionIncentiveFlagJson> positionIncentiveFlags;

    public ArrayList<PositionIncentiveFlagJson> getPositionIncentiveFlags() {
        return positionIncentiveFlags;
    }

    public void setPositionIncentiveFlags(ArrayList<PositionIncentiveFlagJson> positionIncentiveFlags) {
        this.positionIncentiveFlags = positionIncentiveFlags;
    }
}
