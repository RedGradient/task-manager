package hexlet.code.service;


import hexlet.code.exceptions.StatusNotFoundException;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.models.TaskStatus;
import hexlet.code.repositories.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository repository;

    public Iterable<TaskStatus> getStatuses() {
        return repository.findAll();
    }

    public TaskStatus getStatusById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new StatusNotFoundException(id)
        );
    }

    public TaskStatus createNewStatus(TaskStatusDto statusDto) {
        final TaskStatus newStatus = new TaskStatus();
        newStatus.setName(statusDto.getName());
        return repository.save(newStatus);
    }

    public TaskStatus updateStatus(Long id, TaskStatusDto statusDto) {
        final TaskStatus statusToUpdate = repository.findById(id).get();
        statusToUpdate.setName(statusDto.getName());
        return repository.save(statusToUpdate);
    }

    public void deleteStatus(Long id) {
        repository.deleteById(id);
    }

}
