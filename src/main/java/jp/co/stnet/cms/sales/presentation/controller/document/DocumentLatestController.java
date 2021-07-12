package jp.co.stnet.cms.sales.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.common.constant.Constants;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.sales.application.service.document.DocumentRevisionService;
import jp.co.stnet.cms.sales.domain.model.document.Document;
import jp.co.stnet.cms.sales.domain.model.document.DocumentRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Set;

import static jp.co.stnet.cms.sales.presentation.controller.document.DocumentConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH)
@SessionAttributes(value = {"op"})
public class DocumentLatestController {

    @Autowired
    DocumentRevisionService documentRevisionService;

    @Autowired
    Mapper beanMapper;

    @Autowired
    DocumentHelper helper;

    @Autowired
    HttpSession session;

    @Autowired
    DocumentAuthority authority;

    @ModelAttribute
    DocumentForm setUp() {
        return new DocumentForm();
    }

    /**
     * Ver指定したドキュメント情報を表示する
     *
     * @param model        モデル
     * @param loggedInUser ユーザ情報
     * @param id           ドキュメントID
     * @param referer      遷移元URL
     * @param version      ドキュメントVer
     * @return VIEWのパス
     */
    @GetMapping(value = "{id}", params = "version")
    public String viewLast(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser,
                           @PathVariable("id") Long id, @RequestHeader(value = "referer", required = false) final String referer,
                           @RequestParam(value = "version", required = false) String version) {

        // 公開区分を格納
        Set<String> publicScope = helper.getPublicScope(loggedInUser);

        DocumentRevision documentRevision;

        if (version.equals("last")) {
            documentRevision = documentRevisionService.findLatest(id, publicScope);
        } else {
            documentRevision = documentRevisionService.versionSpecification(id, Long.parseLong(version), publicScope);
        }

        // Document型に変換
        Document documentRecord = beanMapper.map(documentRevision, Document.class);

        // 権限チェック
        authority.hasAuthority(Constants.OPERATION.VIEW, loggedInUser, documentRecord);

        OperationsUtil op = new OperationsUtil(BASE_PATH);

        // セッション管理
        if (referer != null) {
            if (helper.isReferer(referer)) {
                session.setAttribute("referer", referer);
            }
        } else {
            // セッションから情報がとれなかったときは検索一覧にとばす
            op.setURL_LIST("list");
        }

        // セッションに値が格納されている場合、セッションに格納されたURLへ遷移する
        if (session.getAttribute("referer") != null) {
            op.setBaseUrl("");
            op.setURL_LIST((String) session.getAttribute("referer"));
        }

        // Modelに値を格納
        model.addAttribute("document", documentRevision);
        model.addAttribute("buttonState", helper.getButtonStateMap(Constants.OPERATION.VIEW, documentRecord, null).asMap());
        model.addAttribute("fieldState", helper.getFiledStateMap(Constants.OPERATION.VIEW, documentRecord, null).asMap());
        model.addAttribute("op", op);

        return BASE_PATH + "/form";
    }
}
