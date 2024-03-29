package hexlet.code.controller;


import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;


@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_OWNER_BY_ID = """
        @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.name
        """;

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Get list of all tasks")
    @ApiResponse(responseCode = "200", description = "List of all tasks",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    @GetMapping
    public Iterable<Task> getAllTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskService.getTasks(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task with that id not found", content = @Content)
    })
    @GetMapping(ID)
    public Task getTaskById(@Parameter(description = "id of task to be searched") @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Task created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    @PreAuthorize(AUTHENTICATED)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task with that id not found")
    })
    @PreAuthorize(AUTHENTICATED + " and " + ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public Task updateTask(@Parameter(description = "id of task to be updated") @PathVariable Long id,
                           @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(description = "Delete task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task with that id not found", content = @Content)
    })
    @PreAuthorize(AUTHENTICATED + " and " + ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteTask(@Parameter(description = "id of task to be deleted") @PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

}
