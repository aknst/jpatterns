package ru.mirea.prac22.services;

import ru.mirea.prac22.dtos.GroupDto;
import ru.mirea.prac22.entities.Group;
import ru.mirea.prac22.exceptions.ResourceNotFound;
import ru.mirea.prac22.repositories.GroupRepo;
import ru.mirea.prac22.specifications.GroupSpecifications;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Transactional
public class GroupService {

  private final GroupRepo groupRepo;


  public UUID createGroup(GroupDto groupDto) {
    var group = new Group(groupDto);
    groupRepo.save(group);
    return group.getId();
  }

  public GroupDto getGroup(UUID id) {
    var group = groupRepo.findById(id);
    if (group.isEmpty()) {
      throw new ResourceNotFound();
    }
    return GroupDto.builder().name(group.get().getName()).build();
  }

  public void deleteGroup(UUID id) {
    groupRepo.deleteById(id);
  }

  public void updateGroup(UUID id, GroupDto groupDto) {
    var optionalGroup = groupRepo.findById(id);
    if (optionalGroup.isEmpty()) {
      throw new ResourceNotFound();
    }
    var existingGroup = optionalGroup.get();
    existingGroup.setName(groupDto.getName());

    groupRepo.save(existingGroup);
  }

  public List<GroupDto> getFilteredGroups(String prefix) {
    var groups = groupRepo.findAll(GroupSpecifications.startsWithPrefix(prefix));
    if (groups.isEmpty()) {
      throw new ResourceNotFound();
    }

    return groups.stream().map(group -> GroupDto.builder().name(group.getName()).build())
        .collect(Collectors.toList());
  }
}
