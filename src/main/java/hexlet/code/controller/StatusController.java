package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.models.Status;
import hexlet.code.service.StatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";

    @Autowired
    private StatusService statusService;

    @GetMapping
    public Iterable<Status> getStatuses() {
        return statusService.getStatuses();
    }

    @GetMapping(ID)
    public Status getStatus(@PathVariable Long id) {
        return statusService.getStatus(id);
    }

    @PreAuthorize(AUTHENTICATED)
    @PostMapping
    public Status createStatus(@RequestBody StatusDto statusDto) {
        return statusService.createNewStatus(statusDto);
    }

    @PreAuthorize(AUTHENTICATED)
    @PutMapping(ID)
    public Status updateStatus(@PathVariable Long id, @RequestBody StatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @PreAuthorize(AUTHENTICATED)
    @DeleteMapping
    public void deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
    }

}
