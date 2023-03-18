package hexlet.code.repositories;

import hexlet.code.models.Status;
import org.springframework.data.repository.CrudRepository;

public interface StatusRepository  extends CrudRepository<Status, Long> {
    Iterable<Status> findAll();
}
