package main.repository;

import main.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByName(String name);
}
