package hexlet.code.service;


import hexlet.code.exception.StatusNotFoundException;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.TaskStatusInUseException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskRepository taskRepository;

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
        final TaskStatus statusToUpdate = getStatusById(id);
        statusToUpdate.setName(statusDto.getName());
        return repository.save(statusToUpdate);
    }

    public void deleteStatus(Long id) {
        repository.deleteById(id);
    }

}
