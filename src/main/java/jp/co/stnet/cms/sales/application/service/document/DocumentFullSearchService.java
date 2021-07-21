package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.base.domain.model.variable.Variable;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DocumentFullSearchService {
    SearchResult<DocumentIndex> search(String term, String period, String sort, List<String> facets, Pageable pageable);

    String highlight(String text, String term);

    List<Variable> label(Map<String, Long> text);
}
