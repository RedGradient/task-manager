package hexlet.code.repositories;

import hexlet.code.models.TaskStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {
    Iterable<TaskStatus> findAll();

    Optional<TaskStatus> findByName(String name);
}
