package com.lior.application.rh_test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
@Table
public class News {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Size(max = 150, message = "Up to 150 characters")
    private String title;

    @Column
    @Size(max = 2000,message = "Up to 2000 characters")
    private String text;

    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.DATE)
    private Date creation_date;

    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.DATE)
    private Date last_edit_date;

    @OneToMany(mappedBy = "commentedNews")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id", updatable = false)
    private User inserted_by;

    @ManyToOne
    @JoinColumn(name = "updated_by_id", referencedColumnName = "id")
    private User updated_by;
}
