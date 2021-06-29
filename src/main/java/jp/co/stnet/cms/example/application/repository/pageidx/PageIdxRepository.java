package jp.co.stnet.cms.example.application.repository.pageidx;

import jp.co.stnet.cms.example.domain.model.pageidx.PageIdx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageIdxRepository extends JpaRepository<PageIdx, Long> {
}
