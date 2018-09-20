package io.pivotal.pal.tracker

import com.mysql.cj.jdbc.MysqlDataSource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.PreparedStatement
import java.util.*
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.sql.Time
import java.time.LocalDate


class JdbcTimeEntryRepository(dataSource: MysqlDataSource): TimeEntryRepository {
    private var jdbcTemplate : JdbcTemplate

    init{
        this.jdbcTemplate = JdbcTemplate(dataSource)
    }

    override fun create(timeEntry: TimeEntry): TimeEntry {
        val keyHolder = GeneratedKeyHolder()
        val psc = PreparedStatementCreator {connection ->
            val statement: PreparedStatement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " + "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            )
            statement.setLong(1, timeEntry.projectId)
            statement.setLong(2, timeEntry.userId)
            statement.setDate(3, java.sql.Date.valueOf(timeEntry.date))
            statement.setInt(4, timeEntry.hours)
            statement
        }

        jdbcTemplate.update(psc, keyHolder)

        //find the new entry
        return find(keyHolder.key.toLong()) as TimeEntry
    }

    override fun find(id: Long): TimeEntry? {
        val timeEntries: List<TimeEntry> = jdbcTemplate.query("select * from time_entries where id =" + id, rowMapper)
        return if (timeEntries.isEmpty()) null else timeEntries.first()
    }

    override fun list(): List<TimeEntry>{
        return jdbcTemplate.query("select * from time_entries", rowMapper)
    }

    override fun update(id: Long, timeEntry: TimeEntry): TimeEntry{
        val psc = PreparedStatementCreator {connection ->
            val statement: PreparedStatement = connection.prepareStatement(
                    "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?;" ,
                    RETURN_GENERATED_KEYS
            )
            statement.setLong(1, timeEntry.projectId)
            statement.setLong(2, timeEntry.userId)
            statement.setDate(3, java.sql.Date.valueOf(timeEntry.date))
            statement.setInt(4, timeEntry.hours)
            statement.setLong(5, id)
            statement
        }

        jdbcTemplate.update(psc)
        return find(id) as TimeEntry

    }

    override fun delete(id: Long): TimeEntry{
        val timeEntry = find(id) as TimeEntry

        jdbcTemplate.update({connection ->
            val statement: PreparedStatement = connection.prepareStatement(
                    "DELETE FROM time_entries WHERE id = " + id ,
                    RETURN_GENERATED_KEYS
            )
            statement
        })
        return timeEntry
    }

    private val rowMapper: RowMapper<TimeEntry> = RowMapper { resultSet, _ -> TimeEntry(resultSet.getLong(1),
            resultSet.getLong(2), resultSet.getLong(3), resultSet.getDate(4).toLocalDate(), resultSet.getInt(5))}

}