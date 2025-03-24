package com.example.aplicatie;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class Account {

    private String name;
    private Button accept;
    private Button decline;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account(String name, Button accept, Button decline) {
        this.name = name;
        this.accept = accept;
        this.decline = decline;
    }

    public Button getAccept() {
        return accept;
    }

    public void setAccept(Button accept) {
        this.accept = accept;
    }

    public Button getDecline() {
        return decline;
    }

    public void setDecline(Button decline) {
        this.decline = decline;
    }

    public Account(String name) {
        this.name = name;
    }
}
