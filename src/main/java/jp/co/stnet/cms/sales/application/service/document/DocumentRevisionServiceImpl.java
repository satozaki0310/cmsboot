package jp.co.stnet.cms.sales.application.service.document;


import jp.co.stnet.cms.sales.application.repository.document.DocumentRepository;
import jp.co.stnet.cms.sales.domain.model.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DocumentRevisionServiceImpl implements DocumentRevisionService {


    @Autowired
    DocumentRepository documentRepository;

    @Override
    public Document findLatest(Long id, Set<String> publicScope) {
        return documentRepository.findByIdAndPublicScopeIn(id, publicScope);
    }
}
