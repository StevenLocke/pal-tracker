package io.pivotal.pal.tracker;

import java.util.List;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeEntryController {
  private TimeEntryRepository timeEntryRepository;
  private CounterService counterService;
  private GaugeService gaugeService;

  public TimeEntryController(TimeEntryRepository timeEntryRepository, CounterService counterService, GaugeService gaugeService) {
    this.timeEntryRepository = timeEntryRepository;
    this.counterService = counterService;
    this.gaugeService = gaugeService;

  }

  @PostMapping("/time-entries")
  public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
    counterService.increment("TimeEntries.create");
    gaugeService.submit("entries", timeEntryRepository.list().size() + 1);
    return new ResponseEntity<>(timeEntryRepository.create(timeEntry), HttpStatus.CREATED);
  }

  @GetMapping("/time-entries/{id}")
  public ResponseEntity<TimeEntry> read(@PathVariable Long id) {
    counterService.increment("TimeEntries.read");
    TimeEntry timeEntry = timeEntryRepository.find(id);
    if (timeEntry != null) {
      return new ResponseEntity<>(timeEntry, HttpStatus.OK);
    }
    return new ResponseEntity<>(timeEntry, HttpStatus.NOT_FOUND);
  }

  @GetMapping("/time-entries")
  public ResponseEntity<List<TimeEntry>> list() {
    counterService.increment("TimeEntries.list");
    return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
  }

  @PutMapping("/time-entries/{id}")
  public ResponseEntity<TimeEntry> update(@PathVariable Long id, @RequestBody TimeEntry timeEntry) {
    counterService.increment("TimeEntries.update");
    TimeEntry foundTimeEntry = timeEntryRepository.update(id, timeEntry);
    if (foundTimeEntry != null) {
      return new ResponseEntity<>(foundTimeEntry, HttpStatus.OK);
    }
    return new ResponseEntity<>(foundTimeEntry, HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/time-entries/{id}")
  public ResponseEntity<TimeEntry> delete(@PathVariable Long id) {
    counterService.increment("TimeEntries.deleted");
    gaugeService.submit("entries", timeEntryRepository.list().size() - 1);
    return new ResponseEntity<>(timeEntryRepository.delete(id), HttpStatus.NO_CONTENT);
  }
}

