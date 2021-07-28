package jp.co.stnet.cms.sales.domain.model.variable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableBean implements Serializable {

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
