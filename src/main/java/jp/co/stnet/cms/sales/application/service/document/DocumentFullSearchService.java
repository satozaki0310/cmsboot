package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;

public interface DocumentFullSearchService {
    SearchResult<DocumentIndex> search(String term, Pageable pageable);

    String highlight(String text, String term);
}
