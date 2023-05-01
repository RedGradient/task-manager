package hexlet.code.service;

import hexlet.code.exceptions.LabelNotFoundException;
import hexlet.code.dto.LabelDto;
import hexlet.code.models.Label;
import hexlet.code.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    public Label createLabel(LabelDto labelDto) {
        var label = new Label(labelDto.getName());
        return labelRepository.save(label);
    }

    public Label getLabelById(long id) {
        return labelRepository.findById(id).orElseThrow(
                () -> new LabelNotFoundException(id)
        );
    }

    public Set<Label> getAllLabels() {
        var labels = new HashSet<Label>();
        for (var label : labelRepository.findAll()) {
            labels.add(label);
        }
        return labels;
    }

    public Label updateLabel(long id, LabelDto labelDto) {
        var labelToUpdate = getLabelById(id);
        labelToUpdate.setName(labelDto.getName());
        return labelRepository.save(labelToUpdate);
    }

    public void deleteLabel(long id) {
        labelRepository.deleteById(id);
    }

}
