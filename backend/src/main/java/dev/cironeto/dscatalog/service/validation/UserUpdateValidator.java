package dev.cironeto.dscatalog.service.validation;

import dev.cironeto.dscatalog.dto.UserUpdateDto;
import dev.cironeto.dscatalog.entity.User;
import dev.cironeto.dscatalog.repository.UserRepository;
import dev.cironeto.dscatalog.resource.exception.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDto> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDto dto, ConstraintValidatorContext context) {

        var requestAttribute = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userToUpdateId = Long.parseLong(requestAttribute.get("id"));

        List<FieldMessage> fieldMessageList = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail());

        if(user != null && userToUpdateId != user.getId()){
            fieldMessageList.add(new FieldMessage("email", "Email already exists"));
        }

        for (FieldMessage e : fieldMessageList) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return fieldMessageList.isEmpty();
    }
}
