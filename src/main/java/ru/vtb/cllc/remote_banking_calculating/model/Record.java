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
    public long n_inb;
    @JsonProperty("N_out")
    public long n_out;
    @JsonProperty("N_hold")
    public long n_hold;
    @JsonProperty("N_abandon")
    public long n_abandon;
    @JsonProperty("N_transfer")
    public long n_transfer;
    @JsonProperty("T_inb")
    public long t_inb;
    @JsonProperty("T_out")
    public long t_out;
    @JsonProperty("T_hold")
    public long t_hold;
    @JsonProperty("T_ring")
    public long t_ring;
    @JsonProperty("T_acw")
    public long t_acw;
    @JsonProperty("T_wait")
    public long t_wait;

    private int month;
    private long epochDay;

    @JsonProperty("Date")
    public void setDate(String dateAsString) {
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

    public static Record sum(Record r1, Record r2) {
        r1.n_inb += r1.n_inb;
        return r1;
    }
}
