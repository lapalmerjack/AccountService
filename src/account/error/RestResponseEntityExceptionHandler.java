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
            AuthenticationException.class,
           })
    @ResponseBody
    public ResponseEntity<ErrorMessageTemplate> handleUnauthenticatedException(WebRequest webRequest,
                                                                               RuntimeException e) {

        LOGGER.info("preparing bad credentials for authentication");

        String urlPath = getUrlPath(webRequest);

     

        ErrorMessageTemplate errorMessage = new ErrorMessageTemplate(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                e.getMessage(),
                urlPath);

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);

    }



    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        logger.info("Preparing Bad Exception");
        logger.error(ex.getCause() + ": " + ex.getMessage());
        String urlPath = getUrlPath(request);
        if (ex.getMessage().contains("values accepted for Enum class: [GRANT, REMOVE]")) {
            OperationDoesNotExistException operationDoesNotExistException = new OperationDoesNotExistException();
            ErrorMessageTemplate errorMessage = new ErrorMessageTemplate(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    operationDoesNotExistException.getMessage(),
                    urlPath);

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
           })
    public ResponseEntity<ErrorMessageTemplate> HandlingServiceRuntimeExceptions(
            RuntimeException e, WebRequest webRequest
    ) {
        logger.info("Preparing Bad Exception");
        logger.error(e.getCause() + ": " + e.getMessage());

        String urlPath = getUrlPath(webRequest);

        ErrorMessageTemplate errorMessage = new ErrorMessageTemplate(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                urlPath);

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler({OperationDoesNotExistException.class, UserNotFoundException.class,
            RoleDoesNotExist.class})
    public ResponseEntity<ErrorMessageTemplate> handleUserNotFoundException(
            RuntimeException e, WebRequest webRequest
    ) {
        logger.info("Preparing exception");
        logger.error(e.getCause() + ": " + e.getMessage());

        String urlPath = getUrlPath(webRequest);

        ErrorMessageTemplate errorMessage = new ErrorMessageTemplate(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                urlPath);

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

        String urlPath = getUrlPath(request);

        ErrorMessageTemplate errorMessage = new ErrorMessageTemplate(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessageString,
                urlPath);


        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }



    private String getUrlPath(WebRequest path) {

        logger.info("providing the url path");
        String urlPath = path.getDescription(false);

        return urlPath.substring(urlPath.indexOf("/"));

    }




}