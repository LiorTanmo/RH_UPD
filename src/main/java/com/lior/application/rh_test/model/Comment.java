


package com.lior.application.rh_test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Data
@Entity
@Builder
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Size(max = 300,message = "Up to 300 characters")
    private String text;

    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.DATE)
    private Date creation_date;

    @ManyToOne
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id", updatable = false)
    private User inserted_by;

    @ManyToOne
    @JoinColumn(name = "id_news", referencedColumnName = "id", updatable = false)
    private News commentedNews;

}
