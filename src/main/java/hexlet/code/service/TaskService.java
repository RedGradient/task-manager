package hexlet.code.service;


import com.querydsl.core.types.Predicate;
import hexlet.code.exceptions.TaskNotFoundException;
import hexlet.code.dto.TaskDto;
import hexlet.code.models.Label;
import hexlet.code.models.Task;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.repositories.UserRepository;
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
        var updatedLabels = new HashSet<Label>();
        for (long labelId : taskDto.getLabelIds()) {
            updatedLabels.add(labelService.getLabelById(labelId));
        }

        var task = getTaskById(id);
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(statusService.getStatusById(taskDto.getTaskStatusId()));
        task.setLabels(updatedLabels);
        task.setExecutor(userService.getUserById(taskDto.getExecutorId()));

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
