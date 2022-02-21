package ru.vtb.cllc.remote_banking_calculating.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Record {

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
    private int epochDay;

    @JsonProperty("Date")
    public void setDate(String dateAsString) {
        LocalDate date = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_DATE);
        month = date.getMonthValue();
        epochDay = Math.toIntExact(date.toEpochDay());
    }

    public Integer getEpochDay() {
        return Math.toIntExact(epochDay);
    }

    public Integer getMonth() {
        return month;
    }

    public Record sum(Record other) {
        this.n_inb += other.n_inb;
        this.n_out += other.n_out;
        this.n_hold += other.n_hold;
        this.n_abandon += other.n_abandon;
        this.n_transfer += other.n_transfer;
        this.t_inb += other.t_inb;
        this.t_out += other.t_out;
        this.t_hold += other.t_hold;
        this.t_ring += other.t_ring;
        this.t_acw += other.t_acw;
        this.t_wait += other.t_wait;

        return this;
    }
}
