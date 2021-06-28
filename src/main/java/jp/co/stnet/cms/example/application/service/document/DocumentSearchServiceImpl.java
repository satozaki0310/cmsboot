package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.application.repository.document.DocumentRepository;
import jp.co.stnet.cms.example.domain.model.document.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DocumentSearchServiceImpl implements  DocumentSearchService{

    @Autowired
    DocumentRepository documentRepository;

    /**

     **/
    @Override
    public List<Document> search(){
        return null;
    }
}
