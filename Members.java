package org.example.study.domain;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.study.domain.common.BaseEntity;
import org.example.study.domain.enums.memberStatus;
import org.example.study.domain.mapping.Reviews;
import org.example.study.domain.mapping.Usermissions;
import org.example.study.domain.mapping.locationbonusmission;

import java.util.ArrayList;
import java.util.List;

@Enabled
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Entity
public class Members extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String foodprefer;

    @Column(nullable = false, length = 40)
    private String address;

    private Integer birth;

    @Column(nullable = false, length = 20)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private memberStatus status;

    @OneToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private List<Reviews> memberRevewList = new ArrayList<>();

    @OneToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private List<Usermissions> usermissionsList = new ArrayList<>();

    @OneToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private List<locationbonusmission> locationbonusmissions = new ArrayList<>();




}
