package kz.uco.tsadv.filter;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.model.Competence;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.web.modules.personal.persongroup.Transliteration;
import kz.uco.tsadv.web.toolkit.ui.slider.SliderComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
public class StoreStyleFilter {
    private static final Logger log = LoggerFactory.getLogger(StoreStyleFilter.class);
    private final CommonService commonService;
    private Configuration configuration;
    private RecruitmentConfig recruitmentConfig;

    Messages messages;
    ComponentsFactory componentsFactory;

    private CollectionDatasource<? extends Entity, UUID> datasource;
    private Map<String, Element> filterByMap;
    private PopupButton filterByButton;
    private Button searchButton;
    private Button refreshButton;
    private VBoxLayout filterContainer;
    private ScrollBoxLayout filterScrollBox;
    private HBoxLayout buttonsPanel;
    private List<Entity> datasourceItems;
    private String datasourceQuery;
    private String view;
    protected Map<LookupField, LookupField> competenceMap = new HashMap<>();

    private StoreStyleFilter() {
        this.messages = AppBeans.get(Messages.NAME);
        this.componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        this.commonService = AppBeans.get(CommonService.class);
    }

    public static StoreStyleFilter init(CollectionDatasource<? extends Entity, UUID> datasource,
                                        Map<String, Element> filterByMap) {
        StoreStyleFilter storeStyleFilter = new StoreStyleFilter();
        storeStyleFilter.datasource = datasource;
        storeStyleFilter.filterByMap = filterByMap;
        storeStyleFilter.datasourceQuery = datasource.getQuery();
        storeStyleFilter.configuration = AppBeans.get(Configuration.class);
        storeStyleFilter.recruitmentConfig = storeStyleFilter.configuration.getConfig(RecruitmentConfig.class);
        storeStyleFilter.createFilterElements();
        storeStyleFilter.createSearchButton();
        storeStyleFilter.createRefreshButton();
        storeStyleFilter.createFilterContainer();
        return storeStyleFilter;
    }

    public void search() {
        StringBuilder queryFilter = new StringBuilder();

        this.filterByMap.forEach((k, v) -> {
            if (v.isSelected()) {
                ComponentContainer c = (ComponentContainer) this.filterScrollBox.getComponent(v.componentId);

                switch (v.getElementType()) {
                    case TEXT:
                        Object textValue = ((HasValue) c.getComponent(v.textId)).getValue();
                        if (textValue != null && ((String) textValue).trim().length() > 0) {
                            String transliteralText = Transliteration.toTranslit(textValue.toString()); //transliteration of the text
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (");
                            String s = v.getQueryFilter().replace("?",
                                    Operation.LIKE.formatJpqlString(v.textId))
                                    .replace("!",
                                            Operation.LIKE_BY_STRING.formatJpqlString(transliteralText));
                            queryFilter.append(s);
                            queryFilter.append(")");
                        }
                        break;
                    case CHECKBOXCITY:
                        Object checkBox = c.getComponent(v.checkBoxCityId);
                        Object value = null;
                        if (checkBox != null) {
                            value = ((HasValue) checkBox).getValue();
                        }
                        if (value != null && !(Boolean) value) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(" (select concat(case when dc.langValue1 is not null " +
                                    " then dc.langValue1 else '&' end," +
                                    " concat('&',concat(case when dc.langValue2 is not null " +
                                    " then dc.langValue2 else '&' end," +
                                    " concat('&',concat(case when dc.langValue3 is not null " +
                                    " then dc.langValue3 else '&' end," +
                                    " concat('&',concat(case when dc.langValue4 is not null " +
                                    " then dc.langValue4 else '&' end," +
                                    " concat('&',case when dc.langValue5 is not null " +
                                    " then dc.langValue5 else '&' end)))))))) " +
                                    " from base$DicCity dc where dc.id = e.requisition.location.id)" +
                                    " like concat('%',concat((select ad.city from tsadv$Address ad " +
                                    " where ad.personGroup.id = cpg.id " +
                                    " and ad.addressType.code = 'RESIDENCE' " +
                                    " and current_date between ad.startDate and ad.endDate ),'%')))");
                        }
                        break;
                    case SELECT:
                        Object selectValue = ((HasValue) c.getComponent(v.selectId)).getValue();
                        if (selectValue != null && !((Collection) selectValue).isEmpty()) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.IN.formatJpqlString(v.selectId))).append(")");
                        }
                        break;
                    case DATE:
                        Object date1Value = ((HasValue) c.getComponent(v.date1Id)).getValue();
                        Object date2Value = ((HasValue) c.getComponent(v.date2Id)).getValue();

                        if (date1Value != null && date2Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.BETWEEN.formatJpqlString(v.date1Id, v.date2Id))).append(")");
                        } else if (date1Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.GREATER_OR_EQUAL.formatJpqlString(v.date1Id))).append(")");
                        } else if (date2Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.LESS_OR_EQUAL.formatJpqlString(v.date2Id))).append(")");
                        }
                        break;
                    case NUMBER:
                        Object number1Value = ((HasValue) c.getComponent(v.number1Id)).getValue();
                        Object number2Value = ((HasValue) c.getComponent(v.number2Id)).getValue();

                        if (number1Value != null && number2Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.BETWEEN.formatJpqlString(v.number1Id, v.number2Id))).append(")");
                        } else if (number1Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.GREATER_OR_EQUAL.formatJpqlString(v.number1Id))).append(")");
                        } else if (number2Value != null) {
                            if (queryFilter.length() > 0)
                                queryFilter.append(" and ");
                            queryFilter.append(" (").append(v.getQueryFilter().replace("?", Operation.LESS_OR_EQUAL.formatJpqlString(v.number2Id))).append(")");
                        }
                        break;
                    case LOOKUP:
                        for (Map.Entry<LookupField, LookupField> entry : competenceMap.entrySet()) {
                            if (entry.getKey() != null && entry.getValue() != null) {
                                if (entry.getKey().getValue() != null && entry.getValue().getValue() != null) {
                                    queryFilter.append(" and")
                                            .append("(")
                                            .append("(select ce from tsadv$CompetenceElement ce " +
                                                    " where ce.competenceGroup.id = '" + ((Competence) entry.getKey().getValue()).getGroup().getId() + "'" +
                                                    " and cpg.id = ce.personGroup.id " +
                                                    " and ce.scaleLevel.levelNumber >= " + ((ScaleLevel) entry.getValue().getValue()).getLevelNumber())
                                            .append(")")
                                            .append(" MEMBER OF cpg.competenceElements ")
                                            .append(")");
                                }
                            }
                        }
                        break;
                }
            }
        });

        datasource.setQuery(CustomFilter.getFilteredQuery(this.datasourceQuery, queryFilter.toString()));
//        log.info(datasource.getQuery());
        this.datasource.refresh();
    }

    private void createFilterContainer() {
        this.filterContainer = this.componentsFactory.createComponent(VBoxLayout.class);
        this.filterContainer.setSpacing(true);
        this.filterContainer.setWidthFull();
        this.filterContainer.setHeightFull();
        this.buttonsPanel = this.componentsFactory.createComponent(HBoxLayout.class);
        this.buttonsPanel.setWidthAuto();
        this.buttonsPanel.setSpacing(true);
        this.buttonsPanel.add(this.refreshButton);
        this.buttonsPanel.add(this.filterByButton);
        this.buttonsPanel.add(this.searchButton);

        this.filterContainer.add(buttonsPanel);
        this.filterContainer.add(this.filterScrollBox);
        this.filterContainer.expand(this.filterScrollBox);
    }

    private void createSearchButton() {
        this.searchButton = this.componentsFactory.createComponent(Button.class);
        this.searchButton.setIcon("icons/search.png");
        //this.searchButton.setCaption(this.messages.getMainMessage("CustomFilter.search"));
        this.searchButton.setCaption(this.messages.getMainMessage("btn.empty.caption"));
        this.searchButton.setAction(new BaseAction("search") {
            @Override
            public void actionPerform(Component component) {
                search();
            }
        });
    }

    private void createRefreshButton() {
        this.refreshButton = this.componentsFactory.createComponent(Button.class);
        this.refreshButton.setIcon("icons/refresh.png");
        this.refreshButton.setCaption("");
        this.refreshButton.setAction(new BaseAction("refresh") {
            @Override
            public void actionPerform(Component component) {
                refresh();
            }
        });
    }

    public void refresh() {
        this.datasourceItems.clear();
        this.datasourceItems = null;
        filterContainer.remove(filterScrollBox);
        buttonsPanel.remove(filterByButton);
        buttonsPanel.remove(searchButton);
        createFilterElements();
        createSearchButton();
        buttonsPanel.add(filterByButton);
        buttonsPanel.add(searchButton);
        filterContainer.add(filterScrollBox);
        filterContainer.expand(filterScrollBox);
    }

    public void setFilterByMap(Map<String, Element> filterByMap) {
        this.filterByMap = filterByMap;
    }

    public Button getRefreshButton() {
        return this.refreshButton;
    }

    private void createFilterElements() {
        this.filterByButton = this.componentsFactory.createComponent(PopupButton.class);
        //this.filterByButton.setCaption(this.messages.getMainMessage("StoreStyleFilter.filterBy"));
        this.filterByButton.setIcon("font-icon:FILTER");

        this.filterScrollBox = this.componentsFactory.createComponent(ScrollBoxLayout.class);
        this.filterScrollBox.setWidthFull();
        this.filterScrollBox.setScrollBarPolicy(ScrollBoxLayout.ScrollBarPolicy.VERTICAL);
        this.filterScrollBox.setHeightFull();

        for (String filterBy : this.filterByMap.keySet()) {
            Element filterElement = this.filterByMap.get(filterBy);

            GroupBoxLayout groupContainer = this.componentsFactory.createComponent(GroupBoxLayout.class);
            groupContainer.setId(filterElement.componentId);
            groupContainer.setCaption(filterElement.getCaption());
            groupContainer.setCollapsable(true);
            groupContainer.setWidthFull();
//            groupContainer.setStyleName("widget-group-box");

            this.filterByButton.addAction(new BaseAction(filterBy)
                    .withHandler(e -> {
                        filterElement.setSelected(!filterElement.isSelected());
                        this.filterByButton.getAction(filterBy).setIcon(filterElement.isSelected() ? "icons/ok.png" : "images/empty.png");
                        groupContainer.setVisible(filterElement.isSelected());
                    })
                    .withCaption(filterElement.getCaption()));

            this.filterByButton.getAction(filterBy).setIcon(filterElement.isSelected() ? "icons/ok.png" : "images/empty.png");

            switch (filterElement.getElementType()) {
                case TEXT:
                    TextField text = this.componentsFactory.createComponent(TextField.class);
                    text.setWidthFull();
                    text.setId(filterElement.textId);
                    if (filterElement.getSelectedValues() != null && filterElement.getSelectedValues().length == 1)
                        text.setValue(filterElement.getSelectedValues()[0]);
                    groupContainer.add(text);
                    break;
                case DATE:
                    VBoxLayout dateContainer = this.componentsFactory.createComponent(VBoxLayout.class);
                    dateContainer.setSpacing(true);

                    DateField dateFrom = this.componentsFactory.createComponent(DateField.class);
                    dateFrom.setResolution(DateField.Resolution.DAY);
                    dateFrom.setWidth("120px");
                    dateFrom.setId(filterElement.date1Id);

                    DateField dateTo = this.componentsFactory.createComponent(DateField.class);
                    dateTo.setResolution(DateField.Resolution.DAY);
                    dateTo.setWidth("120px");
                    dateTo.setId(filterElement.date2Id);

                    Label dash = this.componentsFactory.createComponent(Label.class);
                    dash.setValue("&mdash;");
                    dash.setHtmlEnabled(true);

                    dateContainer.add(dateFrom);
                    /* dateContainer.add(dash);*/
                    dateContainer.add(dateTo);

                    if (filterElement.getSelectedValues() != null && filterElement.getSelectedValues().length == 2) {
                        dateFrom.setValue(filterElement.getSelectedValues()[0]);
                        dateTo.setValue(filterElement.getSelectedValues()[1]);
                    }

                    groupContainer.add(dateContainer);
                    break;
                case NUMBER:
                    VBoxLayout numberVContainer = this.componentsFactory.createComponent(VBoxLayout.class);
                    HBoxLayout numberHContainer = this.componentsFactory.createComponent(HBoxLayout.class);
                    numberHContainer.setSpacing(true);
                    numberHContainer.setWidthAuto();
                    numberVContainer.setSpacing(true);
                    numberVContainer.setWidth("85%");

                    TextField<Double> numberFrom = this.componentsFactory.createComponent(TextField.class);
                    numberFrom.setDatatype(Datatypes.get(DoubleDatatype.NAME));
                    numberFrom.setWidth("120px");
                    numberFrom.setId(filterElement.number1Id);

                    TextField<Double> numberTo = this.componentsFactory.createComponent(TextField.class);
                    numberTo.setDatatype(Datatypes.get(DoubleDatatype.NAME));
                    numberTo.setWidth("120px");
                    numberTo.setId(filterElement.number2Id);

                    Label numberDash = this.componentsFactory.createComponent(Label.class);
                    numberDash.setValue("&mdash;");
                    numberDash.setHtmlEnabled(true);

                    Layout numberVContainerLayout = (Layout) WebComponentsHelper.unwrap(numberVContainer);
                    SliderComponent slider = new SliderComponent();
                    slider.setMinValue(filterElement.getMinValue());
                    slider.setMaxValue(filterElement.getMaxValue());
                    slider.setValue(new double[]{slider.getMinValue(), slider.getMaxValue()});
                    numberFrom.setValue(slider.getMinValue());
                    numberTo.setValue(slider.getMaxValue());

                    slider.setWidth("100%");
                    slider.setStyleName("v-slider");
                    slider.setListener(newValue -> {
                        numberFrom.setValue(newValue[0]);
                        numberTo.setValue(newValue[1]);
                    });

                    numberFrom.addValueChangeListener(e -> {
                        if (e.getValue() != null) {
                            if ((Double) e.getValue() < slider.getMinValue())
                                slider.setMinValue((Double) e.getValue());
                            slider.setValue(new double[]{(Double) e.getValue(), slider.getValue()[1]});
                        }
                    });

                    numberTo.addValueChangeListener(e -> {
                        if (e.getValue() != null) {
                            if ((Double) e.getValue() > slider.getMaxValue())
                                slider.setMaxValue((Double) e.getValue());
                            slider.setValue(new double[]{slider.getValue()[0], (Double) e.getValue()});
                        }
                    });

                    numberHContainer.add(numberFrom);
                    numberHContainer.add(numberDash);
                    numberHContainer.add(numberTo);

                    if (filterElement.getSelectedValues() != null && filterElement.getSelectedValues().length == 2) {
                        slider.setValue(new double[]{(Double) filterElement.getSelectedValues()[0], (Double) filterElement.getSelectedValues()[1]});
                        numberFrom.setValue((Double) filterElement.getSelectedValues()[0]);
                        numberTo.setValue((Double) filterElement.getSelectedValues()[1]);
                    }

                    numberVContainerLayout.addComponent(slider);
                    numberVContainer.add(numberHContainer);
                    groupContainer.add(numberVContainer);
                    break;
                case SELECT:
                    if (filterElement.getView() != null && !filterElement.getView().equals("")) {
                        this.view = filterElement.getView();
                    }
                    VBoxLayout selectContainer = this.componentsFactory.createComponent(VBoxLayout.class);
                    selectContainer.setWidthFull();
                    OptionsGroup optionsGroupMulti = this.componentsFactory.createComponent(OptionsGroup.class);
                    optionsGroupMulti.setMultiSelect(true);
                    optionsGroupMulti.setId(filterElement.selectId);

                    Map<String, Object> optionsMap = new LinkedHashMap<>();
                    final List<Object> selectedValues = filterElement.getSelectedValues() != null && filterElement.getSelectedValues().length > 0 ? Arrays.asList(filterElement.getSelectedValues()) : null;
                    final Map<Object, Long> statisticsMap = filterElement.isShowStatistics()
                            && filterElement.getDatasourceProperty() != null
                            && filterElement.getOptionsMap() != null ? this
                            .getDatasourceItems()
                            .stream()
                            .map(e -> e.getValueEx(filterElement.getDatasourceProperty()))
                            .collect(Collectors.groupingBy(o -> o, Collectors.counting())) : null;
                    this.view = null;
                    List<ElementOption> optionsList = new ArrayList<>();

                    filterElement.getOptionsMap().forEach((k, v) -> {
                        ElementOption elementOption = new ElementOption();
                        elementOption.setName(v);
                        elementOption.setValue(k);
                        if (selectedValues != null && selectedValues.indexOf(k) > -1)
                            elementOption.setSelected(true);

                        if (statisticsMap != null && statisticsMap.containsKey(k))
                            elementOption.setCount(statisticsMap.get(k));

                        optionsList.add(elementOption);
                    });

                    if (selectedValues != null)
                        optionsList.sort(Comparator.comparingInt(o -> o.isSelected() ? 0 : (o.getCount() != null && o.getCount() > 0 ? 1 : 2)));


                    Long fetchCount = ((Integer) optionsList.size()).longValue();
                    if (filterElement.getFetchCount() != null && filterElement.getFetchCount() > 0 && filterElement.getFetchCount() < fetchCount)
                        fetchCount = filterElement.getFetchCount();
                    if (selectedValues != null && selectedValues.size() > fetchCount)
                        fetchCount = ((Integer) selectedValues.size()).longValue();

                    optionsList.stream().limit(fetchCount).forEach(o -> {
                        o.setFetched(true);
                        optionsMap.put(o.getCount() != null ? o.getName() + " (" + o.getCount() + ")" : o.getName(), o.getValue());
                    });

                    filterElement.elementOptionsList = optionsList;
                    optionsGroupMulti.setOptionsMap(optionsMap);
                    if (filterElement.getSelectedValues() != null && filterElement.getSelectedValues().length > 0) {
                        optionsGroupMulti.setValue(selectedValues);
                    }
                    optionsGroupMulti.setWidthAuto();
                    optionsGroupMulti.setHeightAuto();
                    selectContainer.add(optionsGroupMulti);
                    groupContainer.add(selectContainer);

                    if (fetchCount < optionsList.size()) {
                        LinkButton linkButton = this.componentsFactory.createComponent(LinkButton.class);

                        linkButton.setAction(new BaseAction("showMore")
                                .withIcon("icons/plus-btn.png")
                                .withCaption(String.format(this.messages.getMainMessage("StoreStyleFilter.showMore"), filterElement.getFetchCount()))
                                .withHandler(event -> {
                                    Map<String, Object> m = new LinkedHashMap<>(optionsGroupMulti.getOptionsMap());
                                    List<Object> v = new ArrayList((Integer) optionsGroupMulti.getValue());

                                    filterElement.elementOptionsList
                                            .stream()
                                            .filter(o -> !o.isFetched())
                                            .limit(filterElement.getFetchCount())
                                            .forEach(o ->
                                                    {
                                                        o.setFetched(true);
                                                        m.put(o.getCount() != null ? o.getName() + " (" + o.getCount() + ")" : o.getName(), o.getValue());
                                                    }
                                            );

                                    if (filterElement.elementOptionsList
                                            .stream()
                                            .filter(o -> !o.isFetched())
                                            .count() == 0)
                                        linkButton.setVisible(false);

                                    optionsGroupMulti.setOptionsMap(m);
                                    optionsGroupMulti.setValue(v);
                                }));
                        groupContainer.add(linkButton);
                    }

                    break;
                case CHECKBOX:
                    OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                    optionsGroup.setMultiSelect(true);
                    optionsGroup.setOptionsList(commonService.getEntities(DicSex.class,
                            "select e from base$DicSex e", null, null));
                    groupContainer.add(optionsGroup);
                    break;
                case CHECKBOXCITY:
                    CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
                    checkBox.setCaption(messages.getMessage(
                            "kz.uco.tsadv.web.modules.recruitment.requisition.frames",
                            "fromAllCities"));
                    checkBox.setValue(recruitmentConfig.getFilterCityEnableForJobRequest());
                    checkBox.setId(filterElement.checkBoxCityId);
                    groupContainer.add(checkBox);
                    break;

                case LOOKUP:
                    //Компетенции
                    VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                    vBoxLayout.setSpacing(true);
                    LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                    linkButton.setIcon("font-icon:PLUS");
                    linkButton.setCaption("");
                    groupContainer.add(linkButton);
                    competenceMap.clear();
                    linkButton.setAction(new BaseAction("addCompetence") {
                        @Override
                        public void actionPerform(Component component) {
                            HBoxLayout competenceHboxLayout = componentsFactory.createComponent(HBoxLayout.class);
                            competenceHboxLayout.setSpacing(true);
                            LookupField<Competence> competenceLookupField = componentsFactory.createComponent(LookupField.class);
                            competenceLookupField.setNullOptionVisible(false);
                            competenceLookupField.setOptionsList(commonService.getEntities(Competence.class, "select e from tsadv$Competence e where current_date between e.startDate and e.endDate",
                                    null, "competence.edit"));
                            LookupField scaleLevelLookupField = componentsFactory.createComponent(LookupField.class);
                            scaleLevelLookupField.setNullOptionVisible(false);
                            competenceLookupField.addValueChangeListener(e -> {
                                scaleLevelLookupField.setValue(null);
                                if (e.getValue() != null) {
                                    scaleLevelLookupField.setOptionsList(((Competence) e.getValue()).getScale().getScaleLevels());
                                } else {
                                    scaleLevelLookupField.setOptionsList(new ArrayList());
                                }
                            });

                            LinkButton competenceRemoveButton = componentsFactory.createComponent(LinkButton.class);
                            competenceRemoveButton.setAction(new BaseAction("remove") {
                                @Override
                                public void actionPerform(Component component) {
                                    super.actionPerform(component);
                                    competenceMap.remove(competenceHboxLayout.getComponent(0));
                                    vBoxLayout.remove(competenceHboxLayout);
                                }
                            });
                            competenceRemoveButton.setCaption("");
                            competenceRemoveButton.setIcon("font-icon:CLOSE");
                            competenceHboxLayout.add(competenceLookupField);
                            competenceHboxLayout.add(scaleLevelLookupField);
                            competenceHboxLayout.add(competenceRemoveButton);
                            vBoxLayout.add(competenceHboxLayout);
                            groupContainer.add(vBoxLayout);
                            competenceMap.put(competenceLookupField, scaleLevelLookupField);
                        }
                    });
                    break;
            }
            groupContainer.setVisible(filterElement.isSelected());
            this.filterScrollBox.add(groupContainer);
        }
    }

    private List<Entity> getDatasourceItems() {
        if (this.datasourceItems != null) return this.datasourceItems;
        DataManager dataManager = AppBeans.get(DataManager.NAME);
        LoadContext loadContext = this.datasource.getCompiledLoadContext().copy();
        loadContext.getQuery().setMaxResults(0);
        View view = loadContext.getView();
        if (this.view != null && !this.view.equals("")) {
            loadContext.setView(this.view);
        }
//        this.datasourceItems = commonService.getEntities(Entity.class, this.datasource.getQuery(),
//                this.datasource.getLastRefreshParameters(),this.datasource.getView().getName());
        this.datasourceItems = dataManager.loadList(loadContext);
        loadContext.setView(view);
        return this.datasourceItems;
    }

    public Component getFilterComponent() {
        return this.filterContainer;
    }

    public static class Element {
        private ElementType elementType;
        private String caption;
        private String datasourceProperty;
        private Map<Object, String> optionsMap;
        private Object[] selectedValues;
        private boolean selected;
        private boolean showStatistics;
        private Double minValue;
        private Double maxValue;
        private List<ElementOption> elementOptionsList;
        private Long fetchCount;

        private String view;
        private String componentId;
        private String textId;
        private String selectId;
        private String date1Id;
        private String date2Id;
        private String number1Id;
        private String number2Id;
        private String checkBox1Id;
        private String checkBox2Id;
        private String checkBoxCityId;
        protected String lookupValue1;
        protected String lookupValue2;

        public Element(ElementType elementType) {
            this.elementType = elementType;
            this.componentId = "f_" + (new Random().nextInt(100000));

            switch (elementType) {
                case TEXT:
                    this.textId = this.componentId + "t_" + (new Random().nextInt(100000));
                    break;
                case SELECT:
                    this.selectId = this.componentId + "s_" + (new Random().nextInt(100000));
                    break;
                case NUMBER:
                    this.number1Id = this.componentId + "n1_" + (new Random().nextInt(100000));
                    this.number2Id = this.componentId + "n2_" + (new Random().nextInt(100000));
                    break;
                case DATE:
                    this.date1Id = this.componentId + "d1_" + (new Random().nextInt(100000));
                    this.date2Id = this.componentId + "d2_" + (new Random().nextInt(100000));
                    break;
                case CHECKBOX:
                    this.checkBox1Id = this.componentId + "ch1_" + (new Random().nextInt(100000));
                    break;
                case CHECKBOXCITY:
                    this.checkBoxCityId = this.componentId + "chCity_" + (new Random().nextInt(100000));
                    break;
                case LOOKUP:
                    this.lookupValue1 = this.componentId + "look_" + (new Random().nextInt(100000));
                    this.lookupValue2 = this.componentId + "look_" + (new Random().nextInt(100000));
                    break;
            }
        }

        private String queryFilter;

        public ElementType getElementType() {
            return elementType;
        }

        public String getCaption() {
            return caption;
        }

        public Element setCaption(String caption) {
            this.caption = caption;
            return this;
        }


        public String getView() {
            return view;
        }

        public Element setView(String view) {
            this.view = view;
            return this;
        }


        public String getQueryFilter() {
            return queryFilter;
        }

        public Element setQueryFilter(String queryFilter) {
            this.queryFilter = queryFilter;
            return this;
        }

        public String getDatasourceProperty() {
            return datasourceProperty;
        }

        public Element setDatasourceProperty(String datasourceProperty) {
            this.datasourceProperty = datasourceProperty;
            return this;
        }

        public Map<Object, String> getOptionsMap() {
            return optionsMap;
        }

        public Element setOptionsMap(Map<Object, String> optionsMap) {
            this.optionsMap = optionsMap;
            return this;
        }

        public Object[] getSelectedValues() {
            return selectedValues;
        }

        public Element setSelectedValues(Object... selectedValues) {
            this.selectedValues = selectedValues;
            return this;
        }

        public boolean isSelected() {
            return selected;
        }

        public Element setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public boolean isShowStatistics() {
            return showStatistics;
        }

        public Element setShowStatistics(boolean showStatistics) {
            this.showStatistics = showStatistics;
            return this;
        }

        public Double getMinValue() {
            return minValue;
        }

        public Element setMinValue(Double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Double getMaxValue() {
            return maxValue;
        }

        public Element setMaxValue(Double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Long getFetchCount() {
            return fetchCount;
        }

        public Element setFetchCount(Long fetchCount) {
            this.fetchCount = fetchCount;
            return this;
        }
    }

    public enum ElementType {
        TEXT, DATE, NUMBER, SELECT, CHECKBOX, LOOKUP, CHECKBOXCITY
    }

    public static class ElementOption {
        private boolean fetched;
        private boolean selected;
        private String name;
        private Object value;
        private Long count;

        public boolean isFetched() {
            return fetched;
        }

        public void setFetched(boolean fetched) {
            this.fetched = fetched;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public String toString() {
            return "{name : " + name + ", selected: " + selected + "}";
        }
    }

    private enum Operation {
        LIKE(" like lower(concat('%%', concat(:component$%s, '%%')))"),
        LIKE_BY_STRING(" like lower(concat('%%', concat('%s', '%%')))"),
        BETWEEN("between :component$%s and :component$%s"),
        GREATER_OR_EQUAL(" >= :component$%s"),
        LESS_OR_EQUAL(" <= :component$%s"),
        IN(" in :component$%s");
        String jpqlString;

        Operation(String jpqlString) {
            this.jpqlString = jpqlString;
        }

        public String getJpqlString() {
            return this.jpqlString;
        }

        public String formatJpqlString(String... values) {
            return String.format(this.getJpqlString(), (Object) values);
        }
    }
}
