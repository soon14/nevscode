package com.nevs.car.model;

/**
 * Created by mac on 2018/5/3.
 */

public class ServiceEalution {
    private String u_code;
    private String se_score;
    private String se_evalution;
    private String ro_id;
    private String order_by;
    private String order_by_name;
    private String ro_no;

    public ServiceEalution(String u_code, String se_score, String se_evalution, String ro_id, String order_by, String order_by_name, String ro_no) {
        this.u_code = u_code;
        this.se_score = se_score;
        this.se_evalution = se_evalution;
        this.ro_id = ro_id;
        this.order_by = order_by;
        this.order_by_name = order_by_name;
        this.ro_no = ro_no;
    }

    public String getU_code() {
        return u_code;
    }

    public void setU_code(String u_code) {
        this.u_code = u_code;
    }

    public String getSe_score() {
        return se_score;
    }

    public void setSe_score(String se_score) {
        this.se_score = se_score;
    }

    public String getRo_id() {
        return ro_id;
    }

    public void setRo_id(String ro_id) {
        this.ro_id = ro_id;
    }

    public String getSe_evalution() {
        return se_evalution;
    }

    public void setSe_evalution(String se_evalution) {
        this.se_evalution = se_evalution;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getOrder_by_name() {
        return order_by_name;
    }

    public void setOrder_by_name(String order_by_name) {
        this.order_by_name = order_by_name;
    }

    public String getRo_no() {
        return ro_no;
    }

    public void setRo_no(String ro_no) {
        this.ro_no = ro_no;
    }
}
