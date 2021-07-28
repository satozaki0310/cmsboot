package jp.co.stnet.cms.base.presentation.controller.admin.accesscounter;

import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.authentication.Permission;
import jp.co.stnet.cms.common.constant.Constants;
import lombok.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class AccessCounterAuthority {

    // �����ꂽOperation
    private static final Set<String> allowedOperation = Set.of(
            Constants.OPERATION.UPDATE,
            Constants.OPERATION.DELETE,
            Constants.OPERATION.SAVE,
            Constants.OPERATION.INVALID,
            Constants.OPERATION.VALID,
            Constants.OPERATION.VIEW,
            Constants.OPERATION.LIST
    );

    /**
     * �����`�F�b�N���s���B
     *
     * @param operation    ����̎��(Constants.OPERATION�ɓo�^���ꂽ�l)
     * @param loggedInUser ���O�C�����[�U���
     * @return true=���삷�錠��������, false=���삷�錠���Ȃ�
     * @throws AccessDeniedException    @PostAuthorize��p����false���ɃX���[
     * @throws IllegalArgumentException �s����Operation���w�肳�ꂽ�ꍇ
     * @throws NullPointerException     operation, loggedInUser ��null�̏ꍇ
     */
    @PostAuthorize("returnObject == true")
    public Boolean hasAuthority(String operation, LoggedInUser loggedInUser) {
        return hasAuthorityWOException(operation, loggedInUser);
    }

    /**
     * �����̗L���`�F�b�N
     *
     * @param operation    ����̎��(Constants.OPERATION�ɓo�^���ꂽ�l)
     * @param loggedInUser ���O�C�����[�U���
     * @return true=���삷�錠��������, false=�����������Ȃ�
     * @throws IllegalArgumentException �s����Operation���w�肳�ꂽ�ꍇ
     * @throws NullPointerException     operation, loggedInUser ��null�̏ꍇ
     */
    public Boolean hasAuthorityWOException(@NonNull String operation, @NonNull LoggedInUser loggedInUser) {

        // ���̓`�F�b�N
        validate(operation);

        Collection<GrantedAuthority> authorities = loggedInUser.getAuthorities();
        if (authorities == null) {
            return false;
        }

        // �ҏW��ʂ��J��
        else if (Constants.OPERATION.UPDATE.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        // �ۑ�
        else if (Constants.OPERATION.SAVE.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        // ����
        else if (Constants.OPERATION.INVALID.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        // ��������
        else if (Constants.OPERATION.VALID.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        // �폜
        else if (Constants.OPERATION.DELETE.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        // �ꗗ���J��
        else if (Constants.OPERATION.LIST.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.DOC_MAN_LIST.name()));
        }


        // �Q��
        else if (Constants.OPERATION.VIEW.equals(operation)) {
            return authorities.contains(new SimpleGrantedAuthority(Permission.ADMIN_ACCESS_COUNTER.name()));
        }

        return false;
    }

    /**
     * �����ꂽOperation��
     *
     * @param operation �����\���萔
     */
    private void validate(String operation) {
        if (!allowedOperation.contains(operation)) {
            throw new IllegalArgumentException("Operation not allowed.");
        }
    }
}
