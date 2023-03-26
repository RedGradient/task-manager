package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.models.TaskStatus;
import hexlet.code.service.TaskStatusService;

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
@RequestMapping("/api/statuses")
public class TaskStatusController {

    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";

    @Autowired
    private TaskStatusService statusService;

    @Operation(summary = "Get list of all statuses")
    @ApiResponse(responseCode = "200", description = "List of all statuses")
    @GetMapping
    public Iterable<TaskStatus> getStatuses() {
        return statusService.getStatuses();
    }

    @Operation(summary = "Get specific status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status found"),
        @ApiResponse(responseCode = "404", description = "Status with that id not found")
    })
    @GetMapping(ID)
    public TaskStatus getStatusById(@PathVariable Long id) {
        return statusService.getStatusById(id);
    }

    @Operation(summary = "Create status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PreAuthorize(AUTHENTICATED)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(@RequestBody TaskStatusDto statusDto) {
        return statusService.createNewStatus(statusDto);
    }

    @Operation(summary = "Update status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "404", description = "Status with that id not found")
    })
    @PreAuthorize(AUTHENTICATED)
    @PutMapping(ID)
    public TaskStatus updateStatus(@PathVariable Long id, @RequestBody TaskStatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @Operation(description = "Delete status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status deleted"),
        @ApiResponse(responseCode = "404", description = "Status with that id not found")
    })
    @PreAuthorize(AUTHENTICATED)
    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
    }

}