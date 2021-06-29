package jp.co.stnet.cms.example.application.service;


import jp.co.stnet.cms.base.application.service.NodeIService;
import jp.co.stnet.cms.common.datatables.DataTablesInput;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdx;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdxCriteria;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;


public interface PageIdxService extends NodeIService<PageIdx, Long> {

    SearchResult<PageIdx> searchByInput(DataTablesInput input);


    SearchResult<PageIdx> search(PageIdxCriteria criteria, Pageable pageable);

}
