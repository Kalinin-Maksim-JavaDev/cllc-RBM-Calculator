package ru.vtb.cllc.remote_banking_calculating.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Record {
    @JsonProperty("Date")
    public String dateAsString;
    @JsonProperty("Hour")
    public int hour;
    @JsonProperty("Id_user")
    public int id_user;
    @JsonProperty("Id_line")
    public int id_line;
    @JsonProperty("Id_region")
    public int id_region;
    @JsonProperty("Id_tree_division")
    public int id_tree_division;
    @JsonProperty("N_inb")
    public int n_inb;
    @JsonProperty("N_out")
    public int n_out;
    @JsonProperty("N_hold")
    public int n_hold;
    @JsonProperty("N_abandon")
    public int n_abandon;
    @JsonProperty("N_transfer")
    public int n_transfer;
    @JsonProperty("T_inb")
    public int t_inb;
    @JsonProperty("T_out")
    public int t_out;
    @JsonProperty("T_hold")
    public int t_hold;
    @JsonProperty("T_ring")
    public int t_ring;
    @JsonProperty("T_acw")
    public int t_acw;
    @JsonProperty("T_wait")
    public int t_wait;

    private int month;
    private long epochDay;


    public void setDateAsString(String dateAsString) {
        this.dateAsString = dateAsString;
        LocalDate date = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_DATE);
        month = date.getMonthValue();
        epochDay = date.toEpochDay();
    }

    public long getEpochDay() {
        return epochDay;
    }

    public int getId_user() {
        return id_user;
    }

    public int getId_line() {
        return id_line;
    }

    public int getId_region() {
        return id_region;
    }

    public int getId_tree_division() {
        return id_tree_division;
    }

    public int getMonth() {
        return month;
    }
}
