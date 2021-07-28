package jp.co.stnet.cms.sales.domain.model.document;

import lombok.Data;

import java.time.LocalDateTime;
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
     * 期間指定をLocalDateTimeに変換したもの
     */
    private LocalDateTime periodDate;

    /**
     * ソート順
     */
    private String sort;

    /**
     * ファセット項目で選択した区分1
     */
    private List<String> facetsDoc1;

    /**
     * ファセット項目で選択した区分2
     */
    private List<String> facetsDoc2;

    /**
     * ファセット項目で選択した事業領域
     */
    private List<String> facetsService1;

    /**
     * ファセット項目で選択したサービス種別
     */
    private List<String> facetsService2;

    /**
     * ファセット項目で選択したサービス
     */
    private List<String> facetsService3;

    /**
     * 公開区分
     */
    private String publicScope;
}
