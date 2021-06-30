package jp.co.stnet.cms.example.application.service.document;

import jp.co.stnet.cms.example.domain.model.document.Document;

import java.util.Set;

public interface DocumentRevisionService {
    Document findLatest(Long id, Set<String> publicScope);
}
