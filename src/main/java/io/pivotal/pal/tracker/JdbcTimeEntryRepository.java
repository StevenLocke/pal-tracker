package io.pivotal.pal.tracker;

import static java.sql.Statement.NO_GENERATED_KEYS;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

  private JdbcTemplate jdbcTemplate;

  public JdbcTimeEntryRepository(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public TimeEntry create(TimeEntry timeEntry) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO time_entries (project_id, user_id, date, hours) " +
              "VALUES (?, ?, ?, ?)",
          RETURN_GENERATED_KEYS
      );

      statement.setLong(1, timeEntry.getProjectId());
      statement.setLong(2, timeEntry.getUserId());
      statement.setDate(3, Date.valueOf(timeEntry.getDate()));
      statement.setInt(4, timeEntry.getHours());

      return statement;
    }, keyHolder);
    return find(keyHolder.getKey().longValue());
  }

  @Override
  public TimeEntry find(long id) {
    return jdbcTemplate.query("SELECT * FROM time_entries WHERE id=?",
        new Object[] {id},
        extractor);
  }

  @Override
  public List<TimeEntry> list() {
    return jdbcTemplate.query("SELECT * FROM time_entries", rowMapper);
  }

  @Override
  public TimeEntry update(long id, TimeEntry timeEntry) {
    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id= ? ",
          RETURN_GENERATED_KEYS
      );
      statement.setLong(1, timeEntry.getProjectId());
      statement.setLong(2, timeEntry.getUserId());
      statement.setDate(3, Date.valueOf(timeEntry.getDate()));
      statement.setInt(4, timeEntry.getHours());
      statement.setLong(5, id);


      return statement;
    });
    return find(id);
  }

  @Override
  public TimeEntry delete(long id) {
    TimeEntry timeEntry = find(id);
    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM time_entries WHERE id=?", NO_GENERATED_KEYS
      );

      statement.setLong(1, id);
      return statement;
    });
    return timeEntry;
  }

  private final RowMapper<TimeEntry> rowMapper = (rs, index) -> new TimeEntry(
      rs.getLong("id"),
      rs.getLong("project_id"),
      rs.getLong("user_id"),
      rs.getDate("date").toLocalDate(),
      rs.getInt("hours")
  );


  private final ResultSetExtractor<TimeEntry> extractor =
      (rs) -> rs.next() ? rowMapper.mapRow(rs, 1) : null;
}
