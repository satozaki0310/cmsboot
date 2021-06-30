package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.application.repository.document.DocumentRepository;
import jp.co.stnet.cms.example.application.repository.document.DocumentRevisionRepository;
import jp.co.stnet.cms.example.domain.model.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DocumentRevisionServiceImpl implements DocumentRevisionService{


    @Autowired
    DocumentRepository documentRepository;

    @Override
    public Document findLatest(Long id, Set<String> publicScope) {
        return documentRepository.findByIdAndPublicScopeIn(id,publicScope);
    }
}
