package com.kesari.tkfops.Route;

/**
 * Created by kesari on 28/08/17.
 */

public class BikerSocketDataPojo {

    private String __v;
    private String biker_id;
    private String _id;
    private String created_at;
    private Geo geo;

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getBiker_id() {
        return biker_id;
    }

    public void setBiker_id(String biker_id) {
        this.biker_id = biker_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }
}
