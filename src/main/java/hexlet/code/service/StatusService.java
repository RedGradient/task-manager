package hexlet.code.service;


import hexlet.code.StatusNotFoundException;
import hexlet.code.dto.StatusDto;
import hexlet.code.models.Status;
import hexlet.code.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StatusService {

    @Autowired
    private StatusRepository repository;

    public Iterable<Status> getStatuses() {
        return repository.findAll();
    }

    public Status getStatus(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new StatusNotFoundException(id)
        );
    }

    public Status createNewStatus(StatusDto statusDto) {
        final Status newStatus = new Status();
        newStatus.setName(statusDto.getName());
        return repository.save(newStatus);
    }

    public Status updateStatus(Long id, StatusDto statusDto) {
        final Status statusToUpdate = repository.findById(id).get();
        statusToUpdate.setName(statusDto.getName());
        return repository.save(statusToUpdate);
    }

    public void deleteStatus(Long id) {
        repository.deleteById(id);
    }

}
