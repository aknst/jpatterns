package ru.mirea.prac22.services;

import ru.mirea.prac22.repositories.GroupRepo;
import ru.mirea.prac22.repositories.StudentRepo;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ExportService {
  private static final String exportDirectory = "exports";
  private final List<String> entityNames = List.of("group", "student");
  private GroupRepo groupRepo;
  private StudentRepo studentRepo;


  @Scheduled(cron = "0 * * * * *")
  public void exportData() {
    log.debug("START RUNNING CRON TASK");
    Path exportsDirectory = Paths.get(exportDirectory);
    try {
      if (!Files.exists(exportsDirectory)) {
        Files.createDirectories(exportsDirectory);
      }
      Files.walk(exportsDirectory)
          .filter(Files::isRegularFile)
          .forEach(file -> {
            try {
              Files.delete(file);
            } catch (IOException e) {
              log.error("Error deleting file: {}", file, e);
            }
          });
      for (String entityName : entityNames) {
        List<?> entities = getEntities(entityName);
        Path entityFile = exportsDirectory.resolve(entityName + ".txt");
        try (FileWriter writer = new FileWriter(entityFile.toFile())) {
          for (var entity : entities) {
            writer.write(entity.toString());
            writer.write("\n");
          }
        }
      }

      log.info("Data exported successfully");
    } catch (IOException e) {
      log.error("Error exporting data", e);
    }
  }

  @ManagedOperation
  public void exportDataNow() {
    exportData();
  }
  private List<?> getEntities(String entityName) {
    return switch (entityName) {
      case "group" -> groupRepo.findAll();
      case "student" -> studentRepo.findAll();
      default -> throw new IllegalArgumentException("Unknown entity name: " + entityName);
    };
  }
}
