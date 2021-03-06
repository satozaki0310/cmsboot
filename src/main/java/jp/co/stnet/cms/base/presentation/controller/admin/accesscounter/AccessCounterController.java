package jp.co.stnet.cms.base.presentation.controller.admin.accesscounter;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.application.service.accesscounter.AccessCounterService;
import jp.co.stnet.cms.base.application.service.filemanage.FileManagedSharedService;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.common.AccessCounter;
import jp.co.stnet.cms.base.domain.model.common.Status;
import jp.co.stnet.cms.base.domain.model.filemanage.FileManaged;
import jp.co.stnet.cms.base.presentation.controller.admin.upload.UploadForm;
import jp.co.stnet.cms.common.constant.Constants;
import jp.co.stnet.cms.common.datatables.DataTablesInputDraft;
import jp.co.stnet.cms.common.datatables.DataTablesOutput;
import jp.co.stnet.cms.common.datatables.OperationsUtil;
import jp.co.stnet.cms.common.message.MessageKeys;
import jp.co.stnet.cms.common.util.CsvUtils;
import jp.co.stnet.cms.common.util.StateMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.codelist.CodeList;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import javax.inject.Named;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("admin/accesscounter")
@TransactionTokenCheck("accesscounter")
public class AccessCounterController {

    // JSP???????????????
    private final String BASE_PATH = "admin/accesscounter";
    private final String JSP_LIST = BASE_PATH + "/list";
    private final String JSP_FORM = BASE_PATH + "/form";
    private final String JSP_VIEW = BASE_PATH + "/view";
    private final String JSP_UPLOAD_FORM = "upload/form";
    private final String JSP_UPLOAD_COMPLETE = "upload/complete";

    // CSV/Excel??????????????????(???????????????)
    private final String DOWNLOAD_FILENAME = "accesscounter";

    // ????????????????????????????????????????????????ID
    private final String UPLOAD_JOB_ID = "job03";

    @Autowired
    AccessCounterService accessCounterService;

    @Autowired
    FileManagedSharedService fileManagedSharedService;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    @Named("CL_STATUS")
    CodeList statusCodeList;

    @Autowired
    Mapper beanMapper;

    @ModelAttribute
    private AccessCounterForm setUp() {
        return new AccessCounterForm();
    }

    /**
     * ?????????????????????
     */
    @GetMapping(value = "list")
    public String list(Model model) {
        return JSP_LIST;
    }

    /**
     * DataTables??????JSON?????????
     *
     * @param input DataTables???????????????(Server-side??????)
     * @return JSON
     */
    @ResponseBody
    @GetMapping(value = "/list/json")
    public DataTablesOutput<AccessCounterListRow> listJson(@Validated DataTablesInputDraft input) {

        List<AccessCounterListRow> listRows = new ArrayList<>();
        List<AccessCounter> accessCounterList = new ArrayList<>();
        Long recordsFiltered = 0L;

        if (input.getDraft()) { // ?????????????????????
            Page<AccessCounter> accessCounterPage = accessCounterService.findPageByInput(input);
            accessCounterList.addAll(accessCounterPage.getContent());
            recordsFiltered = accessCounterPage.getTotalElements();

        } else {
//            Page<AccessCounterRevision> accessCounterPage2 = accessCounterService.findMaxRevPageByInput(input);
//            for (AccessCounterRevision accessCounterRevision : accessCounterPage2.getContent()) {
//                accessCounterList.add(beanMapper.map(accessCounterRevision, AccessCounter.class));
//            }
//            recordsFiltered = accessCounterPage2.getTotalElements();
        }

        for (AccessCounterBean bean : getBeanList(accessCounterList)) {
            AccessCounterListRow accessCounterListRow = beanMapper.map(bean,AccessCounterListRow.class);

            accessCounterListRow.setOperations(getToggleButton(bean.getId().toString(), op(null)));
            accessCounterListRow.setDT_RowId(bean.getId().toString());
            // ????????????????????????
            accessCounterListRow.setStatusLabel(Status.getByValue(bean.getStatus()).getCodeLabel());
            listRows.add(accessCounterListRow);
        }

        DataTablesOutput<AccessCounterListRow> output = new DataTablesOutput<>();
        output.setData(listRows);
        output.setDraw(input.getDraw());
        output.setRecordsTotal(0);
        output.setRecordsFiltered(recordsFiltered);

        return output;
    }

    /**
     * CSV?????????????????????????????????
     *
     * @param input DataTables???????????????(Server-side??????)
     * @param model ?????????
     * @return ?????????????????????????????????View
     */
    @GetMapping(value = "/list/csv")
    public String listCsv(@Validated DataTablesInputDraft input, Model model) {
        input.setStart(0);
        input.setLength(Constants.CSV.MAX_LENGTH);
        setModelForCsv(input, model);
        model.addAttribute("csvConfig", CsvUtils.getCsvDefault());
        model.addAttribute("csvFileName", DOWNLOAD_FILENAME + ".csv");
        return "csvDownloadView";
    }

    /**
     * TSV?????????????????????????????????
     *
     * @param input DataTables???????????????(Server-side??????)
     * @param model ?????????
     * @return ?????????????????????????????????View
     */
    @GetMapping(value = "/list/tsv")
    public String listTsv(@Validated DataTablesInputDraft input, Model model) {
        input.setStart(0);
        input.setLength(Constants.CSV.MAX_LENGTH);
        setModelForCsv(input, model);
        model.addAttribute("csvConfig", CsvUtils.getTsvDefault());
        model.addAttribute("csvFileName", DOWNLOAD_FILENAME + ".tsv");
        return "csvDownloadView";
    }

    /**
     * Excel?????????????????????????????????
     *
     * @param input DataTables???????????????(Server-side??????)
     * @param model ?????????
     * @return ?????????????????????????????????View
     */
    @GetMapping(value = "/list/excel")
    public String listExcel(@Validated DataTablesInputDraft input, Model model) {
        input.setStart(0);
        input.setLength(Constants.EXCEL.MAX_LENGTH);
        // TODO: ???????????????????????????????????????
        model.addAttribute("list", accessCounterService.findPageByInput(input).getContent());
        model.addAttribute("excelFileName", DOWNLOAD_FILENAME + ".xlsx");
        return "excelDownloadView";
    }

    /**
     * csvDownloadView????????????????????????
     *
     * @param input DataTables???????????????(Server-side??????)
     * @param model ????????????????????????????????????
     */
    private void setModelForCsv(DataTablesInputDraft input, Model model) {

        List<AccessCounterCsvBean> csvBeans = new ArrayList<>();
        List<AccessCounter> accessCounterList = new ArrayList<>();

        if (input.getDraft() == null || input.getDraft()) { // ?????????????????????
            Page<AccessCounter> accessCounterPage = accessCounterService.findPageByInput(input);
            accessCounterList.addAll(accessCounterPage.getContent());

        } else {
//            Page<AccessCounterRevision> accessCounterPage2 = accessCounterService.findMaxRevPageByInput(input);
//            for (AccessCounterRevision accessCounterRevision : accessCounterPage2.getContent()) {
//                accessCounterList.add(beanMapper.map(accessCounterRevision, AccessCounter.class));
//            }
        }

        for (AccessCounterBean accessCounterBean : getBeanList(accessCounterList)) {
            AccessCounterCsvBean row = beanMapper.map(accessCounterBean, AccessCounterCsvBean.class);
            customMap(row, accessCounterBean);
            row.setStatusLabel(Status.getByValue(accessCounterBean.getStatus()).getCodeLabel());
            csvBeans.add(row);
        }

        model.addAttribute("exportCsvData", csvBeans);
        model.addAttribute("class", AccessCounterCsvBean.class);
    }

    /**
     * ???????????????CSV???????????????????????????????????????
     *
     * @param entities
     * @return
     */
    private List<AccessCounterBean> getBeanList(List<AccessCounter> entities) {
        List<AccessCounterBean> beans = new ArrayList<>();
        for (AccessCounter entity : entities) {
            AccessCounterBean bean = beanMapper.map(entity, AccessCounterBean.class);

            // TODO: ????????????????????????????????????????????????

            beans.add(bean);
        }
        return beans;
    }

    /**
     * CSV??????????????????????????????????????????
     *
     * @param row               ????????????CSV?????????
     * @param accessCounterBean ??????????????????
     */
    private void customMap(AccessCounterCsvBean row, AccessCounterBean accessCounterBean) {

    }

//    /**
//     * ??????????????????????????????????????????????????????????????????????????????
//     * @param entities ?????????????????????????????????????????????
//     * @return ??????????????????????????????
//     */
//    private List<AccessCounterBean> getBeanListByRev(List<AccessCounterRevision> entities) {
//        List<AccessCounter> beans = new ArrayList<>();
//        for(AccessCounterRevision entity : entities) {
//            AccessCounter bean = beanMapper.map(entities, AccessCounter.class);
//            beans.add(bean);
//        }
//        return getBeanList(beans);
//    }

    /**
     * ?????????????????????????????????HTML?????????
     *
     * @param id ???????????????????????????ID
     * @param op OperationsUtil ?????????URL????????????????????????
     * @return HTML
     */
    private String getToggleButton(String id, OperationsUtil op) {

        // fixedColumn????????????????????????????????????????????????
//        StringBuffer link = new StringBuffer();
//        link.append("<div class=\"whitespace-nowrap\">");
//        link.append("<a class=\"whitespace-nowrap\" href=\"" + op.getEditUrl(id) + "\">" + op.getLABEL_EDIT() + "</a>");
//        link.append(" | ");
//        link.append("<a class=\"whitespace-nowrap\" href=\"" + op.getViewUrl(id) + "\">" + op.getLABEL_VIEW() + "</a></li>");
//        link.append("</div>");

        StringBuffer link = new StringBuffer();
        link.append("<div class=\"btn-group\">");
        link.append("<a href=\"" + op.getEditUrl(id) + "\" class=\"btn btn-button btn-sm\" style=\"white-space: nowrap\">" + op.getLABEL_EDIT() + "</a>");
        link.append("<button type=\"button\" class=\"btn btn-button btn-sm dropdown-toggle dropdown-toggle-split\"data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">");
        link.append("</button>");
        link.append("<div class=\"dropdown-menu\">");
        link.append("<a class=\"dropdown-item\" href=\"" + op.getViewUrl(id) + "\">" + op.getLABEL_VIEW() + "</a>");
        link.append("<a class=\"dropdown-item\" href=\"" + op.getCopyUrl(id) + "\">" + op.getLABEL_COPY() + "</a>");
        link.append("<a class=\"dropdown-item\" href=\"" + op.getInvalidUrl(id) + "\">" + op.getLABEL_INVALID() + "</a>");
        link.append("</div>");
        link.append("</div>");

        return link.toString();
    }

    private OperationsUtil op() {
        return new OperationsUtil(BASE_PATH);
    }

    private OperationsUtil op(String param) {
        return new OperationsUtil(param);
    }

    /**
     * ??????????????????
     */
    @GetMapping("{uuid}/download")
    public String download(
            Model model,
            @PathVariable("uuid") String uuid,
            @AuthenticationPrincipal LoggedInUser loggedInUser) {

        accessCounterService.hasAuthority(Constants.OPERATION.DOWNLOAD, loggedInUser);

        model.addAttribute(fileManagedSharedService.findByUuid(uuid));
        return "fileManagedDownloadView";
    }

    /**
     * ????????????
     */
    @PostMapping("bulk_delete")
    public String bulkDelete(Model model, String selectedKey, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        accessCounterService.hasAuthority(Constants.OPERATION.BULK_DELETE, loggedInUser);

        String[] strKeys = selectedKey.split(",");
        List<AccessCounter> deleteEntities = new ArrayList<>();
        for (String key : strKeys) {
            AccessCounter entity = accessCounterService.findById(Long.valueOf(key));
            if (entity.getStatus().equals(Status.INVALID.getCodeValue())) {
                deleteEntities.add(entity);
            }
        }

        accessCounterService.delete(deleteEntities);

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0003));
        return "redirect:" + op().getListUrl();
    }

    /**
     * ???????????????(???????????????????????????)
     */
    @PostMapping("bulk_invalid")
    public String bulkInvalid(Model model, String selectedKey, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        accessCounterService.hasAuthority(Constants.OPERATION.BULK_INVALID, loggedInUser);

        String[] strKeys = selectedKey.split(",");
        List<Long> ids = new ArrayList<>();
        for (String key : strKeys) {
            Long id = Long.valueOf(key);
            AccessCounter entity = accessCounterService.findById(id);
            if (entity.getStatus().equals(Status.VALID.getCodeValue())) {
                ids.add(id);
            }
        }

        accessCounterService.invalid(ids);

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0002));
        return "redirect:" + op().getListUrl();
    }

    /**
     * ???????????????(???????????????????????????)
     */
    @PostMapping("bulk_valid")
    public String bulkValid(Model model, String selectedKey, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        accessCounterService.hasAuthority(Constants.OPERATION.BULK_VALID, loggedInUser);

        String[] strKeys = selectedKey.split(",");
        List<Long> ids = new ArrayList<>();
        for (String key : strKeys) {
            Long id = Long.valueOf(key);
            AccessCounter entity = accessCounterService.findById(id);
            if (entity.getStatus().equals(Status.INVALID.getCodeValue())) {
                ids.add(id);
            }
        }

        accessCounterService.valid(ids);

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0002));
        return "redirect:" + op().getListUrl();
    }

    /**
     * ???????????????????????????
     */
    @GetMapping(value = "create", params = "form")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String createForm(AccessCounterForm form,
                             Model model,
                             @AuthenticationPrincipal LoggedInUser loggedInUser,
                             @RequestParam(value = "copy", required = false) Long copy) {

        accessCounterService.hasAuthority(Constants.OPERATION.CREATE, loggedInUser);

        if (copy != null) {
            AccessCounter source = accessCounterService.findById(copy);
            beanMapper.map(source, form);
            form.setId(null);
            form.setVersion(null);
        }

        setFileManagedToForm(form);

        model.addAttribute("buttonState", getButtonStateMap(Constants.OPERATION.CREATE, null).asMap());
        model.addAttribute("fieldState", getFiledStateMap(Constants.OPERATION.CREATE, null).asMap());
        model.addAttribute("op", op());

        return JSP_FORM;
    }

    /**
     * UUID??????FileManaged?????????????????????????????????form?????????????????????
     *
     * @param form ????????????
     */
    private void setFileManagedToForm(AccessCounterForm form) {
        // TODO ??????????????????????????????????????????
//        if (form.getAttachedFile01Uuid() != null) {
//            form.setAttachedFile01Managed(fileManagedSharedService.findByUuid(form.getAttachedFile01Uuid()));
//        }
    }

//    /**
//     * UUID??????FileManaged?????????????????????????????????Entity?????????????????????
//     * @param entity ??????????????????
//     */
//    private void setFileManagedToEntity(AccessCounter entity) {
//        // TODO ??????????????????????????????????????????
//        if (entity.getAttachedFile01Uuid() != null) {
//            entity.setAttachedFile01Managed(fileManagedSharedService.findByUuid(entity.getAttachedFile01Uuid()));
//        }
//    }

    /**
     * ????????????
     */
    @PostMapping(value = "create")
    @TransactionTokenCheck
    public String create(@Validated({AccessCounterForm.Create.class, Default.class}) AccessCounterForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirect,
                         @AuthenticationPrincipal LoggedInUser loggedInUser,
                         @RequestParam(value = "saveDraft", required = false) String saveDraft) {

        accessCounterService.hasAuthority(Constants.OPERATION.CREATE, loggedInUser);

        if (bindingResult.hasErrors()) {
            return createForm(form, model, loggedInUser, null);
        }

        AccessCounter accessCounter = beanMapper.map(form, AccessCounter.class);

        try {
//            if ("true".equals(saveDraft)) {
//                accessCounter.setStatus(Status.VALID.getCodeValue());
//                accessCounterService.saveDraft(accessCounter);
//            } else {
            accessCounter.setStatus(Status.VALID.getCodeValue());
            accessCounterService.save(accessCounter);
//            }
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
            return createForm(form, model, loggedInUser, null);
        }

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0001));

        return "redirect:" + op().getEditUrl(accessCounter.getId().toString());
    }

    /**
     * ?????????????????????
     */
    @GetMapping(value = "{id}/update", params = "form")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String updateForm(AccessCounterForm form, Model model,
                             @AuthenticationPrincipal LoggedInUser loggedInUser,
                             @PathVariable("id") Long id) {

        accessCounterService.hasAuthority(Constants.OPERATION.UPDATE, loggedInUser);

        AccessCounter accessCounter = accessCounterService.findById(id);
        model.addAttribute("accessCounter", accessCounter);

        // ??????=?????????????????????????????????????????????
        if (accessCounter.getStatus().equals(Status.INVALID.getCodeValue())) {
            model.addAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0008));
            return view(model, loggedInUser, id, null);
        }

        // ???????????????????????????????????????form????????????DB???????????????????????????
        if (form.getVersion() == null) {
            beanMapper.map(accessCounter, form);
        }

        setFileManagedToForm(form);

        model.addAttribute("buttonState", getButtonStateMap(Constants.OPERATION.UPDATE, accessCounter).asMap());
        model.addAttribute("fieldState", getFiledStateMap(Constants.OPERATION.UPDATE, accessCounter).asMap());
        model.addAttribute("op", op());

        return JSP_FORM;
    }

    /**
     * ??????
     */
    @PostMapping(value = "{id}/update")
    @TransactionTokenCheck
    public String update(@Validated({AccessCounterForm.Update.class, Default.class}) AccessCounterForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirect,
                         @AuthenticationPrincipal LoggedInUser loggedInUser,
                         @PathVariable("id") Long id,
                         @RequestParam(value = "saveDraft", required = false) String saveDraft) {

        accessCounterService.hasAuthority(Constants.OPERATION.UPDATE, loggedInUser);

        if (bindingResult.hasErrors()) {
            return updateForm(form, model, loggedInUser, id);
        }

        AccessCounter accessCounter = beanMapper.map(form, AccessCounter.class);

        try {
//            if ("true".equals(saveDraft)) {
//                accessCounterService.saveDraft(accessCounter);
//            } else {
            accessCounter.setStatus(Status.VALID.getCodeValue());
            accessCounterService.save(accessCounter);
//            }
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
            return updateForm(form, model, loggedInUser, id);
        }

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0004));

        return "redirect:" + op().getEditUrl(accessCounter.getId().toString());
    }

    /**
     * ??????
     */
    @GetMapping(value = "{id}/delete")
    public String delete(Model model, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser,
                         @PathVariable("id") Long id) {

        accessCounterService.hasAuthority(Constants.OPERATION.DELETE, loggedInUser);

        try {
            accessCounterService.delete(id);
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
        }

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0007));

        return "redirect:" + op().getListUrl();
    }

    /**
     * ?????????
     */
    @GetMapping(value = "{id}/invalid")
    public String invalid(Model model, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser,
                          @PathVariable("id") Long id) {

        accessCounterService.hasAuthority(Constants.OPERATION.INVALID, loggedInUser);

        AccessCounter entity = accessCounterService.findById(id);

        try {
            entity = accessCounterService.invalid(id);
        } catch (BusinessException e) {
            redirect.addFlashAttribute(e.getResultMessages());
            return "redirect:" + op().getEditUrl(id.toString());
        }

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0002));

        return "redirect:" + op().getViewUrl(id.toString());
    }

    /**
     * ????????????
     */
    @GetMapping(value = "{id}/valid")
    public String valid(Model model, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser,
                        @PathVariable("id") Long id) {

        accessCounterService.hasAuthority(Constants.OPERATION.VALID, loggedInUser);

        // ??????????????????????????????
        AccessCounter entity = accessCounterService.findById(id);

        try {
            entity = accessCounterService.valid(id);
        } catch (BusinessException e) {
            redirect.addFlashAttribute(e.getResultMessages());
            return "redirect:" + op().getViewUrl(id.toString());
        }

        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0002));

        return "redirect:" + op().getEditUrl(id.toString());
    }

//    /**
//     * ???????????????
//     */
//    @GetMapping(value = "{id}/cancel_draft")
//    public String cancelDraft(Model model, RedirectAttributes redirect, @AuthenticationPrincipal LoggedInUser loggedInUser,
//                              @PathVariable("id") Long id) {
//
//        accessCounterService.hasAuthority(Constants.OPERATION.CANCEL_DRAFT, loggedInUser);
//
//        // ??????????????????????????????
//        AccessCounter entity = accessCounterService.findById(id);
//
//        try {
//            entity = accessCounterService.cancelDraft(id);
//        } catch (BusinessException e) {
//            redirect.addFlashAttribute(e.getResultMessages());
//            return "redirect:" + op().getEditUrl(id.toString());
//        }
//
//        redirect.addFlashAttribute(ResultMessages.info().add(MessageKeys.I_CM_FW_0002));
//
//        if (entity != null) {
//            return "redirect:" + op().getEditUrl(id.toString());
//        } else {
//            return "redirect:" + op().getListUrl();
//        }
//    }

    /**
     * ?????????????????????
     */
    @GetMapping(value = "{id}")
    public String view(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser,
                       @PathVariable("id") Long id,
                       @RequestParam(value = "rev", required = false) Long rev) {

        accessCounterService.hasAuthority(Constants.OPERATION.VIEW, loggedInUser);

        // AccessCounter accessCounter = accessCounterService.findById(id);???// TODO: ???????????????????????????????????????????????????????????????

        AccessCounter accessCounter;

//        if (rev == null) {
        // ????????????????????????
        accessCounter = accessCounterService.findById(id);

//        } else if (rev == 0) {
        // ??????????????????????????????
//            accessCounter = beanMapper.map(accessCounterService.findByIdLatestRev(id), AccessCounter.class);

//        } else {
        // ???????????????????????????
//            accessCounter = beanMapper.map(accessCounterService.findByRid(rev), AccessCounter.class);
//        }

//        setFileManagedToEntity(accessCounter);

        model.addAttribute("accessCounter", accessCounter);

        model.addAttribute("buttonState", getButtonStateMap(Constants.OPERATION.VIEW, accessCounter).asMap());
        model.addAttribute("fieldState", getFiledStateMap(Constants.OPERATION.VIEW, accessCounter).asMap());
        model.addAttribute("op", op());

        return JSP_FORM;
    }

    /**
     * ????????????????????????
     */
    private StateMap getButtonStateMap(String operation, AccessCounter record) {

        if (record == null) {
            record = new AccessCounter();
        }

        List<String> includeKeys = new ArrayList<>();
        includeKeys.add(Constants.BUTTON.GOTOLIST);
        includeKeys.add(Constants.BUTTON.GOTOUPDATE);
        includeKeys.add(Constants.BUTTON.VIEW);
        includeKeys.add(Constants.BUTTON.SAVE);
        includeKeys.add(Constants.BUTTON.INVALID);
        includeKeys.add(Constants.BUTTON.VALID);
        includeKeys.add(Constants.BUTTON.DELETE);
        includeKeys.add(Constants.BUTTON.UNLOCK);
        includeKeys.add(Constants.BUTTON.SAVE_DRAFT);
        includeKeys.add(Constants.BUTTON.CANCEL_DRAFT);
        includeKeys.add(Constants.BUTTON.COPY);

        StateMap buttonState = new StateMap(Default.class, includeKeys, new ArrayList<>());

        // ????????????
        buttonState.setViewTrue(Constants.BUTTON.GOTOLIST);

        // ????????????
        if (Constants.OPERATION.CREATE.equals(operation)) {
            buttonState.setViewTrue(Constants.BUTTON.SAVE);
            buttonState.setViewTrue(Constants.BUTTON.SAVE_DRAFT);
        }

        // ??????
        if (Constants.OPERATION.UPDATE.equals(operation)) {

            if (Status.DRAFT.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.CANCEL_DRAFT);
                buttonState.setViewTrue(Constants.BUTTON.SAVE_DRAFT);
                buttonState.setViewTrue(Constants.BUTTON.SAVE);
                buttonState.setViewTrue(Constants.BUTTON.VIEW);
                buttonState.setViewTrue(Constants.BUTTON.COPY);
            }

            if (Status.VALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.SAVE_DRAFT);
                buttonState.setViewTrue(Constants.BUTTON.SAVE);
                buttonState.setViewTrue(Constants.BUTTON.VIEW);
                buttonState.setViewTrue(Constants.BUTTON.INVALID);
                buttonState.setViewTrue(Constants.BUTTON.COPY);
            }

            if (Status.INVALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.VIEW);
                buttonState.setViewTrue(Constants.BUTTON.VALID);
                buttonState.setViewTrue(Constants.BUTTON.DELETE);
                buttonState.setViewTrue(Constants.BUTTON.COPY);
            }

        }

        // ??????
        if (Constants.OPERATION.VIEW.equals(operation)) {

            // ???????????????????????????
            if (Status.VALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.GOTOUPDATE);
                buttonState.setViewTrue(Constants.BUTTON.INVALID);
                buttonState.setViewTrue(Constants.BUTTON.UNLOCK);
                buttonState.setViewTrue(Constants.BUTTON.COPY);
            }

            // ????????????????????????
            if (Status.INVALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.VALID);
                buttonState.setViewTrue(Constants.BUTTON.DELETE);
                buttonState.setViewTrue(Constants.BUTTON.COPY);
            }
        }

        return buttonState;
    }

    /**
     * ??????????????????????????????
     */
    private StateMap getFiledStateMap(String operation, AccessCounter record) {

        // ??????????????????????????????????????????????????????
        List<String> excludeKeys = new ArrayList<>();
        excludeKeys.add("id");
        excludeKeys.add("version");

        StateMap fieldState = new StateMap(AccessCounterForm.class, new ArrayList<>(), excludeKeys);

        // ????????????
        if (Constants.OPERATION.CREATE.equals(operation)) {
            fieldState.setInputTrueAll();
        }

        // ??????
        if (Constants.OPERATION.UPDATE.equals(operation)) {
            fieldState.setInputTrueAll();
            fieldState.setViewTrue("status");

            // ????????????????????????
            if (Status.INVALID.toString().equals(record.getStatus())) {
                fieldState.setReadOnlyTrueAll();
            }
        }

        // ??????
        if (Constants.OPERATION.VIEW.equals(operation)) {
            fieldState.setViewTrueAll();
        }

        return fieldState;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    @GetMapping(value = "upload", params = "form")
    public String uploadForm(@ModelAttribute UploadForm form, Model model,
                             @AuthenticationPrincipal LoggedInUser loggedInUser) {

        form.setJobName("job03");

        FileManaged uploadFileManaged = fileManagedSharedService.findByUuid(form.getUploadFileUuid());
        form.setUploadFileManaged(uploadFileManaged);

        model.addAttribute("pageTitle", "Import Simple Entity");
        model.addAttribute("referer", "list");

        return JSP_UPLOAD_FORM;
    }

    /**
     * ????????????????????????(???????????????)
     */
    @PostMapping(value = "upload")
    public String upload(@Validated UploadForm form, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal LoggedInUser loggedInUser) {

        final String jobName = UPLOAD_JOB_ID;
        Long jobExecutionId = null;

        if (result.hasErrors()) {
            return uploadForm(form, model, loggedInUser);
        }

        FileManaged uploadFile = fileManagedSharedService.findByUuid(form.getUploadFileUuid());
        String uploadFileAbsolutePath = fileManagedSharedService.getFileStoreBaseDir() + uploadFile.getUri();
        String jobParams = "inputFile=" + uploadFileAbsolutePath;


        if (!jobName.equals(form.getJobName())) {
            return uploadForm(form, model, loggedInUser);
        }


        try {
            jobExecutionId = jobOperator.start(jobName, jobParams);

        } catch (NoSuchJobException | JobInstanceAlreadyExistsException | JobParametersInvalidException e) {
            e.printStackTrace();

            // ??????????????????????????????????????????????????????????????????

        }

        redirectAttributes.addAttribute("jobName", jobName);
        redirectAttributes.addAttribute("jobExecutionId", jobExecutionId);

        return "redirect:upload?complete";
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping(value = "upload", params = "complete")
    public String uploadComplete(Model model, @RequestParam Map<String, String> params, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        model.addAttribute("returnBackBtn", "?????????????????????");
        model.addAttribute("returnBackUrl", op().getListUrl());
        model.addAttribute("jobName", params.get("jobName"));
        model.addAttribute("jobExecutionId", params.get("jobExecutionId"));
        return JSP_UPLOAD_COMPLETE;
    }

}
