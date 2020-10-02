package kz.uco.tsadv.modules.personal.dto;

import java.io.Serializable;

public class LinkedinProfileDTO implements Serializable {
    byte[] photo;

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
