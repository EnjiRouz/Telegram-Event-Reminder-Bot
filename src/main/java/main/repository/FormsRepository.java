package main.repository;

import main.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormsRepository extends JpaRepository<Form, Long> {
}
