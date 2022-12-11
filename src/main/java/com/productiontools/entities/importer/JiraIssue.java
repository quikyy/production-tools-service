package com.productiontools.entities.importer;

import com.productiontools.entities.jira.*;
import com.productiontools.enums.Status;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JiraIssue {

    JiraProject project;
    Status status;
    String typeId;
    String summary;
    JiraUser assignee;
    JiraUser approver;
    JiraSharedTeam sharedTeam;
    Optional<JiraComponent> component;
    Optional<JiraVersion> version;

}
