package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.base.domain.model.variable.Variable;
import jp.co.stnet.cms.sales.application.repository.variable.VariableRepository;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentFullSearchServiceImpl implements DocumentFullSearchService {

    @Autowired
    VariableRepository variableRepository;

    /**
     * 入力フォームに入力した内容で全文検索を行う
     * ① 入力内容を形態素解析する
     * ② ①の値を用いて全文検索
     * where: ①の結果がcontentにマッチしたデータ and 閲覧権限がある
     * aggregation: 指定したフィールドのジャンル別の数
     * sort: sort項目の内容
     * .fetch: ページの表示数
     *
     * @param term     　入力フォームに入力した内容
     * @param pageable 　ページ情報
     * @return
     */
    @Override
    public SearchResult<DocumentIndex> search(String term, Pageable pageable) {
        return null;
    }

    /**
     * textからtermの箇所を検索し、その前後の文字を抽出する
     *
     * @param text ヒットした添付ファイルの内容(content)
     * @param term 入力フォームに入力した内容
     * @return ヒットした箇所前後の文字列
     */
    @Override
    public String highlight(String text, String term) {
        return null;
    }

    /**
     * 日本語形態素解析を行う
     *
     * @param term 入力フォームに入力した内容
     * @return 形態素解析の結果(List)
     */
    private List<String> analyze(String term) {
        return null;
    }

    /**
     * ジャンル別に集約したデータからコードを抜き出し、コードで検索する
     *
     * @param text ジャンル別に集約したコード
     * @return
     */
    @Override
    public List<Variable> label(Map<String, Long> text) {
        // Mapのコードの値だけをリストに格納する
        List<String> code = new ArrayList<>();
        variableRepository.findByCode(code);
        return null;
    }
}
