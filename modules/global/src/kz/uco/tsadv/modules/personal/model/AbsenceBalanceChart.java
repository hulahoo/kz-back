package kz.uco.tsadv.modules.personal.model;

import java.io.Serializable;

/**
 * @author Adilbekov Yernar
 */
public class AbsenceBalanceChart implements Serializable {

    private int balanceDays;
    private int daysSpent;
    private int daysLeft;

    public int getBalanceDays() {
        return balanceDays;
    }

    public void setBalanceDays(int balanceDays) {
        this.balanceDays = balanceDays;
    }

    public int getDaysSpent() {
        return daysSpent;
    }

    public void setDaysSpent(int daysSpent) {
        this.daysSpent = daysSpent;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }
}
