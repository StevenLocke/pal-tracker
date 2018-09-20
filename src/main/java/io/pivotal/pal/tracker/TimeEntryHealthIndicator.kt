import io.pivotal.pal.tracker.TimeEntryRepository
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.springframework.boot.actuate.metrics.GaugeService

@Component
class TimeEntryHealthIndicator(val timeEntryRepository: TimeEntryRepository) : HealthIndicator {


    override fun health(): Health {
        val errorCode = check() // perform some specific health check
        return if (errorCode != 0) {
            Health.down().withDetail("Error Code", errorCode).build()
        } else Health.up().build()
    }

    fun check(): Int {
        return if (timeEntryRepository.list().size <= 5) 0 else 1
    }

}