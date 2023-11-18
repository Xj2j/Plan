package ru.xj2j.plan.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.xj2j.plan.model.Issue;
import ru.xj2j.plan.model.Project;
import ru.xj2j.plan.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCommentDTO {

    private Long id;
    private String commentStripped = "";
    private Map<String, Object> commentJson = new HashMap<>();
    private List<String> attachments = new ArrayList<>();
    private IssueDTO issue;
    private UserDTO actor;
}
