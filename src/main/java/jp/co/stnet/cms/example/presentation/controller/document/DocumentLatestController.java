package jp.co.stnet.cms.example.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.common.constant.Constants;
import jp.co.stnet.cms.common.datatables.DataTablesInputHistory;
import jp.co.stnet.cms.common.datatables.DataTablesOutput;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.example.application.service.document.DocumentHistoryService;
import jp.co.stnet.cms.example.application.service.document.DocumentRevisionService;
import jp.co.stnet.cms.example.application.service.document.DocumentService;
import jp.co.stnet.cms.example.domain.model.document.Document;
import jp.co.stnet.cms.example.domain.model.document.DocumentHistoryBean;
import jp.co.stnet.cms.example.domain.model.document.DocumentLatestBean;
import jp.co.stnet.cms.example.domain.model.document.DocumentRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import static jp.co.stnet.cms.example.presentation.controller.document.DocumentConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH)
public class DocumentLatestController {

    @Autowired
    DocumentRevisionService documentRevisionService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentHelper helper;

    @ModelAttribute
    DocumentForm setUp() {
        return new DocumentForm();
    }

    /**
     *
     * @param model
     * @param loggedInUser
     * @param id
     * @return
     */
    @GetMapping(value = "{id}/last")
    public String viewLast(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser,
                                                         @PathVariable("id") Long id) {

        //公開区分を格納
        Set<String> publicScope = helper.getPublicScope(loggedInUser);


        Document document = documentRevisionService.findLatest(id,publicScope);

        model.addAttribute("document",document);
        model.addAttribute("buttonState", helper.getButtonStateMap(Constants.OPERATION.VIEW,document,null).asMap());
        model.addAttribute("fieldState", helper.getFiledStateMap(Constants.OPERATION.VIEW, document, null).asMap());
        model.addAttribute("op", new OperationsUtil(BASE_PATH));
        return BASE_PATH + "/details";
    }
}
