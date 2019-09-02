package com.nevs.car.jnihelp.modle;

import java.io.Serializable;

/**
 * Created by mac on 2018/8/13.
 */

public class DigitalKeyBean implements Serializable{
   private String bookingcert;

    public DigitalKeyBean(String bookingcert) {
        this.bookingcert = bookingcert;
    }

    public String getBookingcert() {
        return bookingcert;
    }

    public void setBookingcert(String bookingcert) {
        this.bookingcert = bookingcert;
    }
}
