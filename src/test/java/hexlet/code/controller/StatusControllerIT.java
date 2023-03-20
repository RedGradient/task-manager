package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.StatusDto;
import hexlet.code.models.Status;
import hexlet.code.models.User;
import hexlet.code.repositories.StatusRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class StatusControllerIT {

    private final static String STATUS_CONTROLLER_PATH = "/api/statuses";
    private final static String ID = "/{id}";

    @Autowired
    private StatusController controller;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils utils;


    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void getStatuses() throws Exception {
        statusRepository.save(new Status("new"));
        final var response = utils.perform(get(STATUS_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Status> statuses = fromJson(
                response.getContentAsString(), new TypeReference<>() {}
        );

        assertEquals(1, statuses.size());
    }

    @Test
    public void getStatusById() throws Exception {
        var status = new Status("new");
        statusRepository.save(status);
        final var response = utils.perform(get(STATUS_CONTROLLER_PATH + ID, status.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }

    @Disabled
    @Test
    public void createStatus() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        var request = post(STATUS_CONTROLLER_PATH)
                .content(asJson(new StatusDto("new")))
                .contentType(APPLICATION_JSON);

        utils.perform(request, expectedUser.getEmail()).andExpect(status().isCreated());
        assertEquals(1, statusRepository.count());
    }

    @Disabled
    @Test
    public void createStatusFails() throws Exception {
        var request = post(STATUS_CONTROLLER_PATH)
                .content(asJson(new StatusDto("new")))
                .contentType(APPLICATION_JSON);
        utils.perform(request).andExpect(status().isForbidden());
        assertEquals(0, statusRepository.count());
    }

    @Disabled
    @Test
    public void updateStatus() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        var status = new Status("new");
        statusRepository.save(status);

        var updatedName = "at work";
        var request = put(STATUS_CONTROLLER_PATH + ID, status.getId())
                .content(asJson(new StatusDto(updatedName)))
                .contentType(APPLICATION_JSON);
        utils.perform(request, expectedUser.getEmail()).andExpect(status().isOk());

        assertEquals(statusRepository.findById(status.getId()).get().getName(), updatedName);
    }

    @Disabled
    @Test
    public void updateStatusFails() {}

    @Disabled
    @Test
    public void deleteStatus() {}

    @Disabled
    @Test
    public void deleteStatusFails() {}
}
