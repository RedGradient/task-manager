package hexlet.code.repositories;

import hexlet.code.models.Label;
import org.springframework.data.repository.CrudRepository;

public interface LabelRepository extends CrudRepository<Label, Long> {
}
