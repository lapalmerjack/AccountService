package account.error;


import account.error.customexceptions.admin.*;
import account.error.customexceptions.payments.NoExistingDatePeriodException;
import account.error.customexceptions.users.*;

import java.time.LocalDateTime;
import java.util.List;

import account.error.customexceptions.payments.ExistingDatePeriodException;
import account.error.customexceptions.payments.DateSyntaxWrongException;
import account.error.customexceptions.payments.SalaryBelowZeroException;
import org.h2.security.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.
            getLogger(RestResponseEntityExceptionHandler.class);


    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class,
            AuthenticationException.class, LockedException.class
           })
    @ResponseBody
    public ResponseEntity<ErrorMessageTemplate> handleUnauthenticatedException(WebRequest webRequest,
                                                                               RuntimeException e) {
        LOGGER.info("preparing bad credentials for authentication");

        ErrorMessageTemplate errorMessage = setUpErrorMessageTemplate(e.getMessage(), HttpStatus.UNAUTHORIZED.value(),
                webRequest, "Unauthorized");
     

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);

    }





    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {

        LOGGER.error("Preparing message not readable Exception");

        if (ex.getMessage().contains("[GRANT, REMOVE]")) {
            LOGGER.error("Preparing role does not exist exception");
            OperationDoesNotExistException operationDoesNotExistException = new OperationDoesNotExistException();
            ErrorMessageTemplate errorMessage = setUpErrorMessageTemplate(operationDoesNotExistException.getMessage(), HttpStatus.BAD_REQUEST.value(),
                    request, "Bad Request");

            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }


    @ExceptionHandler({NewPasswordMatchesOldPassword.class, UserFoundException.class,
            PasswordMatchesBannedPassword.class, MinimumPasswordLengthException.class,
            DateSyntaxWrongException.class, ExistingDatePeriodException.class,
            SalaryBelowZeroException.class, NoExistingDatePeriodException.class,
            UserRoleExists.class, UserCantDeleteItSelfException.class,
            RoleCombinationException.class, RoleAlreadyAssignedException.class,
            InsufficientRoleCountException.class, RoleDoesNotExistForUser.class,
            CanNotLockAdministratorException.class
           })
    public ResponseEntity<ErrorMessageTemplate> HandlingServiceRuntimeExceptions(
            RuntimeException e, WebRequest webRequest
    ) {
        logger.info("Preparing Bad Request Exception");
        logger.error(e.getCause() + ": " + e.getMessage());

        ErrorMessageTemplate errorMessage = setUpErrorMessageTemplate(e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                webRequest, "Bad Request");

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler({OperationDoesNotExistException.class, UserNotFoundException.class,
            RoleDoesNotExist.class})
    public ResponseEntity<ErrorMessageTemplate> handleUserNotFoundException(
            RuntimeException e, WebRequest webRequest
    ) {
        logger.info("Preparing exception");
        logger.error(e.getCause() + ": " + e.getMessage());

        ErrorMessageTemplate errorMessage = setUpErrorMessageTemplate(e.getMessage(), HttpStatus.NOT_FOUND.value(),
                webRequest, "Not Found");

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }



    private String processFieldErrors(List<FieldError> fieldErrors) {
        String errorMessage = null;
        for (org.springframework.validation.FieldError fieldError: fieldErrors) {
            errorMessage = fieldError.getDefaultMessage();
        }
        return errorMessage;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        BindingResult result = ex.getBindingResult();
        List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
        String errorMessageString = processFieldErrors(fieldErrors);

        logger.info("Handling methodArgumentNotValid");

      ErrorMessageTemplate errorMessage = setUpErrorMessageTemplate(errorMessageString,
              HttpStatus.BAD_REQUEST.value(), request, "Bad Request");

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    private ErrorMessageTemplate setUpErrorMessageTemplate(String errorMessage, int httpValue,
                                                           WebRequest webRequest, String error) {
        String urlPath = getUrlPath(webRequest);

        return new ErrorMessageTemplate(
                LocalDateTime.now(),
                httpValue,
                error,
                errorMessage,
                urlPath);
    }

    private String getUrlPath(WebRequest path) {
        logger.info("providing the url path");
        String urlPath = path.getDescription(false);

        return urlPath.substring(urlPath.indexOf("/"));

    }




}