package hexlet.code.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import hexlet.code.models.Task;
import hexlet.code.models.QTask;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;


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

}
