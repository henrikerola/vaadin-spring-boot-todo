package demo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Henri Kerola / Vaadin
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
