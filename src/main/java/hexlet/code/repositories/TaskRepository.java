package hexlet.code.repositories;

//import com.querydsl.core.types.dsl.SimpleExpression;
//import com.querydsl.core.types.dsl.StringExpression;
import hexlet.code.models.Task;
//import hexlet.code.models.QTask;

//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
//import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends
        CrudRepository<Task, Long> {
//        QuerydslPredicateExecutor<Task> {
//        QuerydslBinderCustomizer<QTask> {

//    @Override
//    default void customize(QuerydslBindings bindings, QTask task) {
//        bindings.bind(task.taskStatus).first(StringExpression::containsIgnoreCase);
//        bindings.bind(task.executorId).first(StringExpression::eq);
//        bindings.bind(task.labels).first(SimpleExpression::eq);
//        bindings.bind(task.authorId).first(StringExpression::containsIgnoreCase);
//    }

}
