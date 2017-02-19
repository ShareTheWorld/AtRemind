package com.at.remind.db;

/**
 * Created by user on 16-12-19.
 */

public class AtRemind {
    public static final  String TABLE_NAME="at_remind";
    public static final String ID="id";
    public static final String TEXT="TEXt";
    public static final String TIME="time";
    private int id;
    private String text;
    private long time;

    public AtRemind(String text, long time) {
        this.text = text;
        this.time = time;
    }

    public AtRemind(int id, String text, long time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AtRemind{" +id +"," + text +"," + time +'}';
    }
}
