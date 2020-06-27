package co.edu.icesi.metrocali.atc.evaluator.timer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import co.edu.icesi.metrocali.atc.services.evaluator.EvalParametersService;
import co.edu.icesi.metrocali.atc.services.evaluator.ExpressionsService;
import lombok.extern.log4j.Log4j2;

@Component
@EnableScheduling
@Log4j2
public class Timer {

    public static final String CRON_FORMAT_BY_SECONDS = "0/%d * * ? * *";
    public static final String CRON_FORMAT_BY_MINTES = "0 0/%d * ? * *";
    public static final String CRON_FORMAT_BY_HOURS = "0 0 0/%d ? * *";

    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ExpressionsService expressionService;

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private EvalParametersService parameterService;

    private ScheduledFuture<?> currentTask = null;

    @PostConstruct
    private void setupSchedulerConfiguration() {
        scheduleKPICalculus();
    }

    /**
     * If current periodicity value is equals to NON_PERIODICITY this methods will be get
     * periodicity value from DB.
     */
    public void scheduleKPICalculus() {

        Runnable task = () -> {
            calculateKPI();
            rescheduleKPICalculus();
        };
        schedule(task);
    }

    /**
     * Schedule again KPI calculus only if it is necessary (if the parameter in DB has
     * <b>changed</b>).<br>
     * <br>
     * <b>Postcondition:</b> if parameter in DB has changed {@link #currentTask} will be canceled
     * and {@link #scheduleKPICalculus} is recalled.
     * 
     * @see co.edu.icesi.aviomFE.evaluator.services.EvalParametersService#periodicityHasChanged
     */
    public void rescheduleKPICalculus() {

        if (parameterService.periodicityHasChanged()) {
            currentTask.cancel(true);
            scheduleKPICalculus();
        }
    }

    public void schedule(Runnable task) {
        // Scheduled using a crontab expressions (See Quartz Framework documentation).
        // cronPeriodicityExpression is defined in the configuration class
        long periodicity = (long) parameterService.getLocalPeriodicity();
        if (periodicity > 0) {
            String cronExpression = String.format(CRON_FORMAT_BY_MINTES, periodicity);
            currentTask = scheduler.schedule(task, new CronTrigger(cronExpression));
        } else {
            if (currentTask != null) {
                this.currentTask.cancel(true);
            }
        }
    }

    public void calculateKPI() {
        try {
            expressionService.calculateKPI();
        } catch (Exception e) {
            log.error(getLogCurrentHeader() + "\nIniciando calculo de KPI", e);
        }

    }

    private String getLogCurrentHeader() {
        return dateTimeFormatter.format(LocalDateTime.now());
    }

}
