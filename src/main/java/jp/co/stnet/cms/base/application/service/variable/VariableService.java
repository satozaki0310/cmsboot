package jp.co.stnet.cms.base.application.service.variable;


import jp.co.stnet.cms.base.application.service.NodeIService;
import jp.co.stnet.cms.base.domain.model.variable.Variable;

import java.util.List;

/**
 * VariableService
 */
public interface VariableService extends NodeIService<Variable, Long> {

    /**
     * �^�C�v�Ō�������B
     *
     * @param type �^�C�v
     * @return �q�b�g�����f�[�^�̃��X�g
     */
    List<Variable> findAllByType(String type);

    /**
     * �^�C�v�ƃR�[�h�Ō�������B
     *
     * @param type �^�C�v
     * @param code �R�[�h
     * @return �q�b�g�����f�[�^�̃��X�g
     */
    List<Variable> findAllByTypeAndCode(String type, String code);

    /**
     * �^�C�v��valueX�̒l�Ō�������B(X = 1 ? 10)
     *
     * @param type  �^�C�v
     * @param i     valueX��X����������(1 - 10)
     * @param value valueX�̒l
     * @return �q�b�g�����f�[�^�̃��X�g
     */
    List<Variable> findAllByTypeAndValueX(String type, int i, String value);

}
