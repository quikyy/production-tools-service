package com.productiontools.entities.jira;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "jiraVersion" )
public class JiraVersion {

	@Id
	@Column
	private String id;

	@Column
	private String name;

	@Column
	private String projectKey;

}
