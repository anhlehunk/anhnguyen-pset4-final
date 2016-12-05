package com.example.anh.anhnguyen_pset4_final;

/**
 * Created by Anh on 5-12-2016.
 */

public class TodoItem {

    private int id;
    private String task;
    private long itemParent;
    private int check;
    public TodoItem(){}
    public TodoItem(String task, int checker, long parent){
        this.task = task;
        this.check = checker;
        this.itemParent = parent;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setTask(String task){
        this.task = task;
    }

    public void setParent(long parent){
        this.itemParent = parent;
    }

    // empty or checked box
    public void setChecker(int checker){
        this.check = checker;
    }

}