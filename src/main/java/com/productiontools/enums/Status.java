package com.productiontools.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum Status {
    IN_PROGRESS("In Progress", "3"),
    BACKLOG("Backlog", "10000"),
    DONE("Done", "10002"),
    TO_DO("To Do", "10006"),
    CANCELLED("Cancelled", "10076"),
    REVIEW("Review", "10077"),
    BLOCKED("Blocked!", "10078"),
    QA_VERIFICATION("QA Verification", "10079"),
    TO_INTEGRATE("To Integrate", "10080");

    private final String name;
    private final String id;

    Status(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public static Status getStatus(String name){
        for(Status s: Status.values()){
            if(s.name.equalsIgnoreCase(name)){
                return s;
            }
        }
        throw new IllegalArgumentException("No status with name: " + name);
    }

}
