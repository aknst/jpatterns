package ru.mirea.prac22.entities;

import ru.mirea.prac22.dtos.GroupDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "groups")
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
  private List<Student> students;

  public Group(GroupDto groupDto){
    this.name = groupDto.getName();
  }
}
