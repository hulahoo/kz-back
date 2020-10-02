package kz.uco.tsadv.web.modules.personal.selflearning;

import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.InfoWindow;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;

import javax.inject.Inject;
import java.util.Map;

public class LearningCenter extends AbstractWindow {

    @WindowParam
    private DicLearningCenter dicLearningCenter;

    @Inject
    private MapViewer map;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (dicLearningCenter != null) {
            setCaption(String.format(getMessage("course.schedule.lc.map"), dicLearningCenter.getLangValue()));

            GeoPoint geoPoint = map.createGeoPoint(43.27025445863985, 76.93449106347657);
            if (dicLearningCenter.getLatitude() != null && dicLearningCenter.getLongitude() != null) {
                geoPoint = map.createGeoPoint(dicLearningCenter.getLatitude(), dicLearningCenter.getLongitude());
                map.addMarker(map.createMarker(dicLearningCenter.getLangValue(), geoPoint, false, ""));
            }

            map.setCenter(geoPoint);
            map.setZoom(14);

            map.addMarkerClickListener(event -> {
                Marker marker = event.getMarker();
                InfoWindow w = map.createInfoWindow(dicLearningCenter.getLangValue(), marker);
                map.openInfoWindow(w);
            });

            map.setVisible(true);
        } else {
            setCaption(String.format(getMessage("course.schedule.lc.map"), ""));
            map.setVisible(false);
        }
    }
}