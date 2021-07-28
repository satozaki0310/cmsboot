package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.common.datatables.DataTablesInput;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.hibernate.search.engine.search.query.SearchResult;

public interface DocumentSearchService {

    SearchResult<DocumentIndex> searchByInput(DataTablesInput input);

}
