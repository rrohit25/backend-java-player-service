package com.app.playerservicejava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="PLAYERS")
public class Player {

    @Id
    @Column(name = "PLAYERID")
    private String playerId;

    @Column(name = "BIRTHYEAR")
    private String birthYear;

    @Column(name = "BIRTHMONTH")
    private String birthMonth;

    @Column(name = "BIRTHDAY")
    private String birthDay;

    @Column(name = "BIRTHCOUNTRY")
    private String birthCountry;

    @Column(name = "BIRTHSTATE")
    private String birthState;

    @Column(name = "BIRTHCITY")
    private String birthCity;

    @Column(name = "DEATHYEAR")
    private String deathYear;

    @Column(name = "DEATHMONTH")
    private String deathMonth;

    @Column(name = "DEATHDAY")
    private String deathDay;

    @Column(name = "DEATHCOUNTRY")
    private String deathCountry;

    @Column(name = "DEATHSTATE")
    private String deathState;

    @Column(name = "DEATHCITY")
    private String deathCity;

    @Column(name = "NAMEFIRST")
    private String firstName;

    @Column(name = "NAMELAST")
    private String lastName;

    @Column(name = "NAMEGIVEN")
    private String givenName;

    @Column(name = "WEIGHT")
    private String weight;

    @Column(name = "HEIGHT")
    private String height;

    @Column(name = "BATS")
    private String bats;

    @Column(name = "THROWS")
    private String throwStats;

    @Column(name = "DEBUT")
    private String debut;

    @Column(name = "FINALGAME")
    private String finalGame;

    @Column(name = "RETROID")
    private String retroId;

    @Column(name = "BBREFID")
    private String bbrefId;

    public Player() {}
}
