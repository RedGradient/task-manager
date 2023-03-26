package hexlet.code.repositories;

import hexlet.code.models.TaskStatus;
import org.springframework.data.repository.CrudRepository;

public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {
    Iterable<TaskStatus> findAll();
}
