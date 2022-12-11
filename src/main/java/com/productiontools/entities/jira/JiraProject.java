package com.productiontools.entities.jira;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jiraProjects")
public class JiraProject {

	@Id
	@Column(name = "_id")
	private int id;

	@Column(name = "_key")
	private String key;

	@Column(name = "_name")
	private String name;
}
