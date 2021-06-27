package com.mmall.concurrency.diningphilosopher;

/**
 * @author renxiaoya
 * @date 2021-06-27
 **/
public enum StatusEnum {
    THINKING("Thinking"),
    EATING("Eating"),
    HUNGRY("Hungry"),
    ;
    private String name;

    StatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
