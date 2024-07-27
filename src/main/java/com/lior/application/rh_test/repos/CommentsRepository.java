package com.lior.application.rh_test.repos;

import com.lior.application.rh_test.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findCommentsByCommentedNews_Id(Integer id, Pageable pageable);

//    @Modifying
//    @Query("DELETE FROM Comment c WHERE c.news_id = ?1")
    void deleteByCommentedNews_Id(Integer news_id);
}
