package account.services;


import account.entities.responseentities.LoggingResponse;
import account.logging.LogInfoAggregator;
import account.logging.LoggerEntity;
import account.logging.LoggingActions;
import account.repositories.LoggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoggerService {

    private static Optional<String> username;
    private static Optional<String> urlPath;

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
            case LOGIN_FAILED, ACCESS_DENIED, BRUTE_FORCE -> prepareLoginFailedLog(loggingAction);
            case GRANT_ROLE, REMOVE_ROLE -> prepareRoleChangeLog(loggingAction);
            case LOCK_USER -> prepareUserLockLog(loggingAction);
            case UNLOCK_USER -> prepareUserUnlockLog(loggingAction);
            case DELETE_USER -> prepareDeletedUserLog(loggingAction);


        };

        username = Optional.of(logger.getSubject());
        urlPath = Optional.of(logger.getPath());

        LOGGER.info("This is saved to logger {}", logger);
        loggerRepository.save(logger);
        LOGGER.info("Removing threads");
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

    private LoggerEntity prepareLoginFailedLog(LoggingActions loggingAction) {

        String correctSubject = LogInfoAggregator.getUserInfo();
        String url = LogInfoAggregator.getUrlPath();
        if (loggingAction == LoggingActions.BRUTE_FORCE) {
            System.out.println("UP IN HERE MY MAN");
            correctSubject = username.get();
            url = urlPath.get();
        }

        return new LoggerEntity.Builder()
                .date(LocalDateTime.now().toString())
                .action(loggingAction)
                .subject(correctSubject)
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
        String correctSubject = username.orElse(LogInfoAggregator.getUserInfo());
        String objectString = "Lock user " + correctSubject;
        String url = urlPath.orElse(LogInfoAggregator.getUrlPath());


       return new LoggerEntity.Builder()
               .date(LocalDateTime.now().toString())
               .action(loggingAction)
               .subject(correctSubject)
               .object(objectString)
               .path(url).build();
   }

   private LoggerEntity prepareUserUnlockLog(LoggingActions loggingAction) {
    String objectString = "Unlock user " + LogInfoAggregator.getObectInfo();

       return new LoggerEntity.Builder()
               .date(LocalDateTime.now().toString())
               .action(loggingAction)
               .subject(LogInfoAggregator.getUserInfo())
               .object(objectString)
               .path(LogInfoAggregator.getUrlPath()).build();

   }


}