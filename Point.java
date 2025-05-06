package org.example.study.domain;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.study.domain.common.BaseEntity;

@Enabled
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Entity
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Integer missioncount;

    private Integer point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id")
    private Members members;


}
