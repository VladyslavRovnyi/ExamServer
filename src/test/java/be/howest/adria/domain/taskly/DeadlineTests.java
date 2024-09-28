package be.howest.adria.domain.taskly;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import be.howest.adria.domain.taskly.Deadline.Status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class DeadlineTests {

  @Test
  void constructorWithValidDateSetsCorrectStatus() {
    // Arrange
    LocalDate futureDate = LocalDate.now().plusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = futureDate.format(formatter);

    // Act
    Deadline deadline = Deadline.create(date);

    // Assert
    assertEquals(Status.PENDING, deadline.status());
  }

  @Test
  void constructorWithPastDateSetsExpiredStatus() {
    // Arrange
    LocalDate pastData = LocalDate.now().minusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = pastData.format(formatter);

    // Act
    Deadline deadline = Deadline.create(date);

    // Assert
    assertEquals(Status.EXPIRED, deadline.status());
  }

  @Test
  void emptyConstructorSetsUnlimitedStatus() {
    // Arrange & Act
    Deadline deadline = Deadline.UNLIMITED;

    // Assert
    assertEquals(Status.UNLIMITED, deadline.status());
  }

  @Test
  void dateReturnsCorrectOptionalDate() {
    // Arrange
    LocalDate now = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = now.format(formatter);
    Deadline deadline = Deadline.create(date);

    // Act
    LocalDate result = deadline.date();

    // Assert
    assertEquals(result, LocalDate.of(
      LocalDate.now().getYear(),
      LocalDate.now().getMonthValue(),
      LocalDate.now().getDayOfMonth()));
  }

  @Test
  void dateReturnsEmptyOptionalForUnlimitedDeadline() {
    // Arrange & Act
    Deadline deadline = Deadline.UNLIMITED;

    // Assert
    assertEquals(LocalDate.MAX, deadline.date());
  }

  @Test
  void isExpiredReturnsFalseForPendingDeadline() {
    // Arrange
    LocalDate futureDate = LocalDate.now().plusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = futureDate.format(formatter);
    Deadline deadline = Deadline.create(date);

    // Act & Assert
    assertNotEquals(Status.EXPIRED, deadline.status());
  }

  @Test
  void isPendingReturnsFalseForExpiredDeadline() {
    // Arrange
    LocalDate pastData = LocalDate.now().minusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = pastData.format(formatter);
    Deadline deadline = Deadline.create(date);

    // Act & Assert
    assertNotEquals(Status.PENDING, deadline.status());
  }

  @Test
  void isUnlimitedReturnsFalseForPendingDeadline() {
    // Arrange
    LocalDate futureDate = LocalDate.now().plusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = futureDate.format(formatter);
    Deadline deadline = Deadline.create(date);

    // Act & Assert
    assertNotEquals(Status.UNLIMITED, deadline.status());
  }
}
