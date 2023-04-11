package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.models.BaseModel;
import hexlet.code.models.Label;
import hexlet.code.models.Task;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.repositories.TaskStatusRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    private static final String TASK_CONTROLLER = "/api/tasks";
    private static final String EXECUTOR_USERNAME = "executor@mail.com";


    @Autowired
    private TaskController controller;
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

    @BeforeAll
    public void beforeAll() throws Exception {

    }
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
    public void contextLoads() {
        assertNotNull(controller);
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

    @Test
    public void getTasksByParams() throws Exception {
        var user_1 = utils.getUserByEmail(TEST_USERNAME);
        var user_2 = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus_1 = statusService.createNewStatus(new TaskStatusDto("Status 1"));
        var taskStatus_2 = statusService.createNewStatus(new TaskStatusDto("Status 2"));

        var labels_1 = new HashSet<Label>();
        var labels_2 = new HashSet<Label>();
        var label_1 = labelService.createLabel(new LabelDto("Label 1"));
        var label_2 = labelService.createLabel(new LabelDto("Label 2"));
        var label_3 = labelService.createLabel(new LabelDto("Label 3"));
        labels_1.add(label_1);
        labels_2.add(label_2);
        labels_2.add(label_3);

        taskRepository.save(new Task("Task 1", "Descr", taskStatus_1, labels_1, user_1, user_2));
        taskRepository.save(new Task("Task 2", "Descr", taskStatus_2, labels_2, user_2, user_1));
        taskRepository.save(new Task("Task 3", "Descr", taskStatus_2, labels_2, user_2, user_2));

        // check param: 'taskStatusId'
        final long expectedTaskStatusId = 2;
        var request_1 = get(TASK_CONTROLLER)
                .param("taskStatusId", String.valueOf(expectedTaskStatusId));
        var response_1 = utils.perform(request_1).andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> tasks_1 = fromJson(response_1.getContentAsString(), new TypeReference<>() { });
        assertEquals(2, tasks_1.size(), "Collection has incorrect size");
        assertEquals(expectedTaskStatusId, tasks_1.get(0).getTaskStatus().getId(),"Task has incorrect task status");

        // check param: 'authorId'
        final long expectedAuthorId = 1;
        var request_2 = get(TASK_CONTROLLER)
                .param("authorId", String.valueOf(expectedAuthorId));
        var response_2 = utils.perform(request_2)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> tasks_2 = fromJson(response_2.getContentAsString(), new TypeReference<>() { });
        assertEquals(1, tasks_2.size(), "Collection has incorrect size");
        assertEquals(expectedAuthorId, tasks_2.get(0).getAuthor().getId(), "Task has incorrect author");

        // check param: 'executorId'
        var expectedExecutorId = 2;
        var request_3 = get(TASK_CONTROLLER)
                .param("executorId", String.valueOf(expectedExecutorId));
        var response_3 = utils.perform(request_3)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> tasks_3 = fromJson(response_3.getContentAsString(), new TypeReference<>() { });
        assertEquals(2, tasks_3.size(), "Collection has incorrect size");
        assertEquals(expectedExecutorId, tasks_3.get(0).getExecutor().getId(), "Task has incorrect executor");
        assertEquals(expectedExecutorId, tasks_3.get(1).getExecutor().getId(), "Task has incorrect executor");

        // check param: 'labels'
        var expectedLabelId = 1;
        var request_4 = get(TASK_CONTROLLER)
                .param("labels", String.valueOf(expectedLabelId));
        var response_4 = utils.perform(request_4)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> tasks_4 = fromJson(response_4.getContentAsString(), new TypeReference<>() { });
        assertEquals(1, tasks_4.size(), "Collection has incorrect size");
        assertTrue(tasks_4.get(0).getLabels().contains(label_1), "Task has incorrect label");
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

    @Test
    public void updateTask() throws Exception {
        var oldTask = createDefaultTask();

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
        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        var updatedTask = taskRepository.findById(oldTask.getId()).get();
        assertEquals(newName, updatedTask.getName());
        assertEquals(newDescr, updatedTask.getDescription());
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

        var task = taskRepository.findById(oldTask.getId()).get();
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
