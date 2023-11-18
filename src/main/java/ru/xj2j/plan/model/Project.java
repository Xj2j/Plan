package ru.xj2j.plan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends AuditModel {

    public enum Network {
        SECRET, PUBLIC
    }

    public enum Role {
        ADMIN, MEMBER, VIEWER, GUEST
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "identifier", length = 5)
    private String identifier;

    @Column(name = "name", length = 255)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "network")
    private Network network;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne
    @JoinColumn(name = "workspace_id", foreignKey = @ForeignKey(name = "fk_project_workspace"))
    private Workspace workspace;

    @OneToOne
    @JoinColumn(name = "project_lead_id", foreignKey = @ForeignKey(name = "fk_project_project_lead"))
    private User projectLead;

    public String toString() {
        return String.format("%s <%s>", this.getName(), this.getWorkspace().getName());
    }

}
