package org.example.study.domain;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.study.domain.common.BaseEntity;
import org.example.study.domain.mapping.Reviews;

import java.util.ArrayList;
import java.util.List;

@Enabled
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Entity
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 50)
    private String storename;

    @Column(nullable = false, length = 40)
    private String storelocation;

    @Column(nullable = false, length = 20)
    private String storetype;


    private Integer reviewcount;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Reviews> reviewsList = new ArrayList<>();



}
