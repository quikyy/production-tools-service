package com.productiontools.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${auth.username}")
    private String username;
    @Value("${auth.password}")
    private String password;

    @Bean
    public PasswordEncoder getBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails developer = User.withUsername(username)
                .password(getBcryptPasswordEncoder().encode(password))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(developer);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/jira/api/feature/updateJiraDatabase").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/removeJiraDatabase").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraGroupsDatabase").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/getGroups").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraSharedTeams").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/getSharedTeams").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraUsersDatabase").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/getUsers").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraComponents").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraProjectsDatabase").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/getProjects").hasRole("ADMIN")

                .antMatchers("/jira/api/feature/updateJiraVersionsDatabase").hasRole("ADMIN")
                .antMatchers("/jira/api/feature/getVersions").hasRole("ADMIN")

                .antMatchers("/budget/api/updateDatabase").hasRole("ADMIN")
                .antMatchers("/budget/api/getRates").hasRole("ADMIN")
                .and()
                .httpBasic()
                .and()
                .build();
    }
}
