package com.equiz.com.eQuiz.repository;


import com.equiz.com.eQuiz.model.Result;
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
public class ResultRepoTest {

    @Autowired
    private ResultRepo resultRepo;

    @Test
    @DisplayName("Test saving a Result entity")
    void saveResult_shouldPersistAndReturnSavedEntity() {
        Result result = new Result();
        result.setUsername("John");
        result.setTotalCorrect(4);

        Result saved = resultRepo.save(result);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("John");
        assertThat(saved.getTotalCorrect()).isEqualTo(4);
    }

    @Test
    @DisplayName("Test findById returns correct Result")
    void findById_shouldReturnResult() {
        Result result = new Result();
        result.setUsername("Alice");
        result.setTotalCorrect(3);
        resultRepo.save(result);

        Optional<Result> found = resultRepo.findById(result.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Test findAll returns list of results")
    void findAll_shouldReturnList() {
        Result r1 = new Result();
        r1.setUsername("User1");
        r1.setTotalCorrect(2);

        Result r2 = new Result();
        r2.setUsername("User2");
        r2.setTotalCorrect(5);

        resultRepo.save(r1);
        resultRepo.save(r2);

        List<Result> results = resultRepo.findAll();

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(Result::getUsername)
                .containsExactlyInAnyOrder("User1", "User2");
    }

    @Test
    @DisplayName("Test delete result by ID")
    void deleteById_shouldRemoveResult() {
        Result result = new Result();
        result.setUsername("ToDelete");
        result.setTotalCorrect(1);
        Result saved = resultRepo.save(result);

        resultRepo.deleteById(saved.getId());

        Optional<Result> deleted = resultRepo.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
