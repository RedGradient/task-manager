package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.models.Label;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.service.LabelService;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    private static final String LABEL_CONTROLLER = "/api/labels";
    private static final String ID = "/{id}";

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private LabelService labelService;
    @Autowired
    private TestUtils utils;


    @BeforeEach
    public void beforeEach() throws Exception {
        utils.regDefaultUser();
    }
    @AfterEach
    public void afterEach() {
        labelRepository.deleteAll();
    }

    @Test
    public void createLabel() throws Exception {
        var labelName = "Feature";
        var request = post(LABEL_CONTROLLER)
                .content(asJson(new LabelDto(labelName)))
                .contentType(APPLICATION_JSON);
        var json = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn().getResponse()
                .getContentAsString();

        Label label = fromJson(json, new TypeReference<>() { });

        assertEquals(labelName, label.getName());
    }

    @Test
    public void getAllLabels() throws Exception {
        labelService.createLabel(new LabelDto("Label One"));
        labelService.createLabel(new LabelDto("Label Two"));
        labelService.createLabel(new LabelDto("Label Three"));

        var request = get(LABEL_CONTROLLER);
        var json = utils
                .perform(request)
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Label> labels = fromJson(json, new TypeReference<>() { });
        assertEquals(3, labels.size());
    }

    @Test
    public void updateLabel() throws Exception {
        var label = labelService.createLabel(new LabelDto("Old Label"));

        var newLabelName = "New Label";
        var request = put(LABEL_CONTROLLER + ID, label.getId())
                .contentType(APPLICATION_JSON)
                .content(asJson(new LabelDto(newLabelName)));
        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        var updatedLabel = labelService.getLabelById(label.getId());

        assertEquals(newLabelName, updatedLabel.getName());
    }

    @Test
    public void updateLabelFails() throws Exception {
        var labelName = "Old Label";
        var label = labelService.createLabel(new LabelDto(labelName));

        var request = put(LABEL_CONTROLLER + ID, label.getId())
                .contentType(APPLICATION_JSON)
                .content(asJson(new LabelDto("New Label")));
        utils.perform(request).andExpect(status().isForbidden());

        var updatedLabel = labelService.getLabelById(label.getId());

        assertEquals(labelName, updatedLabel.getName());
    }

    @Test
    public void deleteLabel() throws Exception {
        var label = labelService.createLabel(new LabelDto("Label"));

        var request = delete(LABEL_CONTROLLER + ID, label.getId());
        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        assertEquals(0, labelService.getAllLabels().size());
    }

    @Test
    public void deleteLabelFails() throws Exception {
        var label = labelService.createLabel(new LabelDto("Label"));

        var request = delete(LABEL_CONTROLLER + ID, label.getId());
        utils.perform(request).andExpect(status().isForbidden());

        assertEquals(1, labelService.getAllLabels().size());
    }

}
