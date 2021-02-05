package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PositionDataJson implements Serializable {

    protected ArrayList<PositionJson> positions;

    public ArrayList<PositionJson> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<PositionJson> positions) {
        this.positions = positions;
    }
}