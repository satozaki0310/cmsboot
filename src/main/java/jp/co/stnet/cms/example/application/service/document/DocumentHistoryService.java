package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.base.domain.model.authentication.Account;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import jp.co.stnet.cms.example.domain.model.person.Person;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface DocumentHistoryService {
    Page<DocumentRevision> search(Long id, boolean saveRevisionOnly, Set<String> publicScope);

    Account nameSearch(String id);
}
