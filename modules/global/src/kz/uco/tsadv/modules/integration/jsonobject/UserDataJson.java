package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDataJson implements Serializable {

    public ArrayList<UserJson> users;

    public ArrayList<UserJson> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserJson> users) {
        this.users = users;
    }
}
