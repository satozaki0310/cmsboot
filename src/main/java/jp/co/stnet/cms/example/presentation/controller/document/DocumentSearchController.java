package jp.co.stnet.cms.example.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.common.Status;
import jp.co.stnet.cms.common.datatables.DataTablesInput;
import jp.co.stnet.cms.common.datatables.DataTablesOutput;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.example.application.service.PageIdxService;
import jp.co.stnet.cms.example.application.service.document.DocumentService;
import jp.co.stnet.cms.example.domain.model.document.Document;
import jp.co.stnet.cms.example.domain.model.document.File;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdx;
import jp.co.stnet.cms.example.domain.model.pageidx.PageIdxListRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.terasoluna.gfw.common.codelist.CodeList;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static jp.co.stnet.cms.example.presentation.controller.document.DocumentConstant.BASE_PATH;
import static jp.co.stnet.cms.example.presentation.controller.document.DocumentConstant.TEMPLATE_LIST;

@Controller
@RequestMapping(BASE_PATH)
public class DocumentSearchController {

    @Autowired
    Mapper beanMapper;

    @Autowired
    PageIdxService pageIdxService;

    @Autowired
    @Named("CL_DOC_STAGE")
    CodeList useStageCodeList;

    /**
     * 一覧画面の表示
     */
    @GetMapping(value = "search")
    public String list(Model model) {
        return BASE_PATH + "search";
    }

    /**
     * DataTables用のJSONの作成
     * 検索入力項目に入力された値を用いてJSONの作成を行う
     * 検索する項目: ID、タイトル、添付ファイル、区分1、区分2、区分3、区分4、文書の種類、最終改定日
     * タイトルはドキュメント詳細へのリンク、添付ファイルは対象ドキュメントの最新リンクにする。
     * @param input DataTablesから要求
     * @return JSON
     */
    @ResponseBody
    @GetMapping(value = "/search/json")
    public DataTablesOutput<PageIdxListRow> listJson(@Validated DataTablesInput input,
                                                     @AuthenticationPrincipal LoggedInUser loggedInUser) {
        List<PageIdxListRow> listRows = new ArrayList<>();
        List<PageIdx> pageIdxList = new ArrayList<>();
        Long recordsFiltered = 0L;

        Page<PageIdx> pageIdxPage = pageIdxService.findPageByInput(input);
        pageIdxList.addAll(pageIdxPage.getContent());
        recordsFiltered = pageIdxPage.getTotalElements();



        return null;
    }
}