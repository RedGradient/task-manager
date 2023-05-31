package hexlet.code.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import hexlet.code.model.Task;
import hexlet.code.model.QTask;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends
        CrudRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {

    @Override
    default void customize(QuerydslBindings bindings, QTask task) {
        bindings.bind(task.author).first(SimpleExpression::eq);
        bindings.bind(task.executor).first(SimpleExpression::eq);
        bindings.bind(task.taskStatus).first(SimpleExpression::eq);


        bindings.bind(task.labels).first((path, value) -> {
            BooleanBuilder predicate = new BooleanBuilder();
            value.forEach(label -> predicate.and(path.any().eq(label)));
            return predicate;
        });

    }

    List<Task> findAllByTaskStatusId(Long id);

}
