package hexlet.code.controller;


import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;


@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";

    @Autowired
    private LabelService labelService;


    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of all labels",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class)))
    @GetMapping
    public Iterable<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "404", description = "Label with that id not found", content = @Content)
    })
    @GetMapping(ID)
    public Label getLabelById(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Create label")
    @ApiResponse(responseCode = "201", description = "Label created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class)))
    @PostMapping
    @PreAuthorize(AUTHENTICATED)
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "404", description = "Label with that id not found", content = @Content)
    })
    @PreAuthorize(AUTHENTICATED)
    @PutMapping(ID)
    public Label updateLabel(@PathVariable Long id, @RequestBody LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(description = "Delete label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Label with that id not found", content = @Content)
    })
    @PreAuthorize(AUTHENTICATED)
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }

}
