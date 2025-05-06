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
public class Missions extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 40)
    private String missionname;

    private Integer missionpoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store stores;


}
