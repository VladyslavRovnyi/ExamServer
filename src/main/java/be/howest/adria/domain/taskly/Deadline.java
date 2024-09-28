package be.howest.adria.domain.taskly;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;

public class Deadline {

  public static final Deadline UNLIMITED = new Deadline();
  private LocalDate deadlineDate;

  public enum Status {
    EXPIRED, PENDING, UNLIMITED
  }

  public void update(Deadline newDeadline) {
    deadlineDate = newDeadline.deadlineDate;
  }

  public static Deadline create(String date) {
    try {
      LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
      return new Deadline(date);
    } catch (DateTimeException e) {
      return new Deadline();
    }
  }

  public static Deadline create(int day, int month, int year) {
    return new Deadline(LocalDate.of(year, month, day));
  }

  public boolean isBefore(Deadline newDeadline) {
    return this.deadlineDate.isBefore(newDeadline.deadlineDate);
  }

  private Deadline(String date) {
    this(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
  }

  private Deadline() {
    this(LocalDate.MAX);
  }

  private Deadline(LocalDate deadlineDate) {
    this.deadlineDate = deadlineDate;
  }

  public LocalDate date() {
    return deadlineDate;
  }

  public Status status() {
    if (this.deadlineDate == LocalDate.MAX)
      return Status.UNLIMITED;

    if (LocalDate.now().isAfter(this.deadlineDate))
      return Status.EXPIRED;

    return Status.PENDING;
  }

  public String toString() {
    return deadlineDate.toString();
  }
}
