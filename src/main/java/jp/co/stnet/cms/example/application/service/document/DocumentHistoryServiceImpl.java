package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.application.repository.document.DocumentRevisionRepository;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import jp.co.stnet.cms.example.domain.model.person.Person;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocumentHistoryServiceImpl implements  DocumentHistoryService{

    @Autowired
    DocumentRevisionRepository documentRevisionRepository;

    @Autowired
    Person person;

    /**
     * testtest
     * @param id
     * @param saveRevisionOnly
     * @param publicScope
     * @return
     */
    @Override
    public List<DocumentRevision> search(Long id, boolean saveRevisionOnly, Set<String> publicScope){
        List<DocumentRevision> list = new ArrayList<>();
        if (saveRevisionOnly = true) {
            list.addAll(documentRevisionRepository.findByIdAndSaveRevisionAndPublicScope(id, true, publicScope));
        }else {
            list.addAll(documentRevisionRepository.findByIdAndPublicScope(id, publicScope));
        }
        return list;
    }

    @Override
    public String findLastModifiedByLabel(Long id) {
        return null;
    }
}
