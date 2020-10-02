package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.ProgressBar;

import javax.inject.Inject;
import java.util.Map;

public class WaitCalculate extends AbstractWindow {

    @Inject
    private Label messageLabel;

    @Inject
    private ProgressBar progressBar;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        progressBar.setValue(0d);
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}