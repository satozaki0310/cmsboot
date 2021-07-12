package jp.co.stnet.cms.sales.application.service.document;


import jp.co.stnet.cms.sales.application.repository.document.DocumentRevisionRepository;
import jp.co.stnet.cms.sales.domain.model.document.DocumentRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DocumentRevisionServiceImpl implements DocumentRevisionService {

    @Autowired
    DocumentRevisionRepository documentRevisionRepository;

    @Override
    public DocumentRevision findLatest(Long id, Set<String> publicScope) {
        return documentRevisionRepository.findTopByIdAndPublicScopeInOrderByVersionDesc(id, publicScope);
    }

    @Override
    public DocumentRevision versionSpecification(Long id, Long version, Set<String> publicScope) {
        return documentRevisionRepository.findByIdAndVersionAndPublicScopeIn(id, version, publicScope);
    }

}
