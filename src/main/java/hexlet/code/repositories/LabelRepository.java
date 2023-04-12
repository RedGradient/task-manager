package hexlet.code.repositories;

import hexlet.code.models.Label;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);
}
