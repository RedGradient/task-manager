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

    public Task createTask(TaskDto taskDto) {
        return taskRepository.save(fromDto(taskDto));
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
    }

    public Task udpateTask(Long id, TaskDto taskDto) {
        var task = fromDto(taskDto);
        task.setId(id);
        return taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    public Task fromDto(TaskDto taskDto) {
        var labels = new HashSet<Label>();
        taskDto.getLabelIds().forEach(
                (id) -> labels.add(labelService.getLabelById(id))
        );

        return Task.builder()
            .name(taskDto.getName())
            .description(taskDto.getDescription())
            .taskStatus(statusService.getStatusById(taskDto.getTaskStatusId()))
            .labels(labels)
            .author(userService.getCurrentUser())
            .executor(userService.getUserById(taskDto.getExecutorId()))
            .build();
    }
}
