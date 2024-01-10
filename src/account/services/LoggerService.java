package account.services;


import account.entities.LogInfoAggregator;
import account.entities.LoggerEntity;
import account.entities.enums.LoggingActions;
import account.entities.responseentities.GrantAndRemoveEntity;
import account.repositories.LoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggerService {

    @Autowired
    private final LoggerRepository loggerRepository;

    public LoggerService(LoggerRepository loggerRepository) {
        this.loggerRepository = loggerRepository;
    }


    public  void processLogEvents(LoggingActions loggingAction) {
        LoggerEntity logger = switch (loggingAction) {
            case CREATE_USER -> null;
            case CHANGE_PASSWORD -> prepareChangePasswordLog(loggingAction);
            case LOGIN_FAILED, ACCESS_DENIED, BRUTE_FORCE -> prepareExceptionLog(loggingAction);
            case GRANT_ROLE, REMOVE_ROLE, DELETE_USER -> null;
            case LOCK_USER, UNLOCK_USER -> null;

        };
    }

   private LoggerEntity prepareExceptionLog(LoggingActions loggingAction) {
        return new LoggerEntity.Builder()
                .date(LocalDateTime.now().toString())
                .action(loggingAction)
                .subject(LogInfoAggregator.getUserInfo())
                .object(LogInfoAggregator.getObectInfo())
                .path(LogInfoAggregator.getUrlPath()).build();
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

   }





}


