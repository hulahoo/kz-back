package kz.uco.tsadv.lms.pojo;


import javax.annotation.Nullable;
import java.io.Serializable;

public class ResponsePojo implements Serializable {
    Response status;
    String message;

    public Response getStatus() {
        return status;
    }

    public void setStatus(Response status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum Response {
        SUCCESS("SUCCESS"),
        ERROR("ERROR");

        private String id;

        Response(String value) {
            this.id = value;
        }

        public String getId() {
            return id;
        }

        @Nullable
        public static Response fromId(String id) {
            for (Response at : Response.values()) {
                if (at.getId().equals(id)) {
                    return at;
                }
            }
            return null;
        }
    }
}
