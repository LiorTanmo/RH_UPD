package com.lior.application.rh_test.services;

import com.lior.application.rh_test.aspect.util.DataTypes;
import com.lior.application.rh_test.aspect.util.OwnershipFilter;
import com.lior.application.rh_test.dto.CommentDTO;
import com.lior.application.rh_test.dto.NewsDTO;
import com.lior.application.rh_test.model.News;
import com.lior.application.rh_test.repos.CommentsRepository;
import com.lior.application.rh_test.repos.NewsRepository;
import com.lior.application.rh_test.util.CurrentUser;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final CommentsRepository commentsRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetches news from database and loads its comments (as page)
     * @param id              ID of news to be fetched
     * @param page            Page of comments (starting from 1)
     * @param commentsPerPage Number of comments loaded per page
     * @return NewsDTo with comments
     */
    @Cacheable(value = "news", key = "#id")
    public NewsDTO findOne(int id, int page, int commentsPerPage) {
        return toDTO(newsRepository.findById(id).orElseThrow(NotFoundException::new), page-1, commentsPerPage);
    }

    /**
     * Looks for news with inputted string in its title or text
     *
     * @param s Query string
     * @return List of results
     */
    @Cacheable(value = "newsSearchResults", key = "#s")
    public Page<NewsDTO> search(String s, int page, int newsPerPage) {
        return newsRepository.findByTextOrTitle(s, PageRequest.of(page-1, newsPerPage)).map((this::toDTO));
    }

    /**
     * Gets all news as page
     * @param page Page number (starting with 1)
     * @param newsPerPage News displayed per page
     * @return Page of NewsDTO
     */
    public Page<NewsDTO> findAll(int page, int newsPerPage) {
        return newsRepository.findAll(PageRequest.of(page - 1, newsPerPage)).map(this::toDTO);
    }

    /**
     * Saves news in DB
     * @param newsDTO News to be saved
     */
    public void save(NewsDTO newsDTO) {
        News news = modelMapper.map(newsDTO, News.class);
        news.setInserted_by(CurrentUser.get());
        news.setUpdated_by(CurrentUser.get());
        newsRepository.save(news);
    }

    /**
     * Updates news with given ID
     * @param id ID of news to be updated
     * @param updNewsDTO new News info DTO
     */
    @OwnershipFilter(datatype = DataTypes.News)
    public void update(int id, NewsDTO updNewsDTO) {
        News updNews = modelMapper.map(updNewsDTO, News.class);
        updNews.setId(id);
        updNews.setUpdated_by(CurrentUser.get());
        newsRepository.save(updNews);
    }

    /**
     * Delete news by ID
     * @param id ID of news to be deleted
     */
    @OwnershipFilter(datatype = DataTypes.News)
    public void delete(int id) {
        newsRepository.deleteById(id);
    }

    //region Util
    private NewsDTO toDTO(News news) {
        return modelMapper.map(news, NewsDTO.class);
    }

    //Loads comments, and maps everything to their DTOs
    private NewsDTO toDTO(News news, int page, int commsPerPage) {
        NewsDTO newsDTO = modelMapper.map(news, NewsDTO.class);
        newsDTO.setComments(commentsRepository
                .findCommentsByCommentedNews_Id(news.getId(),
                        PageRequest.of(page, commsPerPage))
                .map((comm) -> modelMapper.map(comm, CommentDTO.class)));
        return newsDTO;
    }
    //endregion
}
