package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;


@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";

    @Autowired
    private TaskStatusService statusService;

    @Operation(summary = "Get list of all statuses")
    @ApiResponse(responseCode = "200", description = "List of all statuses",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class)))
    @GetMapping
    public Iterable<TaskStatus> getAllStatuses() {
        return statusService.getStatuses();
    }

    @Operation(summary = "Get specific status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "404", description = "Status with that id not found", content = @Content)
    })
    @GetMapping(ID)
    public TaskStatus getStatusById(@PathVariable Long id) {
        return statusService.getStatusById(id);
    }

    @Operation(summary = "Create status")
    @ApiResponse(responseCode = "201", description = "Status created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class)))
    @PreAuthorize(AUTHENTICATED)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(@RequestBody TaskStatusDto statusDto) {
        return statusService.createNewStatus(statusDto);
    }

    @Operation(summary = "Update status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "404", description = "Status with that id not found", content = @Content)
    })
    @PreAuthorize(AUTHENTICATED)
    @PutMapping(ID)
    public TaskStatus updateStatus(@PathVariable Long id, @RequestBody TaskStatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @Operation(description = "Delete status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Status with that id not found", content = @Content),
        @ApiResponse(responseCode = "409",
                description = "Status can not be deleted as it is used in a task", content = @Content)
    })
    @PreAuthorize(AUTHENTICATED)
    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
    }

}
