package com.lior.application.rh_test.model;

import com.lior.application.rh_test.util.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;




@Data
@Entity
@Builder
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @NotEmpty
    @Size(max = 40, message = "No more than 40 characters")
    private String username;

    @Column
    @NotEmpty
    @Size(max = 80, message = "No more than 80 characters")
    private String password;

    @Column
    @Size(max = 20, message = "No more than 20 characters")
    private String name;

    @Column
    @Size(max = 20, message = "No more than 20 characters")
    private String surname;

    @Column(name = "parent_name")
    @Size(max = 20, message = "No more than 20 characters")
    private String parentName;

    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.DATE)
    private Date creation_date;

    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.DATE)
    private Date last_edit_date;

    @Enumerated(EnumType.STRING)
    @Column
    private Roles role;
}
