package jp.co.stnet.cms.sales.application.repository.document;

import jp.co.stnet.cms.sales.domain.model.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByIdAndPublicScopeIn(Long id, Set<String> publicScope);
}
