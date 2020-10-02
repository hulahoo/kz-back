package kz.uco.tsadv.modules.performance.dto;

import java.io.Serializable;

/**
 * @author Adilbekov Yernar
 */
public class BoardChangedItem implements Serializable {

    private String id;
    private int from;
    private int to;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "BoardChangedItem{" +
                "id='" + id + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
