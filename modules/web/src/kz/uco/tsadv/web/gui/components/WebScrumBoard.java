package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.ScrumBoard;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.web.toolkit.ui.scrumboardcomponent.ScrumBoardComponent;

public class WebScrumBoard extends WebAbstractComponent<ScrumBoardComponent> implements ScrumBoard {
    public WebScrumBoard() {
        this.component = new ScrumBoardComponent();
    }
}