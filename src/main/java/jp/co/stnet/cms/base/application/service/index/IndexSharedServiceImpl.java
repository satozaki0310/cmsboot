package jp.co.stnet.cms.base.application.service.index;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
@Service
@Transactional
public class IndexSharedServiceImpl implements IndexSharedService {

    @PersistenceContext
    EntityManager entityManager;

    private final int BATCH_SIZE_TO_LOAD_OBJECTS = 25;

    private final int THREADS_TO_LOAD_OBJECTS = 7;

    @Override
    public void reindexing(String entityName) throws InterruptedException, ClassNotFoundException {

        try {
            Class<?> clazz = Class.forName(entityName);
            SearchSession searchSession = Search.session(entityManager);
            MassIndexer indexer = searchSession.massIndexer(clazz)
                    .idFetchSize(Integer.MIN_VALUE)
                    .batchSizeToLoadObjects(BATCH_SIZE_TO_LOAD_OBJECTS)
                    .threadsToLoadObjects(THREADS_TO_LOAD_OBJECTS);

            indexer.start();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean reindexingSync(String entityName) throws InterruptedException, ClassNotFoundException {

        try {
            Class<?> clazz = Class.forName(entityName);
            SearchSession searchSession = Search.session(entityManager);
            MassIndexer indexer = searchSession.massIndexer(clazz)
                    .idFetchSize(Integer.MIN_VALUE)
                    .batchSizeToLoadObjects(BATCH_SIZE_TO_LOAD_OBJECTS)
                    .threadsToLoadObjects(THREADS_TO_LOAD_OBJECTS);
            indexer.startAndWait();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

}
