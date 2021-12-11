package dev.cironeto.dscatalog.service.validation;

import dev.cironeto.dscatalog.dto.UserInsertDto;
import dev.cironeto.dscatalog.entity.User;
import dev.cironeto.dscatalog.repository.UserRepository;
import dev.cironeto.dscatalog.resource.exception.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDto> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDto dto, ConstraintValidatorContext context) {

        List<FieldMessage> fieldMessageList = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail());
        if(user != null){
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
