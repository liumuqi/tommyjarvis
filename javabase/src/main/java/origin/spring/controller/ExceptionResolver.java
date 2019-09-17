package origin.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = CommonMapping.class)
public class ExceptionResolver extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<RespJson<Integer>> handleException(HttpServletRequest req, Exception ex) {
        RespJson<Integer> rj = new RespJson<>();
        rj.setData(0);
        if (ex instanceof MissingServletRequestParameterException) {
            rj.setCode(HttpStatus.BAD_REQUEST.value());
            rj.setMessage("service error, check your params," + ex.getMessage());
            return new ResponseEntity<>(rj, HttpStatus.OK);
        } else if (ex instanceof ConstraintViolationException) {
            List<String> mes = ((ConstraintViolationException) ex).getConstraintViolations().stream().map(c -> c.getMessage()).collect(Collectors.toList());
            rj.setCode(HttpStatus.BAD_REQUEST.value());
            rj.setMessage("input error=> " + Arrays.deepToString(mes.toArray()));
            return new ResponseEntity<>(rj, HttpStatus.OK);
        } else {
            rj.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            rj.setMessage("service error=> " + ex.getMessage());
            return new ResponseEntity<>(rj, HttpStatus.OK);
        }
    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Object processValidationError(MethodArgumentNotValidException ex) {
//        BindingResult result = ex.getBindingResult();
//        List<FieldError> fieldErrors = result.getFieldErrors();
//        return null;
//    }
}
