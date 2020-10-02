package kz.uco.tsadv.modules.recognition.pojo;

import java.io.Serializable;

/**
 * @author adilbekov.yernar
 */
public class HeartCoinPojo implements Serializable {

    private String personGroupId;
    private long coins;
    private String comment;

    public HeartCoinPojo(String personGroupId, long coins, String comment) {
        this.personGroupId = personGroupId;
        this.coins = coins;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }
}
