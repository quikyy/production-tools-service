package com.productiontools.entities.jira;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraSharedTeam {

    @Id
    private int id;

    @Column
    private String name;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "jiraSharedTeams")
    private List<JiraUser> userList = new ArrayList<>();
}
