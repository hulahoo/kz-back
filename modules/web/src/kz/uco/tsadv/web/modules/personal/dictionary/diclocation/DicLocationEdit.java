package kz.uco.tsadv.web.modules.personal.dictionary.diclocation;

import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.InfoWindow;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.listeners.click.MapClickListener;
import com.haulmont.charts.gui.map.model.listeners.click.MarkerClickListener;
import com.haulmont.charts.gui.map.model.listeners.drag.MarkerDragListener;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.Map;

public class DicLocationEdit extends kz.uco.base.web.dictionary.diclocation.DicLocationEdit {

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    protected boolean showCityField() {
        return true;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        MapViewer map = componentsFactory.createComponent(MapViewer.class);
        map.setStyleName("panel-full-container panel");
        map.setHeight("300px");
        map.setWidthFull();

        map.setDraggable(true);
        map.setZoom(14);

        map.addMapClickListener(new MapClickListener() {
            @Override
            public void onClick(MapClickEvent event) {
                map.clearMarkers();

                Marker marker = map.createMarker("Location", event.getPosition(), true);
                marker.setClickable(true);
                map.addMarker(marker);

                getItem().setLatitude(event.getPosition().getLatitude());
                getItem().setLongitude(event.getPosition().getLongitude());
            }
        });

        map.addMarkerDragListener(new MarkerDragListener() {
            @Override
            public void onDrag(MarkerDragEvent event) {
                getItem().setLatitude(event.getMarker().getPosition().getLatitude());
                getItem().setLongitude(event.getMarker().getPosition().getLongitude());
            }
        });

        map.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void onClick(MarkerClickEvent event) {
                Marker marker = event.getMarker();
                String caption = String.format(getMessage("start.course.coordinate"),
                        marker.getPosition().getLatitude(),
                        marker.getPosition().getLongitude());

                InfoWindow w = map.createInfoWindow(caption, marker);
                map.openInfoWindow(w);
            }
        });


        GeoPoint geoPoint = map.createGeoPoint(43.27025445863985, 76.93449106347657);
        if (!PersistenceHelper.isNew(getItem())) {
            if (getItem().getLatitude() != null && getItem().getLongitude() != null) {
                geoPoint = map.createGeoPoint(getItem().getLatitude(), getItem().getLongitude());
                map.addMarker(map.createMarker(getItem().getLangValue(), geoPoint, true, ""));
            }
        }

        map.setCenter(geoPoint);

        mapViewer.add(map);
    }
}