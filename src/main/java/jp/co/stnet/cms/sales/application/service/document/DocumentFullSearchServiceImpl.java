package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class DocumentFullSearchServiceImpl implements DocumentFullSearchService {


    /**
     * 入力フォームに入力した内容で全文検索を行う
     * ① 入力内容を形態素解析する
     * ②
     *
     * @param term
     * @param pageable
     * @return
     */
    @Override
    public SearchResult<DocumentIndex> search(String term, Pageable pageable) {
        return null;
    }

    @Override
    public String highlight(String text, String term) {
        return null;
    }

    /**
     * 日本語形態素解析を行う
     *
     * @param term 入力フォームに入力した内容
     * @return 形態素解析の結果
     */
    private List<String> analyze(String term) {
        return null;
    }
}
