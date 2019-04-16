package origin.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
<!--校验器 ，LocalValidatorFactoryBean是spring提供的一个校验接口-->
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
    <!-- hibernate校验器 -->
    <property name="providerClass" value="org.hibernate.validator.HibernateValidator" />
    <!-- 指定校验使用的资源文件，在文件中配置校验错误信息，如果不指定则默认使用classpath下的ValidationMessages.properties -->
    <property name="validationMessageSource" ref="messageSource" />
</bean>
<!-- 校验错误信息配置文件 -->
<bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
    <!-- 资源文件名 -->
    <property name="basename" value="classpath:ValidationMessages"/>
    <property name="useCodeAsDefaultMessage" value="true" />
    <!-- 资源文件编码格式 -->
    <property name="fileEncodings" value="utf-8" />
</bean>
<bean class="org.springframework.validation.beanvalidation.MethodValidationPostProcessor"/>


配置ValidationMessages.properties文件，文件名一定要是ValidationMessages，使用其他名字不能读取到，也不知是何原因
NotNull.insureComCode=供应商代码不能为空
Size.transId=transId长度不能超过{max}
NotEmpty.transId=transId不允许空
NotNull.transId=transId不允许NULL
NotEmpty.partnerCode=partnerCode不允许空
NotNull.partnerCode=partnerCode不允许NULL
NotEmpty.operationCode=operationCode不允许空
NotNull.operationCode=operationCode不允许空
NotNull.cityCode=城市代码cityCode不允许空
NotNull.transferFlag=transferFlag不允许空
NotNull.provCode=provCode不允许空
NotNull.noLicenseFlag=noLicenseFlag不允许空

public class RestFulUtil {
    public static <T> RestResponse<T> toCarSuccessResponse(CarMultiRespWrapper carMultiRespWrapper) {
        RestResponse.Builder builder = new RestResponse.Builder();
        builder.setStatus(HttpStatus.OK.value());
        builder.setCode(Constants.CAR_INSURE_SUCCEED.intValue());
        builder.setMessage(CarConstants.CAR_MSG_INSURE_SUCCESS);
        builder.setMessageDetail("");

        if (carMultiRespWrapper != null) {
            builder.setData(carMultiRespWrapper);
        }
        return builder.build();
    }

    public static String getConstraintViolationExMsg(ConstraintViolationException ex) {
        List<FieldError> errors = FieldError.getErrors(ex.getConstraintViolations());
        String errorMsg = "";
        for (FieldError error : errors) {
            errorMsg += error.getMessage() + ";";
        }
        return errorMsg;
    }
}

public class FieldError {
    private String field;
    private String code;
    private String message;

    // Getter Setter Method

    public static List<FieldError> getErrors(Set<ConstraintViolation<?>> constraintViolations) {
        List<FieldError> list = new ArrayList<>();
        for(ConstraintViolation<?> constraintViolation : constraintViolations) {
            list.add(of(constraintViolation));
        }
        return list;
    }

    private static FieldError of(ConstraintViolation<?> constraintViolation) {
        String field = StringUtils.substringAfter(constraintViolation.getPropertyPath().toString(), ".");
        return new FieldError(field, constraintViolation.getMessageTemplate(), constraintViolation.getMessage());
    }
}

 */
public class VinNoValidator implements ConstraintValidator<VinNo, String> {

    private String regexp;

    @Override
    public void initialize(VinNo vinNo) {
        this.regexp = vinNo.regexp();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        if (s.matches(regexp)) {
            return true;
        }
        return false;
    }
}

