package com.app.playerservicejava.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Players implements Serializable {
    private List<Player> players;

    public Players() {
        this.players = new ArrayList<>();
    }
}
