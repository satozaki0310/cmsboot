package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.common.datatables.DataTablesOutput;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;

import java.util.List;
import java.util.Set;

public interface DocumentHistoryService {
    List<DocumentRevision> search(Long id, boolean saveRevision, Set<String> publicScope);
}
