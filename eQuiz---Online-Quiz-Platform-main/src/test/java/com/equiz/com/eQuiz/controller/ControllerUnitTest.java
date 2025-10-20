package com.equiz.com.eQuiz.controller;

import com.equiz.com.eQuiz.MainController.Controller;
import com.equiz.com.eQuiz.model.QuestionForm;
import com.equiz.com.eQuiz.model.Result;
import com.equiz.com.eQuiz.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerUnitTest {

    @InjectMocks
    private Controller controller; // your controller class

    @Mock
    private QuizService qService;

    @Mock
    private Result result; // the autowired result bean in controller

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void postQuiz_missingUsername_redirectsWithWarning() throws Exception {
        mockMvc.perform(post("/quiz").param("username", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void postQuiz_withUsername_returnsQuizViewAndModel() throws Exception {
        QuestionForm qf = new QuestionForm();
        qf.setQuestions(new ArrayList<>());

        when(qService.getQuestions()).thenReturn(qf);

        mockMvc.perform(post("/quiz").param("username", "bob"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz.html"))
                .andExpect(model().attributeExists("qForm"));

        verify(qService).getQuestions();
        verify(result).setUsername("bob");
    }

    @Test
    void getScore_returnsScoreboardView_withModel() throws Exception {
        List<Result> scores = new ArrayList<>();
        when(qService.getTopScore()).thenReturn(scores);

        mockMvc.perform(get("/score"))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard.html"))
                .andExpect(model().attributeExists("sList"));

        verify(qService).getTopScore();
    }
}
