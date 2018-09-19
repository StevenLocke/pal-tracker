package io.pivotal.pal.tracker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
  HashMap<Long, TimeEntry> storage = new HashMap<>();
  private Long nextTimeEntryId = 1L;

  @Override
  public TimeEntry create(TimeEntry timeEntry) {
    TimeEntry createdTimeEntry = new TimeEntry(nextTimeEntryId,
        timeEntry.getProjectId(),
        timeEntry.getUserId(),
        timeEntry.getDate(),
        timeEntry.getHours());
    nextTimeEntryId += 1L;
    storage.put(createdTimeEntry.getId(), createdTimeEntry);
    return createdTimeEntry;
  }

  @Override
  public TimeEntry find(long id) {
    return storage.get(id);
  }

  @Override
  public List<TimeEntry> list() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public TimeEntry update(long id, TimeEntry timeEntry) {
    TimeEntry createdTimeEntry = new TimeEntry(id,
        timeEntry.getProjectId(),
        timeEntry.getUserId(),
        timeEntry.getDate(),
        timeEntry.getHours());
    storage.replace(id, createdTimeEntry);
    return storage.get(id);
  }

  @Override
  public TimeEntry delete(long id) {
    return storage.remove(id);
  }
}
