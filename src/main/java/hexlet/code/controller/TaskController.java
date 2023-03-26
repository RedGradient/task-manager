package hexlet.code.controller;


import hexlet.code.dto.TaskDto;
import hexlet.code.models.Task;
import hexlet.code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    public static final String TASK_CONTROLLER = "/api/tasks";
    public static final String ID = "/{id}";

    @Autowired
    private TaskService taskService;

    @GetMapping
    public Iterable<Task> getAllTasks() {
        return taskService.getTasks();
    }

    @GetMapping(ID)
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping(ID)
    public Task updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        return taskService.udpateTask(id, taskDto);
    }

    @DeleteMapping(ID)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

}
