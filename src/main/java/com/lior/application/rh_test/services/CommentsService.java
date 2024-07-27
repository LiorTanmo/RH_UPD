package com.lior.application.rh_test.services;

import com.lior.application.rh_test.aspect.util.DataTypes;
import com.lior.application.rh_test.aspect.util.OwnershipFilter;
import com.lior.application.rh_test.dto.CommentDTO;
import com.lior.application.rh_test.dto.UserDTO;
import com.lior.application.rh_test.model.Comment;
import com.lior.application.rh_test.repos.CommentsRepository;
import com.lior.application.rh_test.repos.NewsRepository;
import com.lior.application.rh_test.util.CurrentUser;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final NewsRepository newsRepository;
    private final ModelMapper modelMapper;

    /**
     * Gets all comments related to news with given ID
     * @param news_id ID of news
     * @param page Page number of list of comments (starting from 1)
     * @param commentsPerPage Number of comments per page
     * @return Page with comments
     */
    public Page<CommentDTO> findAll(int news_id, int page, int commentsPerPage){
        return commentsRepository.findCommentsByCommentedNews_Id
                (news_id, PageRequest.of(page-1, commentsPerPage))
                .map(this::toDTO);
    }

    /**
     * Deletes comment with given id
     * @param id id of the comment to be deleted
     */
    @OwnershipFilter(datatype = DataTypes.Comment)
    public void removeComment(int id){
        commentsRepository.deleteById(id);
    }

    /**
     * Updates comment with given ID
     * @param id ID of the comment to be updated
     * @param updCom New comment information
     */
    @OwnershipFilter(datatype = DataTypes.Comment)
    public void editComment(int id, CommentDTO updCom) {
        updCom.setId(id);
        commentsRepository.save(toComment(updCom));
    }

    /**
     * Creates comment attached to news with given ID
     * @param commentDTO Body of the comment
     * @param news_id ID of news to be commented
     */
    public void addComment (CommentDTO commentDTO, int news_id){
        Comment comment = toComment(commentDTO);
        comment.setCommentedNews(newsRepository.findById(news_id).orElseThrow(()->
                new NotFoundException("News you're trying to comment not found")));
        comment.setInserted_by(CurrentUser.get());
        commentsRepository.save(comment);
    }

    private CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setInserted_by(modelMapper.map(comment.getInserted_by(), UserDTO.class));
        return commentDTO;
    }

    private Comment toComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

}
