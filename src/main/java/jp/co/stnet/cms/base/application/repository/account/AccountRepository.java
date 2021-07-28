package jp.co.stnet.cms.base.application.repository.account;


import jp.co.stnet.cms.base.domain.model.authentication.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Account�̃��|�W�g��.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * API�L�[�Ń��[�U�A�J�E���g���擾
     *
     * @param apiKey API�L�[
     * @return Account
     */
    Account findByApiKey(String apiKey);

}
