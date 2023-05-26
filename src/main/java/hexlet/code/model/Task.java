package hexlet.code.model;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseModel {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    private TaskStatus taskStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Label> labels = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;
}
