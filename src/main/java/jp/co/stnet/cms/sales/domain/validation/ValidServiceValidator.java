package jp.co.stnet.cms.sales.domain.validation;

import jp.co.stnet.cms.base.application.service.variable.VariableService;
import jp.co.stnet.cms.base.domain.model.variable.VariableType;
import jp.co.stnet.cms.sales.presentation.controller.document.DocumentForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ���Ɨ̈��I��������A�T�[�r�X��ʂƃT�[�r�X(�I����������ꍇ)��K�{�Ƃ���B
 */
public class ValidServiceValidator implements ConstraintValidator<ValidService, Object> {

    @Autowired
    VariableService variableService;

    private ValidService validService;

    private String message;

    @Override
    public void initialize(ValidService constraintAnnotation) {
        this.validService = constraintAnnotation;
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        boolean result = true;

        DocumentForm documentForm = (DocumentForm) value;

        context.disableDefaultConstraintViolation();

        // ���Ɨ̈�ɒl���ݒ肳�ꂽ�ꍇ�A�T�[�r�X��ʁE�T�[�r�X�̕K�{�`�F�b�N���s���B
        if (documentForm.getDocService1() != null) {

            // �T�[�r�X��ʂ̕K�{�`�F�b�N
            if (documentForm.getDocService2() == null) {
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode("docService2")
                        .addConstraintViolation();
                result = false;
            }

            // �T�[�r�X��ʂ̒l�ɕR�Â��T�[�r�X�̗L�����m�F���A�I���\�ȃT�[�r�X������ꍇ�K�{
            if (!variableService.findAllByTypeAndValueX(VariableType.DOC_SERVICE3.name(), 2, documentForm.getDocService2()).isEmpty()) {
                if (documentForm.getDocService3() == null) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addPropertyNode("docService3")
                            .addConstraintViolation();
                    result = false;
                }
            }

        }
        return result;
    }


}
