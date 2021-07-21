package jp.co.stnet.cms.sales.domain.model.variable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableCategoryBean {

    /**
     * コード
     */
    private String code;

    /**
     * コード名称
     */
    private String codeName;

    /**
     * レベル
     */
    private String level;

    /**
     * カウント数
     */
    private Long counts;
}
