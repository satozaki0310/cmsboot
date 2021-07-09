package jp.co.stnet.cms.sales.presentation.controller.document;

import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.common.constant.Constants;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.sales.application.service.document.DocumentRevisionService;
import jp.co.stnet.cms.sales.application.service.document.DocumentService;
import jp.co.stnet.cms.sales.domain.model.document.Document;
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
public class DocumentLatestController {

    @Autowired
    DocumentRevisionService documentRevisionService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentHelper helper;

    @Autowired
    HttpSession session;

    @ModelAttribute
    DocumentForm setUp() {
        return new DocumentForm();
    }

    /**
     * 検索画面、全文検索、管理画面から遷移した際にはセッションに値を入れる
     *
     * @param model
     * @param loggedInUser
     * @param id
     * @return
     */
    @GetMapping(value = "{id}/last")
    public String viewLast(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser,
                           @PathVariable("id") Long id, @RequestHeader(value = "referer", required = false) final String referer) {

        //公開区分を格納
        Set<String> publicScope = helper.getPublicScope(loggedInUser);

        Document document = documentRevisionService.findLatest(id, publicScope);

        model.addAttribute("document", document);
        model.addAttribute("buttonState", helper.getButtonStateMap(Constants.OPERATION.VIEW, document, null).asMap());
        model.addAttribute("fieldState", helper.getFiledStateMap(Constants.OPERATION.VIEW, document, null).asMap());

        OperationsUtil op = new OperationsUtil(null);


        if (referer != null) {
            if (helper.isReferer(referer)) {
                session.setAttribute("referer", referer);
            }
        } else {
            //セッションから情報がとれなかったときは検索一覧にとばす
            op.setURL_LIST("list");
        }

        if (session.getAttribute("referer") != null) {
            op.setURL_LIST((String) session.getAttribute("referer"));
        }

        System.out.println("session:" + session.getAttribute("referer"));
        model.addAttribute("op", op);

        return BASE_PATH + "/form";
    }
}
