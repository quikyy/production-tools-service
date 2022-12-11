package com.productiontools.entities.jira;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "jiraUser")
public class JiraUser {

	@Id
	@Column
	private String accountId;

	@Column
	private String displayName;

	@Column
	private int personId;

	@JsonManagedReference
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {CascadeType.REMOVE })
	@JoinTable(
			name = "jiraUser_jiraGroup",
			joinColumns = { @JoinColumn(name = "fk_jiraUser") },
			inverseJoinColumns = { @JoinColumn(name = "fk_jiraGroup") })
	private List<JiraGroup> jiraGroups = new ArrayList<>();

	@JsonManagedReference
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = { CascadeType.ALL })
	@JoinTable(
			name = "jiraUser_jiraSharedTeams",
			joinColumns = { @JoinColumn(name = "fk_jiraUser") },
			inverseJoinColumns = { @JoinColumn(name = "fk_jiraSharedTeams") })
	private List<JiraSharedTeam> jiraSharedTeams = new ArrayList<>();
}
