package ru.mirea.prac19.contollers;


import ru.mirea.prac19.dtos.GroupDto;
import ru.mirea.prac19.exceptions.ResourceNotFound;
import ru.mirea.prac19.services.GroupService;
import java.net.URI;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/v1/group")
@AllArgsConstructor
@Slf4j
public class GroupController {

  private GroupService service;

  @GetMapping("/{id}")
  public ResponseEntity<?> getGroup(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(service.getGroup(id));
    } catch (ResourceNotFound e) {
      log.warn(e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<?> createGroup(@RequestBody GroupDto groupDto) {
    var id = service.createGroup(groupDto);
    return ResponseEntity.created(URI.create(String.format("/v1/group/%s", id.toString()))).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteGroup(@PathVariable UUID id) {
    try {
      service.deleteGroup(id);
      return ResponseEntity.noContent().build();
    } catch (ResourceNotFound e) {
      log.warn(e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateGroup(@PathVariable UUID id, @RequestBody GroupDto group) {
    try {
      service.updateGroup(id, group);
      return ResponseEntity.ok().build();
    } catch (ResourceNotFound e) {
      log.warn(e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/filter")
  public ResponseEntity<?> filterGroups(@RequestParam String groupNamePrefix) {
    try {
      return ResponseEntity.ok(service.getFilteredGroups(groupNamePrefix));
    } catch (ResourceNotFound e) {
      log.warn(e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}
