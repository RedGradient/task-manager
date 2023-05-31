package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hexlet.code.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseModel {

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @JsonIgnore
    @Min(3)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    private boolean active = true;

    public User(final Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return Objects.equals(getEmail(), ((User) o).getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return getEmail();
    }
}
