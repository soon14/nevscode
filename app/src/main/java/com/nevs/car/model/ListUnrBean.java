package com.nevs.car.model;

/**
 * Created by mac on 2018/7/19.
 */

public class ListUnrBean {
   private String un_id;
    private String is_read;
    private String is_delete;
    private String n_id;

    public ListUnrBean(String un_id, String is_read, String is_delete, String n_id) {
        this.un_id = un_id;
        this.is_read = is_read;
        this.is_delete = is_delete;
        this.n_id = n_id;
    }
}
