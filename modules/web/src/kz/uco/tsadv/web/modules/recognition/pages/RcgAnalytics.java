package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.charts.gui.amcharts.model.*;
import com.haulmont.charts.gui.components.charts.RadarChart;
import com.haulmont.charts.gui.components.charts.SerialChart;
import com.haulmont.charts.gui.data.ListDataProvider;
import com.haulmont.charts.gui.data.MapDataItem;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.RecognitionChartService;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;

public class RcgAnalytics extends AbstractWindow {

    @WindowParam
    private PersonGroupExt analyticsPersonGroup;

    @Inject
    private TabSheet tabSheet;
    @Inject
    private VBoxLayout chart1Content, chart2Content, chart3Content, chart4Content;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private RecognitionChartService recognitionChartService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (analyticsPersonGroup != null) {
            loadCharts(1, chart1Content);

            tabSheet.addSelectedTabChangeListener(event -> {
                String tabName = event.getSelectedTab().getName();

                int number;
                VBoxLayout wrapper;

                switch (tabName) {
                    case "chart2": {
                        number = 2;
                        wrapper = chart2Content;
                        break;
                    }
                    case "chart3": {
                        number = 3;
                        wrapper = chart3Content;
                        break;
                    }
                    case "chart4": {
                        number = 4;
                        wrapper = chart4Content;
                        break;
                    }
                    default: {
                        number = 1;
                        wrapper = chart1Content;
                    }
                }

                loadCharts(number, wrapper);
            });
        }
    }

    private void loadCharts(int number, VBoxLayout wrapper) {
        wrapper.removeAll();


        Component chartComponent = null;
        switch (number) {
            case 1: {
                chartComponent = qualitiesChart();
                break;
            }
            case 2: {
                chartComponent = qualitiesRadarChart();
                break;
            }
            case 3: {
                chartComponent = goodsCountChart();
                break;
            }
            case 4: {
                chartComponent = recognitionChart();
                break;
            }
        }

        if (chartComponent != null) {
            wrapper.add(chartComponent);
            wrapper.expand(chartComponent);
        }
    }

    private Component recognitionChart() {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setSizeFull();

        HBoxLayout filter = componentsFactory.createComponent(HBoxLayout.class);
        filter.setSpacing(true);
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(getMessage("rcg.chart.period"));
        label.setAlignment(Alignment.MIDDLE_LEFT);
        filter.add(label);

        LookupField<Integer> lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setAlignment(Alignment.MIDDLE_LEFT);
        lookupField.setTextInputAllowed(false);

        Map<String, Integer> optionsMap = new LinkedHashMap<>();
        optionsMap.put(getMessage("chart.period.week"), 4);
        optionsMap.put(getMessage("chart.period.month"), 3);
        optionsMap.put(getMessage("chart.period.quarter"), 2);
        optionsMap.put(getMessage("chart.period.year"), 1);
        lookupField.setOptionsMap(optionsMap);

        filter.add(lookupField);
        filter.expand(lookupField);
        wrapper.add(filter);

        SerialChart serialChart = componentsFactory.createComponent(SerialChart.class);
        serialChart.setSizeFull();
        serialChart.setTheme(ChartTheme.LIGHT);
        serialChart.setStartDuration(2.);
        serialChart.setRotate(true);

        ValueAxis valueAxis = new ValueAxis();
        valueAxis.setPosition(Position.LEFT);
        valueAxis.setIntegersOnly(true);
        valueAxis.setMinHorizontalGap(300);
        serialChart.addValueAxes(valueAxis);

        Graph graph = new Graph();
        graph.setBalloonText("[[category]]: <b>[[value]]</b>");
        graph.setFillAlphas(1.);
        graph.setLineAlpha(0.1);
        graph.setType(GraphType.COLUMN);
        graph.setValueField("count");
        graph.setColorField("color");
        graph.setShowAllValueLabels(false);
        serialChart.addGraphs(graph);

        Cursor cursor = new Cursor();
        cursor.setCategoryBalloonEnabled(false);
        cursor.setCursorAlpha(0.);
        cursor.setZoomable(false);
        serialChart.setChartCursor(cursor);

        serialChart.setCategoryField("receiver");

        ListDataProvider listDataProvider = new ListDataProvider();
        fillRecognitionCountData(listDataProvider, 0);
        serialChart.setDataProvider(listDataProvider);

        wrapper.add(serialChart);
        wrapper.expand(serialChart);

        lookupField.addValueChangeListener(valueChangeEvent -> {
            Integer period = valueChangeEvent.getValue() != null ? valueChangeEvent.getValue() : 0;

            fillRecognitionCountData(listDataProvider, period);
        });

        return wrapper;
    }

    private void fillRecognitionCountData(ListDataProvider listDataProvider, Integer period) {
        try {
            List<KeyValueEntity> resultList = recognitionChartService.loadRecognitionsCount(analyticsPersonGroup.getId(), period);
            listDataProvider.removeAll();

            for (KeyValueEntity keyValueEntity : resultList) {
                String color = getRandomColor();
                long count = keyValueEntity.getValue("count");
                String receiver = keyValueEntity.getValue("receiver");
                listDataProvider.addItem(new MapDataItem()
                        .add("receiver", receiver)
                        .add("count", count)
                        .add("color", color));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private Component goodsCountChart() {
        SerialChart serialChart = componentsFactory.createComponent(SerialChart.class);
        serialChart.setSizeFull();
        serialChart.setTheme(ChartTheme.LIGHT);
        serialChart.setStartDuration(0.);

        ValueAxis valueAxis = new ValueAxis();
        valueAxis.setPosition(Position.LEFT);
        serialChart.addValueAxes(valueAxis);

        Graph graph = new Graph();
        graph.setBalloonText("[[category]]: <b>[[value]]</b>");
        graph.setFillAlphas(1.);
        graph.setLineAlpha(0.1);
        graph.setType(GraphType.COLUMN);
        graph.setValueField("goods_count");
        graph.setColorField("color");
        graph.setShowAllValueLabels(false);
        serialChart.addGraphs(graph);

        serialChart.setDepth3D(20);
        serialChart.setAngle(30);

        Cursor cursor = new Cursor();
        cursor.setCategoryBalloonEnabled(false);
        cursor.setCursorAlpha(0.);
        cursor.setZoomable(false);
        serialChart.setChartCursor(cursor);

        Legend legend = new Legend();
        legend.setEnabled(true);
        serialChart.setLegend(legend);

        ListDataProvider listDataProvider = new ListDataProvider();
        try {
            List<KeyValueEntity> resultList = recognitionChartService.loadGoodsOrderCount(analyticsPersonGroup.getId());
            for (KeyValueEntity keyValueEntity : resultList) {
                String color = getRandomColor();
                long goodsCount = keyValueEntity.getValue("goods_count");
                String goodsName = keyValueEntity.getValue("goods_name");

                listDataProvider.addItem(new MapDataItem()
                        .add("goods_name", goodsName)
                        .add("goods_count", goodsCount)
                        .add("color", color));

                LegendItem legendItem = new LegendItem();
                legendItem.setTitle(goodsName + " (" + goodsCount + ")");
                legendItem.setColor(Color.valueOf(color));
                legend.addItems(legendItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        serialChart.setDataProvider(listDataProvider);

        serialChart.setCategoryField("goods_name");

        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setLabelsEnabled(false);
        categoryAxis.setAutoGridCount(true);
        categoryAxis.setGridPosition(GridPosition.START);
        serialChart.setCategoryAxis(categoryAxis);

        return serialChart;
    }

    private Component qualitiesChart() {
        SerialChart serialChart = componentsFactory.createComponent(SerialChart.class);
        serialChart.setSizeFull();
        serialChart.setTheme(ChartTheme.LIGHT);
        serialChart.setStartDuration(0.);
        serialChart.setRotate(true);

        ValueAxis valueAxis = new ValueAxis();
        valueAxis.setPosition(Position.LEFT);
        valueAxis.setMinorTickLength(0);
        valueAxis.setLabelFrequency(1.);
        valueAxis.setIntegersOnly(true);
        valueAxis.setMinHorizontalGap(200);
        serialChart.addValueAxes(valueAxis);

        Graph graph = new Graph();
        graph.setBalloonText("[[category]]: <b>[[value]]</b>");
        graph.setFillAlphas(1.);
        graph.setLineAlpha(0.1);
        graph.setProCandlesticks(false);
        graph.setType(GraphType.COLUMN);
        graph.setValueField("quality_count");
        graph.setColorField("color");
        graph.setShowAllValueLabels(false);
        serialChart.addGraphs(graph);

        Cursor cursor = new Cursor();
        cursor.setCategoryBalloonEnabled(false);
        cursor.setCursorAlpha(0.);
        cursor.setZoomable(false);
        serialChart.setChartCursor(cursor);

        Legend legend = new Legend();
        legend.setEnabled(true);
        serialChart.setLegend(legend);

        ListDataProvider listDataProvider = new ListDataProvider();
        try {
            List<KeyValueEntity> resultList = recognitionChartService.loadQualities(analyticsPersonGroup.getId());
            for (KeyValueEntity keyValueEntity : resultList) {
                String color = getRandomColor();
                long qualityCount = keyValueEntity.getValue("quality_count");
                String qualityName = keyValueEntity.getValue("quality_name");
                listDataProvider.addItem(new MapDataItem()
                        .add("quality_name", qualityName)
                        .add("quality_count", qualityCount)
                        .add("color", color));

                LegendItem legendItem = new LegendItem();
                legendItem.setTitle(qualityName + " (" + qualityCount + ")");
                legendItem.setColor(Color.valueOf(color));
                legend.addItems(legendItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        serialChart.setDataProvider(listDataProvider);

        serialChart.setCategoryField("quality_name");

        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setLabelsEnabled(false);
        categoryAxis.setAutoGridCount(true);
        categoryAxis.setGridPosition(GridPosition.START);
        serialChart.setCategoryAxis(categoryAxis);

        return serialChart;
    }

    private String getRandomColor() {
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    private Component qualitiesRadarChart() {
        RadarChart radarChart = componentsFactory.createComponent(RadarChart.class);
        radarChart.setSizeFull();
        radarChart.setCategoryField("quality_name");
        radarChart.setStartDuration(1.);
        radarChart.setTheme(ChartTheme.LIGHT);

        CollectionDatasource resultDataDs = new DsBuilder(getDsContext())
                .setJavaClass(KeyValueEntity.class)
                .setId("resultDataDs")
                .buildValuesCollectionDatasource();

        try {
            recognitionChartService.loadQualities(analyticsPersonGroup.getId())
                    .forEach(resultDataDs::includeItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        radarChart.setDatasource(resultDataDs);

        ValueAxis valueAxis = new ValueAxis();
        valueAxis.setAutoGridCount(false);
        valueAxis.setAxisAlpha(0.2);
        valueAxis.setFillAlpha(0.05);
        valueAxis.setFillColor(Color.WHITE);
        valueAxis.setGridAlpha(0.08);
        valueAxis.setGridType(GridType.CIRCLES);
        valueAxis.setMinimum(0.);
        valueAxis.setPosition(Position.LEFT);
        radarChart.addValueAxes(valueAxis);

        Graph graph = new Graph();
        graph.setBalloonText("[[category]]: [[value]]");
        graph.setBullet(BulletType.ROUND);
        graph.setFillAlphas(0.3);
        graph.setValueField("quality_count");
        radarChart.addGraphs(graph);

        return radarChart;
    }
}