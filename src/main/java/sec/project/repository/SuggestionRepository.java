package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.Suggestion;


public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    Suggestion findById(Long id);
}
