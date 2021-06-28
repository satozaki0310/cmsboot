package jp.co.stnet.cms.example.application.repository.document;

import jp.co.stnet.cms.base.application.repository.NodeRevRepository;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import jp.co.stnet.cms.example.domain.model.person.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface DocumentRevisionRepository extends NodeRevRepository<DocumentRevision, Long> {

    @Query("SELECT c FROM DocumentRevision c INNER JOIN DocumentMaxRev m ON m.rid = c.rid AND c.revType < 2 WHERE m.id = :id")
    DocumentRevision findByIdLatestRev(@Param("id") Long id);

    /**
     * ドキュメントID、変更履歴チェックの有無、公開区分で検索する
     * @param id
     * @param saveRevision
     * @param publicScope
     * @return DocumentRevision型のリスト
     */
    List<DocumentRevision> findByIdAndSaveRevisionAndPublicScope(Long id, boolean saveRevision, Set<String> publicScope);

    /**
     * ドキュメントID、公開区分で検索する
     * @param id ドキュメントID
     * @param publicScope 公開区分
     * @return DocumentRevision型のリスト
     */
    List<DocumentRevision> findByIdAndPublicScope(Long id, Set<String> publicScope);

}
