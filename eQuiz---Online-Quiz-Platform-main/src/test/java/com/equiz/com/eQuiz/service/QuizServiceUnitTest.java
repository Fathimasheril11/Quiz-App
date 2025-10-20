package com.equiz.com.eQuiz.service;

import com.equiz.com.eQuiz.model.Question;
import com.equiz.com.eQuiz.model.QuestionForm;
import com.equiz.com.eQuiz.model.Result;
import com.equiz.com.eQuiz.repository.QuestionRepo;
import com.equiz.com.eQuiz.repository.ResultRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class QuizServiceUnitTest {

    @InjectMocks
    private QuizService quizService;

    @Mock
    private QuestionRepo qRepo;

    @Mock
    private ResultRepo rRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQuestions_returnsFiveDistinctQuestions_whenRepoHasMany() {
        // prepare repository data
        List<Question> all = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Question q = new Question(i, "title" + i, "A", "B", "C", 1, 0);
            all.add(q);
        }
        when(qRepo.findAll()).thenReturn(new ArrayList<>(all));

        QuestionForm qForm = quizService.getQuestions();

        assertThat(qForm).isNotNull();
        assertThat(qForm.getQuestions()).hasSize(5);
        // ensure each returned question originated from the repo list
        assertThat(all).containsAll(qForm.getQuestions());
        // ensure there are no duplicates in chosen questions
        long uniqueCount = qForm.getQuestions().stream().map(Question::getQuesId).distinct().count();
        assertThat(uniqueCount).isEqualTo(5);
    }

    @Test
    void getResult_countsCorrectAnswers() {
        Question q1 = new Question(1, "t1", "A","B","C", 2, 2);
        Question q2 = new Question(2, "t2", "A","B","C", 1, 3);
        Question q3 = new Question(3, "t3", "A","B","C", 3, 3);

        QuestionForm qForm = new QuestionForm();
        List<Question> list = new ArrayList<>();
        list.add(q1); list.add(q2); list.add(q3);
        qForm.setQuestions(list);

        int correct = quizService.getResult(qForm);
        assertThat(correct).isEqualTo(2); // q1 and q3 are correct
    }

    @Test
    void saveScore_callsRepoSave_withMappedResult() {
        Result in = new Result();
        in.setUsername("alice");
        in.setTotalCorrect(3);

        // spy or let repo be mocked
        doAnswer(invocation -> {
            Result saved = invocation.getArgument(0);
            saved.setId(99); // simulate DB id assigned
            return saved;
        }).when(rRepo).save(any(Result.class));

        quizService.saveScore(in);

        ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class);
        verify(rRepo).save(captor.capture());
        Result saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getTotalCorrect()).isEqualTo(3);
    }

    @Test
    void getTopScore_returnsSortedList_fromRepo() {
        List<Result> results = new ArrayList<>();
        results.add(new Result(1, "u1", 2));
        results.add(new Result(2, "u2", 5));
        when(rRepo.findAll(Sort.by(Sort.Direction.DESC, "totalCorrect"))).thenReturn(results);

        List<Result> got = quizService.getTopScore();

        assertThat(got).isSameAs(results);
        verify(rRepo).findAll(Sort.by(Sort.Direction.DESC, "totalCorrect"));
    }
}
