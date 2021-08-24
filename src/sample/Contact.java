package sample;

import java.util.ArrayList;

public class Contact {

    private ArrayList<String> placeHolders = new ArrayList<>();
    private ArrayList<ArrayList<String>> contact = new ArrayList<>();

    public Contact(ArrayList<ArrayList<String>> contact, ArrayList<String> placeHolders) {
        this.contact = contact;
        this.placeHolders = placeHolders;
    }

    public Contact clon() {
        return new Contact(this.contact, this.placeHolders);
    }

    public ArrayList<String> getPlaceHolders() {
        return placeHolders;
    }

    public void setPlaceHolders(ArrayList<String> placeHolders) {
        this.placeHolders = placeHolders;
    }

    public ArrayList<ArrayList<String>> getContact() {
        return contact;
    }

    public void setContact(ArrayList<ArrayList<String>> contact) {
        this.contact = contact;
    }
}
