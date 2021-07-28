package jp.co.stnet.cms.base.application.service.workflow;


import jp.co.stnet.cms.base.application.repository.workflow.StepInfo;
import jp.co.stnet.cms.base.domain.model.workflow.Workflow;

import java.util.List;

public interface WorkflowService {

    /**
     * ���[�N�t���[�X�e�b�v������������.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     */
    void init(String entityType, Long entityId);

    /**
     * ���̃X�e�b�v�ɐi�߂�.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     */
    void goNextStep(String entityType, Long entityId, Long employeeId);

    /**
     * �u���̃X�e�b�v�ɐi�߂�v����̌����`�F�b�N.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     * @return TRUE: ����\, FALSE: �s��
     */
    boolean canGoNextStep(String entityType, Long entityId, Long employeeId);

    /**
     * �O�̃X�e�b�v�ɖ߂�.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     */
    void returnPrevStep(String entityType, Long entityId, Long employeeId);

    /**
     * �u�O�̃X�e�b�v�ɖ߂��v����̌����`�F�b�N.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     * @return TRUE: ����\, FALSE: �s��
     */
    boolean canReturnPrevStep(String entityType, Long entityId, Long employeeId);

    /**
     * �X�e�b�v�������߂�.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     */
    void pullbackStep(String entityType, Long entityId, Long employeeId);

    /**
     * �u�X�e�b�v�������߂��v����̌����`�F�b�N.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @param employeeId �]�ƈ�ID
     * @return TRUE: ����\, FALSE: �s��
     */
    boolean canPullbackStep(String entityType, Long entityId, Long employeeId);

    /**
     * ���݂̃X�e�b�v�̏����擾����.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @return �ΏۃG���e�B�e�B�̌��݂̃X�e�b�v�̏��
     */
    Workflow getCurrentStepInfo(String entityType, Long entityId);

    /**
     * �ΏۃG���e�B�e�B�̂��ׂẴX�e�b�v�̏����擾����.
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @return �ΏۃG���e�B�e�B�̑S�X�e�b�v���
     */
    List<Workflow> getAllStepInfo(String entityType, Long entityId);

    /**
     * �w��G���e�B�e�B�̃X�e�b�v�Ɋւ������Map�Ŏ擾����.
     * <p>
     * current_step_no        int ���݃X�e�b�v�ԍ�
     * current_step_status    int ���݃X�e�b�v�X�e�[�^�X(�ėD��)
     * current_step_employee  array(employee_id, employee_status, employee_weight)
     * ���݃X�e�b�v�̒S����(�]�ƈ��ԍ�)�ƃX�e�[�^�X
     * prev_step_employee     array(employee_id, employee_status, employee_weight)
     * �P�O�̃X�e�b�v�̒S����(�]�ƈ��ԍ�)�ƃX�e�[�^�X
     * max_step_no            int �ő�X�e�b�v�ԍ�
     * current_is_max         bool ���݃X�e�b�v���ő�X�e�b�v���H
     * </p>
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�B�̓���ID
     * @return �X�e�b�v�Ɋւ�������i�[����Map
     */
    StepInfo getStepInfo(String entityType, Long entityId);

}
