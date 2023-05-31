package hexlet.code.service;


import com.querydsl.core.types.Predicate;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusService statusService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelService labelService;
    @Autowired
    private LabelRepository labelRepository;

    public Long getTasksCount() {
        return taskRepository.count();
    }

    public Iterable<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Iterable<Task> getTasks(Predicate predicate) {
        if (predicate == null) {
            return taskRepository.findAll();
        }
        return taskRepository.findAll(predicate);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
    }

    public Task createTask(TaskDto taskDto) {
        var task = new Task();
        fillTaskFromDto(task, taskDto);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskDto taskDto) {
        var task = taskRepository.findById(id).orElseThrow();
        fillTaskFromDto(task, taskDto);
        return taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    public void fillTaskFromDto(Task task, TaskDto taskDto) {
        var labels = new HashSet<Label>();
        for (var id : taskDto.getLabelIds()) {
            labels.add(labelService.getLabelById(id));
        }

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(statusService.getStatusById(taskDto.getTaskStatusId()));
        task.setLabels(labels);
        task.setAuthor(userService.getCurrentUser());
        Long executorId = taskDto.getExecutorId();
        if (executorId != null) {
            task.setExecutor(userService.getUserById(executorId));
        }
    }
}
