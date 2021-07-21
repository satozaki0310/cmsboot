package jp.co.stnet.cms.sales.domain.model.document;

import lombok.Data;

import java.util.List;

@Data
public class DocumentFullSearchForm {

    /**
     * 全文検索の入力内容
     */
    private String q;

    /**
     * 期間指定
     */
    private String period;

    /**
     * ソート順
     */
    private String sort;

    /**
     * ファセット項目で選択したもの
     */
    private List<String> facets;
}
