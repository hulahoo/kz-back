package kz.uco.tsadv.web.modules.learning.dictionary.diclearningcenter;

import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.InfoWindow;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.listeners.click.MapClickListener;
import com.haulmont.charts.gui.map.model.listeners.click.MarkerClickListener;
import com.haulmont.charts.gui.map.model.listeners.drag.MarkerDragListener;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;
import kz.uco.base.web.abstraction.six.AbstractDictionaryEditor;

import javax.inject.Inject;
import java.util.Map;

public class DicLearningCenterEdit extends AbstractDictionaryEditor<DicLearningCenter> {

//    @Inject
//    private MapViewer map;
//
//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//
//        map.setDraggable(true);
//        map.setZoom(14);
//
//        map.addMapClickListener(new MapClickListener() {
//            @Override
//            public void onClick(MapClickEvent event) {
//                map.clearMarkers();
//
//                Marker marker = map.createMarker("LearningCenter", event.getPosition(), true);
//                marker.setClickable(true);
//                map.addMarker(marker);
//
//                getItem().setLatitude(event.getPosition().getLatitude());
//                getItem().setLongitude(event.getPosition().getLongitude());
//            }
//        });
//
//        map.addMarkerDragListener(new MarkerDragListener() {
//            @Override
//            public void onDrag(MarkerDragEvent event) {
//                getItem().setLatitude(event.getMarker().getPosition().getLatitude());
//                getItem().setLongitude(event.getMarker().getPosition().getLongitude());
//            }
//        });
//
//        map.addMarkerClickListener(new MarkerClickListener() {
//            @Override
//            public void onClick(MarkerClickEvent event) {
//                Marker marker = event.getMarker();
//                String caption = String.format(getMessage("start.course.coordinate"),
//                        marker.getPosition().getLatitude(),
//                        marker.getPosition().getLongitude());
//
//                InfoWindow w = map.createInfoWindow(caption, marker);
//                map.openInfoWindow(w);
//            }
//        });
//    }
//
//    @Override
//    protected void postInit() {
//        super.postInit();
//
//        GeoPoint geoPoint = map.createGeoPoint(43.27025445863985, 76.93449106347657);
//        if (!PersistenceHelper.isNew(getItem())) {
//            if (getItem().getLatitude() != null && getItem().getLongitude() != null) {
//                geoPoint = map.createGeoPoint(getItem().getLatitude(), getItem().getLongitude());
//                map.addMarker(map.createMarker(getItem().getLangValue(), geoPoint, true, ""));
//            }
//        }
//
//        map.setCenter(geoPoint);
//    }
}