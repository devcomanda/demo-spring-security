package com.devcomanda.demospringsecurity.model;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class DemoModel {

    private int id;
    private String name;
    private String code;

    public DemoModel(
            int id,
            String name,
            String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
