package com.lior.application.rh_test.service;

import com.lior.application.rh_test.dto.CommentDTO;
import com.lior.application.rh_test.dto.NewsDTO;
import com.lior.application.rh_test.model.News;
import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.repos.CommentsRepository;
import com.lior.application.rh_test.repos.NewsRepository;
import com.lior.application.rh_test.services.NewsService;
import com.lior.application.rh_test.services.UsersService;
import com.lior.application.rh_test.util.CurrentUser;
import com.lior.application.rh_test.util.Roles;
import org.checkerframework.checker.units.qual.N;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import com.lior.application.rh_test.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@AutoConfigureMockMvc(addFilters = false)
@RunWith(MockitoJUnitRunner.class)
public class NewsServiceTest {
    @Mock
    NewsRepository newsRepository;
    @Mock
    CommentsRepository commentsRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    NewsService newsService;


    @Test
    public void whenFindOneShouldReturnNewsDTOWithComments() {
        final int id = 1;
        final int page = 2;
        final int cPP = 10;
        NewsDTO dto = new NewsDTO();
        dto.setId(1);

        when(modelMapper.map(any(), eq(NewsDTO.class))).thenReturn(dto);
        when(commentsRepository.findCommentsByCommentedNews_Id(eq(id), eq(PageRequest.of(page-1,cPP))))
                .thenReturn(new PageImpl<>(List.of(Comment.builder().id(1).build())));
        when(newsRepository.findById(id)).thenReturn(Optional.of(News.builder().id(1).build()));
        assertEquals(1, newsService.findOne(id, page, cPP).getComments().getTotalElements());
        assertEquals(1, newsService.findOne(id, page, cPP).getId());

        verify(commentsRepository, times(2)).findCommentsByCommentedNews_Id(eq(id),
                eq(PageRequest.of(page - 1, cPP)));
        verify(newsRepository, times(2)).findById(1);

    }


    @Test
    public void whenSearchShouldReturnPageOfNewsDTO() {
        final String query = "T";
        final int page = 2;
        final int nPP = 10;
        NewsDTO dto = new NewsDTO();
        dto.setId(1);

        when(modelMapper.map(any(), eq(NewsDTO.class))).thenReturn(dto);
        when(newsRepository.findByTextOrTitle(eq(query), eq(PageRequest.of(page-1, nPP))))
                .thenReturn(new PageImpl<>(List.of(News.builder().id(1).text("Text").build(),
                        News.builder().id(2).title("Title").build())));

        assertEquals(2, newsService.search(query, page, nPP).getTotalElements());


        verify(newsRepository, times(1)).findByTextOrTitle(query, PageRequest.of(page-1, nPP));

    }
    @Test
    public void whenSaveShouldMapAndSave() {
        try (MockedStatic<CurrentUser> util = mockStatic(CurrentUser.class)) {
            NewsDTO dto = new NewsDTO();
            dto.setId(1);
            dto.setTitle("Title");
            dto.setText("Text");
            util.when(CurrentUser::get).thenReturn(User.builder().username("Admin").role(Roles.ROLE_ADMIN).build());
            when(modelMapper.map(dto, News.class)).thenReturn(News.builder().build());

            newsService.save(dto);

            verify(modelMapper, times(1)).map(any(), eq(News.class));
            verify(newsRepository, times(1)).save(any(News.class));
        }
    }

    @Test
    public void whenUpdateShouldMapAndSave() {
        try (MockedStatic<CurrentUser> util = mockStatic(CurrentUser.class)) {
            util.when(CurrentUser::get).thenReturn(User.builder().username("Admin").role(Roles.ROLE_ADMIN).build());
            NewsDTO dto = new NewsDTO();
            dto.setId(1);
            dto.setTitle("Title");
            dto.setText("Text");
            when(modelMapper.map(dto, News.class)).thenReturn(News.builder().build());
            newsService.update(1, dto);
            verify(modelMapper, times(1)).map(any(), eq(News.class));
            verify(newsRepository, times(1)).save(any(News.class));
        }
    }

    @Test
    public void whenDeleteShouldDelete() {
        newsService.delete(1);
        verify(newsRepository, times(1)).deleteById(eq(1));
    }
}
