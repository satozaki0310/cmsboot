package jp.co.stnet.cms.example.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.common.datatables.DataTablesInput;
import jp.co.stnet.cms.common.datatables.DataTablesOutput;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.example.application.service.document.DocumentHistoryService;
import jp.co.stnet.cms.example.domain.model.document.Document;
import jp.co.stnet.cms.example.domain.model.document.DocumentHistoryBean;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.terasoluna.gfw.common.codelist.CodeList;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static jp.co.stnet.cms.example.presentation.controller.document.DocumentConstant.BASE_PATH;


@Controller
@RequestMapping(BASE_PATH)
public class DocumentHistoryController {

    @Autowired
    Mapper beanMapper;

    @Autowired
    DocumentHistoryService documentHistoryService;

    @Autowired
    @Named("CL_DOC_STAGE")
    CodeList useStageCodeList;

    /**
     * 一覧画面の表示
     */
    @GetMapping(value = "history")
    public String list(Model model) {
        return BASE_PATH + "history";
    }

    /**
     * 全件表示の切り替え
     * datatablesの表示件数を全て表示するに変更する
     * 全件表示の状態でもう一度押すと10件表示に変更
     */
    @GetMapping(value = "history/show")
    public String show() {
        return null;
    }

    /**
     * DataTables用のJSONの作成
     * 検索入力項目に入力された値を用いてJSONの作成を行う
     * 検索する項目: リビジョンID、Ver、変更理由、更新者、更新日時
     * Ver項目は対象ドキュメントのVerに対応したリンク
     * @param input DataTablesから要求
     * @return JSON
     */
    @ResponseBody
    @GetMapping(value = "/history/json/{id}")
    public DataTablesOutput<DocumentHistoryBean> listJson(@Validated DataTablesInput input, @PathVariable("id") Long id) {
        List<DocumentHistoryBean> list = new ArrayList<>();

        //
        OperationsUtil op = new OperationsUtil(null);

        //Helperクラスから返ってくる公開区分
        Set<String> publicScope = null;

        List<DocumentRevision> documentRevisionList = documentHistoryService.search(id,true,publicScope);
        for(DocumentRevision documentRevision : documentRevisionList) {
            DocumentHistoryBean documentHistoryBean = beanMapper.map(documentRevision, DocumentHistoryBean.class);

            //変換する
            documentHistoryBean.setLastModifiedByLabel("satozaki");

            //リビジョンIDにリンクをつける
            // <a href = "example/document/{id}?version=2"> </a>

            documentHistoryBean.setRidLabel("<a href=\"" + op.getViewUrl(id.toString()) + "?version=" + documentRevision.getVersion() + "\" class=\"btn btn-button btn-sm\" style=\"white-space: nowrap\">" + documentRevision.getRid().toString() + "</a>");

            list.add(documentHistoryBean);
        }
        DataTablesOutput<DocumentHistoryBean> output = new DataTablesOutput<>();
        output.setData(list);
        return output;
    }

}
