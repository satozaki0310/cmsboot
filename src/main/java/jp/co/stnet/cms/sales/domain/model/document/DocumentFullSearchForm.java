package jp.co.stnet.cms.sales.domain.model.document;

import lombok.Data;

@Data
public class DocumentFullSearchForm {

    /**
     * 全文検索の入力内容
     */
    private String q;
}
