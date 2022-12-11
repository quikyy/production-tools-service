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
@Table(name = "jiraGroup")
public class JiraGroup {

    @Id
    @Column
    private String groupId;

    @Column(name = "_name")
    private String name;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "jiraGroups")
    private List<JiraUser> userList = new ArrayList<>();
}
