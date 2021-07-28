package jp.co.stnet.cms.base.application.service.authentication;

import jp.co.stnet.cms.base.domain.model.authentication.PermissionRole;

import java.util.Collection;
import java.util.List;

/**
 * PermissionRoleSharedService
 */
public interface PermissionRoleService {

    /**
     * ���[���̃R���N�V�����Ō�������B
     *
     * @param roleIds ���[���̃R���N�V����
     * @return �q�b�g�����f�[�^�̃��X�g
     */
    List<PermissionRole> findAllByRole(Collection<String> roleIds);

}
