package jp.co.stnet.cms.example.application.service;

import jp.co.stnet.cms.base.application.service.AbstractNodeService;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.authentication.Role;
import jp.co.stnet.cms.common.datatables.Column;
import jp.co.stnet.cms.common.datatables.DataTablesInput;
import jp.co.stnet.cms.example.application.repository.pageidx.PageIdxRepository;
import jp.co.stnet.cms.example.application.service.document.DocumentService;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdx;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdxCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

public class PageIdxServiceImpl extends AbstractNodeService<PageIdx, Long> implements PageIdxService {

    @Autowired
    PageIdxRepository pageIdxRepository;

    @Autowired
    DocumentService documentService;

    @PersistenceContext
    EntityManager entityManager;


    @Override
    protected JpaRepository<PageIdx, Long> getRepository() {
        return this.pageIdxRepository;
    }

    @Override
    protected void beforeSave(PageIdx entity, PageIdx current) {
        entity.setDocument(documentService.findById(entity.getDocumentId()));
        super.beforeSave(entity, current);
    }

    @Override
    @PostAuthorize("returnObject == true")
    public Boolean hasAuthority(String Operation, LoggedInUser loggedInUser) {
        return loggedInUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()));
    }

    @Override
    public SearchResult<PageIdx> searchByInput(DataTablesInput input) {

        SearchSession searchSession = Search.session(entityManager);

        int pageSize = input.getLength();
        long offset = input.getStart();

        SearchScope<PageIdx> scope = searchSession.scope( PageIdx.class );

        SearchResult<PageIdx> result = searchSession.search(PageIdx.class)
                .where(
                        f -> {
                            BooleanPredicateClausesStep<?> b = f.bool();
                            int filterFieldNum = 0;

                            for (Column column : input.getColumns()) {
                                String fieldName = column.getData();
                                String value = input.getColumn(fieldName).getSearch().getValue();
                                if (value != null) {
                                    b = b.must(f.match().fields(fieldName).matching(value));
                                    filterFieldNum++;
                                }
                            }

                            if (filterFieldNum > 0) {
                                return b;
                            } else {
                                return f.matchAll();
                            }
                        }
                )
                .sort(f -> f.score())
                .fetch((int) offset, pageSize);

        return result;
    }

    @Override
    public SearchResult<PageIdx> search(PageIdxCriteria criteria, Pageable pageable) {

        SearchSession searchSession = Search.session(entityManager);

        AggregationKey<Map<Integer, Long>> countsByYear = AggregationKey.of("countsByYear");
        AggregationKey<Map<Integer, Long>> countsByPeriod = AggregationKey.of("countsByPeriod");
        AggregationKey<Map<String, Long>> countsByShop = AggregationKey.of("countsByShop");

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();

        SearchScope<PageIdx> scope = searchSession.scope( PageIdx.class );

        SearchResult<PageIdx> result = searchSession.search(PageIdx.class)
                .where(
                        f -> {
                            BooleanPredicateClausesStep<?> b = f.bool();
                            boolean hasFilter = false;

                            if (criteria.getCustomerNumber() != null) {
                                b = b.must(f.match().field("customerNumber").matching(criteria.getCustomerNumber()));
                                hasFilter = true;
                            }

                            if (CollectionUtils.isNotEmpty(criteria.getShopCodes())) {
                                BooleanPredicateClausesStep<?> c = f.bool();
                                for (String shopCode : criteria.getShopCodes()) {
                                    c = c.should(f.match().field("document.shopCode").matching(shopCode));
                                }
                                b = b.must(c);
                                hasFilter = true;
                            }

                            if (CollectionUtils.isNotEmpty(criteria.getYear())) {
                                BooleanPredicateClausesStep<?> c = f.bool();
                                for (Integer year : criteria.getYear()) {
                                    c = c.should(f.match().field("document.year").matching(year));
                                }
                                b = b.must(c);
                                hasFilter = true;
                            }

                            if (CollectionUtils.isNotEmpty(criteria.getPeriod())) {
                                BooleanPredicateClausesStep<?> c = f.bool();
                                for (Integer period : criteria.getPeriod()) {
                                    c = c.should(f.match().field("document.period").matching(period));
                                }
                                b = b.must(c);
                                hasFilter = true;
                            }

                            if (!hasFilter) {
                                return b.must(f.matchAll());
                            }

                            return b;
                        }
                )
                .aggregation(countsByShop, f -> f.terms()
                        .field("document.shopCode", String.class))
                .aggregation(countsByYear, f -> f.terms()
                        .field("document.year", Integer.class))
                .aggregation(countsByPeriod, f -> f.terms()
                        .field("document.period", Integer.class))
                .sort(f -> f.score())
                .fetch((int) offset, pageSize);

        return result;

    }
}
