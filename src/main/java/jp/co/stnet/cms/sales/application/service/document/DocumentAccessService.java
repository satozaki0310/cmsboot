package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.sales.domain.model.document.DocumentAccess;

public interface DocumentAccessService {

    /**
     * �h�L�������g�Q�Ɖ�ʂւ̃A�N�Z�X���L�^
     *
     * @param documentId �h�L�������g��ID
     * @param username   ���[�U��
     * @return �ۑ������G���e�B�e�B
     */
    DocumentAccess save(Long documentId, String username);

}
