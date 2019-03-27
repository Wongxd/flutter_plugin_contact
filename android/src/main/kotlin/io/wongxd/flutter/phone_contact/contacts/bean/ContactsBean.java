package io.wongxd.flutter.phone_contact.contacts.bean;

import java.util.List;

/**
 * Created by wongxd on 2018/06/06.
 * https://github.com/wongxd
 * wxd1@live.com
 */
public class ContactsBean {
    private String letter = "";
    private String lookup_key = "";
    private int contact_id = 0;
    private String name = "";
    private String location = "";
    private List<String> phones;

    public String getLookup_key() {
        return lookup_key;
    }

    public void setLookup_key(String lookup_key) {
        this.lookup_key = lookup_key;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
