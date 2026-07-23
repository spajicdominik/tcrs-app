package com.tcrs_app.tcrs_app.entities;

import com.tcrs_app.tcrs_app.enums.MatchType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time_start", nullable = false)
    private OffsetDateTime timeStart;

    @Column(name = "time_end", nullable = false)
    private OffsetDateTime timeEnd;

    @Column(name = "canceled", nullable = false)
    private boolean canceled;

    @UpdateTimestamp
    @Column(name = "date_modified", nullable = false)
    private OffsetDateTime dateModified;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 20)
    private MatchType matchType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "main_player_id", nullable = false)
    private User mainPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;
}
