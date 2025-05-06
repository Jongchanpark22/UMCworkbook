package org.example.study.domain;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.study.domain.common.BaseEntity;
import org.example.study.domain.enums.alarmcheck;

@Enabled
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Entity
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, length = 200)
    private String body;

    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'UNCHECK'")
    @Enumerated(EnumType.STRING)
    private alarmcheck COMFIRM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id")
    private Members members;
}
