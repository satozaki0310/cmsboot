package jp.co.stnet.cms.base.application.service.workflow;

import jp.co.stnet.cms.base.application.repository.workflow.EmpStepStatus;
import jp.co.stnet.cms.base.application.repository.workflow.StepInfo;
import jp.co.stnet.cms.base.application.repository.workflow.WorkflowRepository;
import jp.co.stnet.cms.base.domain.model.workflow.Workflow;
import jp.co.stnet.cms.common.exception.InvalidArgumentBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.terasoluna.gfw.common.message.ResultMessages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkflowServiceImpl implements WorkflowService {

    private static final int STATUS_INIT = 0; // ������
    private static final int STATUS_WORKING = 1; // �Ή���
    private static final int STATUS_COMPLETE = 2; // ����
    private static final int STATUS_REJECT = 3; // ���߂�
    private static final int STATUS_PULLBACK = 4; // ���߂�

    private static final String GO_NEXT = "goNext";
    private static final String RETURN_PREV = "returnPrev";
    private static final String PULL_BACK = "pullBack";
    private static final String INITIALIZE = "initialize";


    private static final int FIRST_STEP_NO = 1;
    private static final int LAST_STEP_NO = 10;


    // �X�e�[�^�X�̗D�揇��(2:����->3:����->4:����->1:�Ή���->0:���Ή�)
    private static final Map<Integer, Integer> STATUS_ORDER = new LinkedHashMap<Integer, Integer>() {{
        put(STATUS_COMPLETE, 1);
        put(STATUS_REJECT, 2);
        put(STATUS_PULLBACK, 3);
        put(STATUS_WORKING, 4);
        put(STATUS_INIT, 5);
    }};

    private static final List<String> OPERATIONS = new ArrayList<String>() {{
        add(GO_NEXT);
        add(RETURN_PREV);
        add(PULL_BACK);
        add(INITIALIZE);
    }};

    @Autowired
    WorkflowRepository workflowRepository;


    @Override
    public void init(String entityType, Long entityId) {

        // �ۑ�����Ă���f�[�^�̍폜
        workflowRepository.deleteAll(
                workflowRepository.findAll(Example.of(Workflow.builder()
                        .entityType(entityType)
                        .entityId(entityId)
                        .build())));

        // �V�K�f�[�^�̑}��
        List<Workflow> newRecords = new ArrayList<>();
        for (int i = FIRST_STEP_NO; i <= LAST_STEP_NO; i++) {
            newRecords.add(Workflow.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .stepNo(i)
                    .employeeId(getEmployeeIdByStep(entityType, entityId, i))
                    .stepStatus(STATUS_INIT)
                    .build());
        }
        workflowRepository.saveAll(newRecords);

    }

    @Override
    public void goNextStep(String entityType, Long entityId, Long employeeId) {

        if (!canGoNextStep(entityType, entityId, employeeId)) {
            throw new InvalidArgumentBusinessException(ResultMessages.error().add("���쌠��������܂���B"));
        }

        StepInfo stepInfo = getStepInfo(entityType, entityId);

        // ���݂̃X�e�b�v�̃X�e�[�^�X�������ɕύX
        List<Workflow> workflowList = workflowRepository.findAll(Example.of(Workflow.builder()
                .entityType(entityType)
                .entityId(entityId)
                .stepNo(stepInfo.getCurrentStepNo())
                .employeeId(employeeId)
                .build()));
        if (workflowList.size() > 0) {
            workflowList.get(0).setStepStatus(STATUS_COMPLETE);
        }

        // ���̃X�e�b�v�����݂���ꍇ
        if (stepInfo.getCurrentStepNo() < LAST_STEP_NO) {
            // ���̃X�e�b�v�̃X�e�[�^�X��Ή����ɕύX
            List<Workflow> workflowList2 = workflowRepository.findAll(Example.of(Workflow.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .stepNo(stepInfo.getCurrentStepNo() + 1)
                    .build()));
            workflowList2.iterator().next().setStepStatus(STATUS_WORKING);
        }
    }

    @Override
    public boolean canGoNextStep(String entityType, Long entityId, Long employeeId) {
        return this.canOperations(GO_NEXT, entityType, entityId, employeeId);
    }

    @Override
    public void returnPrevStep(String entityType, Long entityId, Long employeeId) {

        if (!canReturnPrevStep(entityType, entityId, employeeId)) {
            throw new InvalidArgumentBusinessException(ResultMessages.error().add("���쌠��������܂���B"));
        }

        StepInfo stepInfo = getStepInfo(entityType, entityId);

        // ���݂̃X�e�b�v�̃X�e�[�^�X���u�����v�ɕύX
        List<Workflow> workflowList = workflowRepository.findAll(Example.of(Workflow.builder()
                .entityType(entityType)
                .entityId(entityId)
                .stepNo(stepInfo.getCurrentStepNo())
                .employeeId(employeeId)
                .build()));
        if (workflowList.size() > 0) {
            workflowList.get(0).setStepStatus(STATUS_WORKING);
        }

        // �O�̃X�e�b�v�̃X�e�[�^�X���u���߂��v�ɕύX
        List<Workflow> workflowList2 = workflowRepository.findAll(Example.of(Workflow.builder()
                .entityType(entityType)
                .entityId(entityId)
                .stepNo(stepInfo.getCurrentStepNo() - 1)
                .build()));
        workflowList2.iterator().next().setStepStatus(STATUS_REJECT);
    }

    @Override
    public boolean canReturnPrevStep(String entityType, Long entityId, Long employeeId) {
        return this.canOperations(RETURN_PREV, entityType, entityId, employeeId);
    }

    @Override
    public void pullbackStep(String entityType, Long entityId, Long employeeId) {

        if (!canPullbackStep(entityType, entityId, employeeId)) {
            throw new InvalidArgumentBusinessException(ResultMessages.error().add("���쌠��������܂���B"));
        }

        StepInfo stepInfo = getStepInfo(entityType, entityId);

        if (stepInfo.isCurrentIsLast() && stepInfo.getCurrentStepStatus() == STATUS_COMPLETE) {
            // ���݂̃X�e�b�v�̃X�e�[�^�X���u���߂��v�ɕύX
            List<Workflow> workflowList = workflowRepository.findAll(Example.of(Workflow.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .stepNo(stepInfo.getCurrentStepNo())
                    .employeeId(employeeId)
                    .build()));
            if (workflowList.size() > 0) {
                workflowList.get(0).setStepStatus(STATUS_PULLBACK);
            }

        } else {
            // �O�̃X�e�b�v�̃X�e�[�^�X���u���߂��v�ɕύX
            List<Workflow> workflowList = workflowRepository.findAll(Example.of(Workflow.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .stepNo(stepInfo.getCurrentStepNo() - 1)
                    .employeeId(employeeId)
                    .build()));
            workflowList.iterator().next().setStepStatus(STATUS_PULLBACK); //�ꌏ�����Ȃ��͂�

            // ���݂̃X�e�b�v�̃X�e�[�^�X���u�������v�ɕύX
            List<Workflow> workflowList2 = workflowRepository.findAll(Example.of(Workflow.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .stepNo(stepInfo.getCurrentStepNo())
                    .build()));
            workflowList2.iterator().next().setStepStatus(STATUS_INIT);
        }
    }

    @Override
    public boolean canPullbackStep(String entityType, Long entityId, Long employeeId) {
        return this.canOperations(PULL_BACK, entityType, entityId, employeeId);
    }

    @Override
    public Workflow getCurrentStepInfo(String entityType, Long entityId) {


        return null;
    }

    @Override
    public List<Workflow> getAllStepInfo(String entityType, Long entityId) {
        return null;
    }

    @Override
    public StepInfo getStepInfo(String entityType, Long entityId) {
        return null;
    }

    /**
     * ���ƈ��ԍ��̏����l�����߂�
     *
     * @param entityType �G���e�B�e�B�^�C�v
     * @param entityId   �G���e�B�e�BID
     * @param step_no    �X�e�b�v�ԍ�
     * @return
     */
    protected Long getEmployeeIdByStep(String entityType, Long entityId, int step_no) {
        // TODO
        return 1L;
    }


    /**
     * ���쌠�����m�F����B
     *
     * @param op
     * @param entityType
     * @param entityId
     * @param employeeId
     * @return
     */
    protected boolean canOperations(String op, String entityType, Long entityId, Long employeeId) {

        if (!OPERATIONS.contains(op)) {
            throw new IllegalArgumentException("op = " + op);
        }

        if (entityType == null) {
            throw new IllegalArgumentException("entityType is null ");
        }

        if (entityId == null) {
            throw new IllegalArgumentException("entityId is null");
        }

        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is null");
        }

        // TODO �����̌����ȃ`�F�b�N


        // TODO �G���g���V�[�g�S�̂��m��(�ύX�s�\���)�̏ꍇ�A���false��Ԃ��B


        // ���݂̃X�e�b�v�����擾.
        StepInfo stepInfo = this.getStepInfo(entityType, entityId);
        if (stepInfo == null) {
            return false;
        }

        if (op.equals(GO_NEXT)) {
            // ���X�e�b�v�̒S���҂ɑΏێ҂��܂܂�A���̃X�e�[�^�X�������ȊO�̏ꍇ.
            for (EmpStepStatus s : stepInfo.getCurrentStepEmployees()) {
                if (s.getEmployeeId().equals(employeeId) && s.getStepStatus() != STATUS_COMPLETE) {
                    return true;
                }
            }

        } else if (op.equals(RETURN_PREV)) {
            // ���X�e�b�v���P�łȂ�
            if (stepInfo.getCurrentStepNo() == 1) {
                return false;
            }

            // ���X�e�b�v�̒S���҂ɑΏێ҂��܂܂�A���̃X�e�[�^�X�������ȊO�̏ꍇ.
            for (EmpStepStatus s : stepInfo.getCurrentStepEmployees()) {
                if (s.getEmployeeId().equals(employeeId) && s.getStepStatus() != STATUS_COMPLETE) {
                    return true;
                }
            }

        } else if (op.equals(PULL_BACK)) {
            // �ŏI�X�e�b�v�������̏ꍇ
            if (stepInfo.isCurrentIsLast() && stepInfo.getCurrentStepStatus() == STATUS_COMPLETE) {
                return true;
            }
            // ���X�e�b�v?�P�̒S���҂ɑΏێ҂��܂܂�A���X�e�b�v�̃X�e�[�^�X��������.
            for (EmpStepStatus s : stepInfo.getPrevStepEmployees()) {
                if (s.getEmployeeId().equals(employeeId) && stepInfo.getCurrentStepStatus() == STATUS_INIT) {
                    return true;
                }
            }
        }

        return false;
    }


}
