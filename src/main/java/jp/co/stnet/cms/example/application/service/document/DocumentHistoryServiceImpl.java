package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.application.repository.document.DocumentRepository;
import jp.co.stnet.cms.example.application.repository.document.DocumentRevisionRepository;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocumentHistoryServiceImpl implements  DocumentHistoryService{

    @Autowired
    DocumentRevisionRepository documentRevisionRepository;

    /**
     * testtest
     * @param id
     * @param saveRevision
     * @param publicScope
     * @return
     */
    @Override
    public List<DocumentRevision> search(Long id, boolean saveRevision, Set<String> publicScope){
        List<DocumentRevision> list = new ArrayList<>();
        list.addAll(documentRevisionRepository.findByIdAndSaveRevisionAndPublicScope(id, saveRevision, publicScope));
        return list;
    }
}
