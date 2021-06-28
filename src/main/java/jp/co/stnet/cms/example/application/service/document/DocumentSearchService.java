package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.domain.model.document.Document;

import java.util.List;

public interface DocumentSearchService {
    List<Document> search();
}
