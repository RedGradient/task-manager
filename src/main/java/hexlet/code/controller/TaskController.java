package hexlet.code.controller;


import hexlet.code.dto.TaskDto;
import hexlet.code.models.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    public static final String TASK_CONTROLLER = "/api/tasks";
    public static final String ID = "/{id}";

    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_OWNER_BY_ID = """
        @userRepository.findById(#id).get().getEmail() == authentication.name
        """;


    @Autowired
    private TaskService taskService;

    @Operation(summary = "Get list of all tasks")
    @ApiResponse(responseCode = "200", description = "List of all tasks")
    @GetMapping
    public Iterable<Task> getAllTasks() {
        return taskService.getTasks();
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found")
    })
    @GetMapping(ID)
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PreAuthorize(AUTHENTICATED)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found")
    })
    @PreAuthorize(AUTHENTICATED)
    @PutMapping(ID)
    public Task updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        return taskService.udpateTask(id, taskDto);
    }

    @Operation(description = "Delete task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

}
