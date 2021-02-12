package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class BellNotificationResponsePojo implements Serializable {
    private UUID id;
    private Date createTs;
    private String code;
    private String name;
    private String link;
    private String entityId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public static final class BellNotificationResponsePojoBuilder {
        private UUID id;
        private Date createTs;
        private String code;
        private String name;
        private String entityId;
        private String link;

        private BellNotificationResponsePojoBuilder() {
        }

        public static BellNotificationResponsePojoBuilder aBellNotificationResponsePojo() {
            return new BellNotificationResponsePojoBuilder();
        }

        public BellNotificationResponsePojoBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public BellNotificationResponsePojoBuilder createTs(Date createTs) {
            this.createTs = createTs;
            return this;
        }

        public BellNotificationResponsePojoBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BellNotificationResponsePojoBuilder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public BellNotificationResponsePojoBuilder link(String link) {
            this.link = link;
            return this;
        }

        public BellNotificationResponsePojoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BellNotificationResponsePojo build() {
            BellNotificationResponsePojo bellNotificationResponsePojo = new BellNotificationResponsePojo();
            bellNotificationResponsePojo.createTs = this.createTs;
            bellNotificationResponsePojo.code = this.code;
            bellNotificationResponsePojo.name = this.name;
            bellNotificationResponsePojo.id = this.id;
            bellNotificationResponsePojo.link = this.link;
            bellNotificationResponsePojo.entityId = this.entityId;
            return bellNotificationResponsePojo;
        }
    }
}
