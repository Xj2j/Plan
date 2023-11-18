package ru.xj2j.plan.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.xj2j.plan.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {
    public enum Priority {
        URGENT("Urgent"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low");

        private final String label;

        Priority(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private Long id;
    private ProjectDTO project;
    private IssueDTO parent;
    private StateDTO state;
    private String name;
    private String description;
    private IssueDTO.Priority priority;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    private List<UserDTO> assignees;
    private List<IssueAttachmentDTO> attachments;
    private UserDTO completedBy;
    private LocalDateTime completedAt;
}
