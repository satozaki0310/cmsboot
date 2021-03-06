package jp.co.stnet.cms.sales.application.repository.document;

import jp.co.stnet.cms.base.application.repository.NodeRevRepository;
import jp.co.stnet.cms.base.domain.model.authentication.Account;
import jp.co.stnet.cms.sales.domain.model.document.Document;
import jp.co.stnet.cms.sales.domain.model.document.DocumentRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DocumentRevisionRepository extends NodeRevRepository<DocumentRevision, Long> {

    @Query("SELECT c FROM DocumentRevision c INNER JOIN DocumentMaxRev m ON m.rid = c.rid AND c.revType < 2 WHERE m.id = :id")
    DocumentRevision findByIdLatestRev(@Param("id") Long id);

    /**
     * saveRevisionがtrueの変更履歴のみを取得する
     *
     * @param id           ドキュメントID
     * @param saveRevision 変更履歴のチェック有無
     * @param publicScope  公開区分
     * @return DocumentRevision型のリスト
     */
    Page<DocumentRevision> findByIdAndSaveRevisionAndPublicScopeIn(Long id, boolean saveRevision, Set<String> publicScope, Pageable pageable);

    /**
     * すべての変更履歴を取得する
     *
     * @param id          ドキュメントID ドキュメントID
     * @param publicScope 公開区分
     * @return DocumentRevision型のリスト
     */
    Page<DocumentRevision> findByIdAndPublicScopeIn(Long id, Set<String> publicScope, Pageable pageable);

    @Query("SELECT c FROM Account c WHERE username = :id")
    Account findPerson(@Param("id") String id);

    DocumentRevision findTopByIdAndPublicScopeInOrderByVersionDesc(Long id, Set<String> publicScope);

    Document findByIdAndPublicScopeIn(Long id, Set<String> publicScope);

}
