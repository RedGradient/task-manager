package hexlet.code.repositories;

import hexlet.code.models.Task;
import hexlet.code.models.TaskStatus;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long>  {

}
