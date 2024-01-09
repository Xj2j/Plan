package ru.xj2j.plan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspaces")
public class Workspace extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]*$")
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    @Pattern(regexp = "^[a-z0-9\\-_]*$")
    private String slug;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<WorkspaceMember> members = new ArrayList<>();

    public void setSlug(String slug) {
        this.slug = slug.toLowerCase().replaceAll(" ", "-");
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workspace workspace)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getSlug(), workspace.getSlug());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSlug());
    }
}
