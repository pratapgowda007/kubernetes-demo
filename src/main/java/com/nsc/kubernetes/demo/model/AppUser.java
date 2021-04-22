package com.nsc.kubernetes.demo.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppUser {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ROLE_LIST",
            joinColumns = {
                    @JoinColumn(name = "USER_ID")
            })
    @Column(name = "ROLE")
    private Set<String> roles = new HashSet<>();
}
