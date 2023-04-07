package hexlet.code.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "labels")
@AllArgsConstructor
@NoArgsConstructor
public class Label extends BaseModel {
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Label anotherLabel)) {
            return false;
        }
        return Objects.equals(getName(), anotherLabel.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

//    @ManyToMany
//    private List<Task> tasks;
//
//    public Label(String name) {
//        this.name = name;
//    }
}
