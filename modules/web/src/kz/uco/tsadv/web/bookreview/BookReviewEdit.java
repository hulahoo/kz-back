package kz.uco.tsadv.web.bookreview;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.BookReview;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.inject.Inject;
import java.math.BigDecimal;

public class BookReviewEdit extends AbstractEditor<BookReview> {

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Datasource<BookReview> bookReviewDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected HBoxLayout wrapRatingStar;


    @Override
    protected void postInit() {
        super.postInit();

        BookReview review = bookReviewDs.getItem();

        Label label = componentsFactory.createComponent(Label.class);
        label.setStyleName("book-name");
        label.setAlignment(Alignment.TOP_LEFT);
        label.setValue("Оцените книгу");

        WebRateStars ratingStars = componentsFactory.createComponent(WebRateStars.class);
        ratingStars.setHeight("50px");
        ratingStars.setAlignment(Alignment.TOP_RIGHT);
        RateStarsComponent rateStarsComponent = (RateStarsComponent) ratingStars.getComponent();
        rateStarsComponent.setListener(value1 -> {
            int roundValue = (int) Math.round(value1);
            rateStarsComponent.setValue(roundValue);
            review.setRating(BigDecimal.valueOf(roundValue));
        });
        wrapRatingStar.add(label);
        wrapRatingStar.add(ratingStars);
    }
}