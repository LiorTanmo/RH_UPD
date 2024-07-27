package com.lior.application.rh_test.repos;

import com.lior.application.rh_test.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
    @Query(nativeQuery = true, value = "select n.id, n.creation_date," +
            "n.inserted_by_id, n.updated_by_id, n.last_edit_date, n.text," +
            "n.title from news n where upper(n.text) " +
            "like concat('%', upper(?1), '%') or upper(n.title) like concat('%', upper(?1),'%')")
    Page<News> findByTextOrTitle(String s, Pageable pageable);

}
