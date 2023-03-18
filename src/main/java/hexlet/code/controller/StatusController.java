package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.models.Status;
import hexlet.code.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private final String ID = "/id";
    private final String AUTHENTICATED = "isAuthenticated()";

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
