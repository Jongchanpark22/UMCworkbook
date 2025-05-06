package org.example.study.domain;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.study.domain.common.BaseEntity;


import java.util.ArrayList;
import java.util.List;

@Enabled
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Entity
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 40)
    private String locationname;

    private Integer storecount;


}
