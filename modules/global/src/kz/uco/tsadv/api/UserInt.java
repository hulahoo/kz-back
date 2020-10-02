package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

/**
 * Пользователь (включая лицо и все сведения)(Интеграционный объект)
 */
@MetaClass(name = "tsadv$UserInt")
public class UserInt extends AbstractEntityInt {
    private static final long serialVersionUID = -352119408910872818L;

    /**
     * Логин
     */
    @MetaProperty
    protected String login;

    /**
     * Язык интерфейса
     */
    @MetaProperty
    protected String language;

    /**
     * Пароль
     */
    @MetaProperty
    protected String password;

    /**
     * Email адрес
     */
    @MetaProperty
    protected String email;

    /**
     * Номер телефона
     */
    @MetaProperty
    protected String phoneNumber;

    /**
     * Персональные данные
     */
    @MetaProperty
    protected PersonInt person;


    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }


    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPerson(PersonInt person) {
        this.person = person;
    }

    public PersonInt getPerson() {
        return person;
    }


}