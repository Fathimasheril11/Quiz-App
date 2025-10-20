package com.equiz.com.eQuiz.repository;


import com.equiz.com.eQuiz.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.equiz.com.eQuiz.model")
@EnableJpaRepositories(basePackages = "com.equiz.com.eQuiz.repository")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class QuestionRepoTest {

    @Autowired
    private QuestionRepo questionRepo;

    @Test
    @DisplayName("Save and find Question")
    void saveAndFind() {
        Question q = new Question();
        q.setTitle("What is Java?");
        q.setOptionA("Programming Language");
        q.setOptionB("Coffee");
        q.setOptionC("Planet");
        q.setAns(1);
        q.setChose(0);

        Question saved = questionRepo.save(q);
        assertThat(saved.getQuesId()).isNotNull();

        Optional<Question> fetched = questionRepo.findById(saved.getQuesId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getOptionA()).isEqualTo("Programming Language");
    }
}