package account.services;


import account.entities.responseentities.LoggingResponse;
import account.logging.LogInfoAggregator;
import account.logging.LoggerEntity;
import account.logging.LoggingActions;
import account.repositories.LoggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.LoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoggerService {

    @Autowired
    private final LoggerRepository loggerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerService.class);


    public LoggerService(LoggerRepository loggerRepository) {
        this.loggerRepository = loggerRepository;
    }



    public List<LoggingResponse> getLoggedEvents() {

        List<LoggerEntity> loggedEvents = loggerRepository.findAll();
        List<LoggingResponse> loggingResponses = new ArrayList<>();
        loggedEvents.forEach(s -> loggingResponses
                .add(new LoggingResponse(s.getAction().name(),
                        s.getSubject(), s.getObject(), s.getPath())) );

        return  loggingResponses;
    }


    public  void processLogEvents(LoggingActions loggingAction) {

        LoggerEntity logger = switch (loggingAction) {
            case CREATE_USER -> prepareCreatedUserLog(loggingAction);
            case CHANGE_PASSWORD -> prepareChangePasswordLog(loggingAction);
            case LOGIN_FAILED, ACCESS_DENIED, BRUTE_FORCE -> prepareExceptionLog(loggingAction);
            case GRANT_ROLE, REMOVE_ROLE -> prepareRoleChangeLog(loggingAction);
            case LOCK_USER, UNLOCK_USER -> prepareUserLockLog(loggingAction);
            case DELETE_USER -> prepareDeletedUserLog(loggingAction);

        };

        LOGGER.info("This is saved to logger {}", logger);
        loggerRepository.save(logger);
        LogInfoAggregator.removeThreads();


    }

    private LoggerEntity prepareDeletedUserLog(LoggingActions loggingAction) {
        return new LoggerEntity.Builder()
                .date(LocalDateTime.now().toString())
                .action(loggingAction)
                .subject(LogInfoAggregator.getUserInfo())
                .object(LogInfoAggregator.getObectInfo())
                .path(LogInfoAggregator.getUrlPath()).build();
    }

    private LoggerEntity prepareRoleChangeLog(LoggingActions loggingAction) {
        String[] parts = LogInfoAggregator.getObectInfo().split(" ");
        String setObjectInfo = prepareObjectInfo(loggingAction, parts);

        return new LoggerEntity.Builder()
                .date(LocalDateTime.now().toString())
                .action(loggingAction)
                .subject(LogInfoAggregator.getUserInfo())
                .object(setObjectInfo)
                .path(LogInfoAggregator.getUrlPath()).build();
    }

    private String prepareObjectInfo(LoggingActions loggingAction, String[] parts) {
        String subjectName = parts[0];
        String objectName = parts[1];
        String prepareObjectName = loggingAction.name().equals("REMOVE_ROLE") ?
                "Remove" : "Grant";

        String setObjectInfo;
        if (prepareObjectName.equals("Remove")) {
            setObjectInfo = prepareObjectName + " role " + objectName + " from " + subjectName.toLowerCase();
        } else {
            setObjectInfo = prepareObjectName + " role " + objectName + " to " + subjectName.toLowerCase();

        }
        return setObjectInfo;
    }

    private LoggerEntity prepareExceptionLog(LoggingActions loggingAction) {
       String url = LogInfoAggregator.getUrlPath()
               .replaceAll("/[a-zA-Z]*@.*\\.com", "");

       return new LoggerEntity.Builder()
                .date(LocalDateTime.now().toString())
                .action(loggingAction)
                .subject(LogInfoAggregator.getUserInfo())
                .object(url)
                .path(url).build();
   }

   private LoggerEntity prepareCreatedUserLog(LoggingActions loggingAction) {
       return new LoggerEntity.Builder()
               .date(LocalDateTime.now().toString())
               .action(loggingAction)
               .subject("Anonymous")
               .object(LogInfoAggregator.getObectInfo())
               .path(LogInfoAggregator.getUrlPath()).build();
   }

   private LoggerEntity prepareChangePasswordLog(LoggingActions loggingAction) {
       return new LoggerEntity.Builder()
               .date(LocalDateTime.now().toString())
               .action(loggingAction)
               .subject(LogInfoAggregator.getUserInfo())
               .object(LogInfoAggregator.getUserInfo())
               .path(LogInfoAggregator.getUrlPath()).build();
   }

   private LoggerEntity prepareUserLockLog(LoggingActions loggingAction) {
        String objectString;

        if (loggingAction.name().equals("LOCK_USER")) {
            objectString = "Lock user " + LogInfoAggregator.getObectInfo();
        } else {
            objectString = "Unlock user " + LogInfoAggregator.getObectInfo();
        }

       return new LoggerEntity.Builder()
               .date(LocalDateTime.now().toString())
               .action(loggingAction)
               .subject(LogInfoAggregator.getUserInfo())
               .object(objectString)
               .path(LogInfoAggregator.getUrlPath()).build();
   }


}