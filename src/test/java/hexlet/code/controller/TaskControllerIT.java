package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.BaseModel;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    private static final String TASK_CONTROLLER = "/api/tasks";
    private static final String EXECUTOR_USERNAME = "executor@mail.com";

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusService statusService;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelService labelService;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void beforeEach() throws Exception {
        utils.regDefaultUser();
        userService.createNewUser(new UserDto(
                EXECUTOR_USERNAME,
                "Good",
                "Executor",
                "12345"
        ));
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createTask() throws Exception {
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var label = labelService.createLabel(new LabelDto("Feature"));
        var labels = new HashSet<Long>();
        labels.add(label.getId());
        var taskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                labels,
                executor.getId()
        );

        var request = post(TASK_CONTROLLER)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task task = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertTrue(taskRepository.findById(task.getId()).isPresent());
    }

    @Test
    public void getTasks() throws Exception {
        // create some tasks
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));

        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));

        final var newTaskCount = 3;

        taskRepository.save(new Task("Task 1", "Descr", taskStatus, labels, author, executor));
        taskRepository.save(new Task("Task 2", "Descr", taskStatus, labels, author, executor));
        taskRepository.save(new Task("Task 3", "Descr", taskStatus, labels, author, executor));
        // -------

        var response = utils.perform(get(TASK_CONTROLLER))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(newTaskCount, tasks.size());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FiltrationTest {
        private static final String STATUS_NAME_1 = "Status 1";
        private static final String STATUS_NAME_2 = "Status 2";
        private static final String LABEL_NAME_1 = "Label 1";
        private static final String LABEL_NAME_2 = "Label 2";
        private static final String LABEL_NAME_3 = "Label 3";
        private static final String DESCRIPTION = "Description";
        private static final String TASK_NAME_1 = "Task 1";
        private static final String TASK_NAME_2 = "Task 2";
        private static final String TASK_NAME_3 = "Task 3";

        void tearDown() {
            taskRepository.deleteAll();
            statusRepository.deleteAll();
            labelRepository.deleteAll();
            userRepository.deleteAll();
        }

        @BeforeEach
        void beforeEach() throws Exception {
            tearDown();

            utils.regDefaultUser();

            userService.createNewUser(new UserDto(
                    EXECUTOR_USERNAME,
                    "Good",
                    "Executor",
                    "12345"
            ));

            prepareData();
        }

        void prepareData() {
            var user1 = utils.getUserByEmail(TEST_USERNAME);
            var user2 = utils.getUserByEmail(EXECUTOR_USERNAME);

            var taskStatus1 = statusService.createNewStatus(new TaskStatusDto(STATUS_NAME_1));
            var taskStatus2 = statusService.createNewStatus(new TaskStatusDto(STATUS_NAME_2));

            var labels1 = new HashSet<Label>();
            var labels2 = new HashSet<Label>();

            var label1 = labelService.createLabel(new LabelDto(LABEL_NAME_1));
            var label2 = labelService.createLabel(new LabelDto(LABEL_NAME_2));
            var label3 = labelService.createLabel(new LabelDto(LABEL_NAME_3));

            labels1.add(label1);
            labels2.add(label2);
            labels2.add(label3);

            taskRepository.save(new Task(TASK_NAME_1, DESCRIPTION, taskStatus1, labels1, user1, user2));
            taskRepository.save(new Task(TASK_NAME_2, DESCRIPTION, taskStatus2, labels2, user2, user1));
            taskRepository.save(new Task(TASK_NAME_3, DESCRIPTION, taskStatus2, labels2, user2, user2));
        }

        @Test
        public void filterByTaskStatusId() throws Exception {
            var expectedStatusOptional = statusRepository.findByName(STATUS_NAME_2);

            assertTrue(expectedStatusOptional.isPresent());

            var expectedStatusId = expectedStatusOptional.get().getId();
            var request = get(TASK_CONTROLLER).param("taskStatusId", String.valueOf(expectedStatusId));
            var response = utils.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
            assertEquals(2, tasks.size(), "Collection has incorrect size");
            assertEquals(expectedStatusId, tasks.get(0).getTaskStatus().getId(), "Task has incorrect task status");
        }

        @Test
        public void filterByAuthorId() throws Exception {
            var expectedAuthorId = utils.getUserByEmail(TEST_USERNAME).getId();
            var request = get(TASK_CONTROLLER).param("authorId", String.valueOf(expectedAuthorId));
            var response = utils.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
            assertEquals(1, tasks.size(), "Collection has incorrect size");
            assertEquals(expectedAuthorId, tasks.get(0).getAuthor().getId(), "Task has incorrect author");
        }

        @Test
        public void filterByExecutorId() throws Exception {
            var expectedExecutorId = utils.getUserByEmail(EXECUTOR_USERNAME).getId();
            var request = get(TASK_CONTROLLER) .param("executorId", String.valueOf(expectedExecutorId));
            var response = utils.perform(request) .andExpect(status().isOk()).andReturn().getResponse();

            final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
            assertEquals(2, tasks.size(), "Collection has incorrect size");
            assertEquals(expectedExecutorId, tasks.get(0).getExecutor().getId(), "Task has incorrect executor");
            assertEquals(expectedExecutorId, tasks.get(1).getExecutor().getId(), "Task has incorrect executor");
        }

        @Test
        public void getTasksByLabelId() throws Exception {
            var expectedLabelOptional = labelRepository.findByName(LABEL_NAME_1);

            assertTrue(expectedLabelOptional.isPresent());

            var expectedLabelId = expectedLabelOptional.get().getId();
            var request = get(TASK_CONTROLLER).param("labels", String.valueOf(expectedLabelId));
            var response = utils.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
            assertEquals(1, tasks.size(), "Collection has incorrect size");
            var labels = tasks.get(0).getLabels();
            var isAnyLabelWithExpectedName = labels.stream().anyMatch((label) -> label.getName().equals(LABEL_NAME_1));
            assertTrue(isAnyLabelWithExpectedName, "Task has incorrect label");
        }
    }

    @Test
    public void getTaskById() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = new Task(
                "Task",
                "Description",
                taskStatus,
                labels,
                author,
                executor
        );
        taskRepository.save(task);

        var response = utils.perform(get(TASK_CONTROLLER + ID, task.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task recievedTask = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertEquals(task.getName(), recievedTask.getName());
    }

    @Test
    public void createTaskFails() throws Exception {
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Long>();
        labels.add(labelService.createLabel(new LabelDto("Feature")).getId());
        var taskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                labels,
                executor.getId()
        );

        var request = post(TASK_CONTROLLER)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(request)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

    }

    TaskDto createTaskDto() throws Exception {
        var newName = "New Task";
        var newDescr = "New Descr";
        var newStatus = statusService.createNewStatus(new TaskStatusDto("New Status"));
        var newLabels = new HashSet<Label>();
        newLabels.add(labelService.createLabel(new LabelDto("New Label 1")));
        newLabels.add(labelService.createLabel(new LabelDto("New Label 2")));
        HashSet<Long> newLabelIds = newLabels
                .stream()
                .map(BaseModel::getId)
                .collect(Collectors.toCollection(HashSet::new));

        var newExecutorDto = new UserDto();
        var newExecutorEmail = "another-executor@mail.com";
        newExecutorDto.setEmail(newExecutorEmail);
        newExecutorDto.setPassword("12345");
        newExecutorDto.setFirstName("Another");
        newExecutorDto.setLastName("Executor");
        utils.regUser(newExecutorDto);
        var newExecutor = userService.getUserByEmail(newExecutorEmail);

        return new TaskDto(
                newName,
                newDescr,
                newStatus.getId(),
                newLabelIds,
                newExecutor.getId()
        );
    }

    @Test
    public void updateTask() throws Exception {
        var oldTask = createDefaultTask();
        var newTaskDto = createTaskDto();

        var request = put(TASK_CONTROLLER + ID, oldTask.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        var updatedTaskOptional = taskRepository.findById(oldTask.getId());
        assertTrue(updatedTaskOptional.isPresent());
        var updatedTask = updatedTaskOptional.get();
        var newStatus = statusService.getStatusById(newTaskDto.getTaskStatusId());
        var newLabels = newTaskDto.getLabelIds().stream()
                .map((id) -> labelService.getLabelById(id)).collect(Collectors.toSet());
        var newExecutor = userService.getUserById(newTaskDto.getExecutorId());

        assertEquals(newTaskDto.getName(), updatedTask.getName());
        assertEquals(newTaskDto.getDescription(), updatedTask.getDescription());
        assertEquals(newStatus, updatedTask.getTaskStatus());
        assertEquals(newLabels, updatedTask.getLabels());
        assertEquals(newExecutor, updatedTask.getExecutor());
    }

    @Test
    public void updateTaskFails() throws Exception {
        var oldTask = createDefaultTask();

        var newName = "New Task";
        var newDescr = "New Descr";
        var newStatus = statusService.createNewStatus(new TaskStatusDto("New Status"));
        var newLabelIds = new HashSet<Long>();
        newLabelIds.add(labelService.createLabel(new LabelDto("New Label 1")).getId());
        newLabelIds.add(labelService.createLabel(new LabelDto("New Label 2")).getId());

        var newExecutorDto = new UserDto();
        var newExecutorEmail = "another-executor@mail.com";
        newExecutorDto.setEmail(newExecutorEmail);
        newExecutorDto.setPassword("12345");
        newExecutorDto.setFirstName("Another");
        newExecutorDto.setLastName("Executor");
        utils.regUser(newExecutorDto);
        var newExecutor = userService.getUserByEmail(newExecutorEmail);

        var newTaskDto = new TaskDto(
                newName,
                newDescr,
                newStatus.getId(),
                newLabelIds,
                newExecutor.getId()
        );

        var request = put(TASK_CONTROLLER + ID, oldTask.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(request).andExpect(status().isForbidden());

        var taskOptional = taskRepository.findById(oldTask.getId());
        assertTrue(taskOptional.isPresent());
        var task = taskOptional.get();
        assertEquals(oldTask.getName(), task.getName());
        assertEquals(oldTask.getDescription(), task.getDescription());
        assertEquals(oldTask.getTaskStatus(), task.getTaskStatus());
        assertEquals(oldTask.getLabels(), task.getLabels());
        assertEquals(oldTask.getExecutor(), task.getExecutor());
    }

    @Test
    public void deleteTask() throws Exception {
        var task = createDefaultTask();

        assertEquals(1, taskRepository.count());

        var request = delete(TASK_CONTROLLER + ID, task.getId());
        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }

    @Test
    public void deleteTaskFails() throws Exception {
        var task = createDefaultTask();

        assertEquals(1, taskRepository.count());

        var request = delete(TASK_CONTROLLER + ID, task.getId());
        utils.perform(request).andExpect(status().isForbidden());

        assertEquals(1, taskRepository.count());
    }

    private Task createDefaultTask() {
        var taskName = "Task";
        var taskDescr = "Descr";
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));

        var task = new Task(taskName, taskDescr, taskStatus, labels, author, executor);
        return taskRepository.save(task);
    }

}
