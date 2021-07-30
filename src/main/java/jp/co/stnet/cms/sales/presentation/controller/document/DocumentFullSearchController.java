package jp.co.stnet.cms.sales.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.application.service.variable.VariableService;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.variable.Variable;
import jp.co.stnet.cms.sales.application.service.document.DocumentFullSearchService;
import jp.co.stnet.cms.sales.application.service.document.DocumentHistoryService;
import jp.co.stnet.cms.sales.domain.model.document.DocumentFullSearchForm;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndexSearchRow;
import jp.co.stnet.cms.sales.domain.model.variable.VariableBean;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static jp.co.stnet.cms.sales.presentation.controller.document.DocumentConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH)
public class DocumentFullSearchController {

    private static final String docCategory1 = "DOC_CATEGORY1";
    private static final String docCategory2 = "DOC_CATEGORY2";
    private static final String docService1 = "DOC_SERVICE1";
    private static final String docService2 = "DOC_SERVICE2";
    private static final String docService3 = "DOC_SERVICE3";


    @Autowired
    DocumentFullSearchService documentFullSearchService;

    @Autowired
    Mapper beanMapper;

    @Autowired
    DocumentHistoryService documentHistoryService;

    @Autowired
    DocumentHelper helper;

    @Autowired
    VariableService variableService;

    @ModelAttribute
    private DocumentFullSearchForm setUpFullSearchForm() {
        return new DocumentFullSearchForm();
    }

    @GetMapping("search")
    public String search(Model model, @Validated DocumentFullSearchForm form, BindingResult bindingResult,
                         @PageableDefault(size = 5) Pageable pageable, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        return searchFacets(model, form, bindingResult, pageable, loggedInUser);
    }

    /**
     * 検索入力フォームに入力した内容をキーワードに検索する
     * 値が入力されている場合: 入力された内容でルシーンから検索
     * 値が入っていない場合: 検索ボタンを選択できない or 全検索
     *
     * @param model
     * @param form
     * @param bindingResult
     * @param pageable
     * @param loggedInUser
     * @return
     */
    @GetMapping(value = "search", params = {"q", "period", "sort", "facets"})
    public String searchFacets(Model model, @Validated DocumentFullSearchForm form, BindingResult bindingResult,
                               @PageableDefault(size = 5) Pageable pageable, @AuthenticationPrincipal LoggedInUser loggedInUser) {



        if (form.getPeriod() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime period = null;
            if (form.getPeriod().equals("year")) {
                period = now.minusYears(1);
            } else if (form.getPeriod().equals("month")) {
                period = now.minusMonths(1);
            } else if (form.getPeriod().equals("week")) {
                period = now.minusWeeks(1);
            } else {
                period = null;
            }
            if (period != null) {
                form.setPeriodDate(period.format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));
            } else {
                form.setPeriodDate(null);
            }
        }

        // 公開区分を格納
        form.setPublicScope(helper.getPublicScopeNumber(loggedInUser));

        SearchResult<DocumentIndex> result = documentFullSearchService.search(form, pageable);

        // 検索にヒットしたものを加工したデータを格納するもの
        List<DocumentIndexSearchRow> hits = new ArrayList<>();
        for (DocumentIndex d : result.hits()) {
            DocumentIndexSearchRow i = beanMapper.map(d, DocumentIndexSearchRow.class);
            if (form.getQ() != null) {
                if (d.getContent() != null) {
                    i.setContentHighlight(documentFullSearchService.highlight(d.getContent(), form.getQ(), "content"));
                    if (i.getContentHighlight().equals("")) {
                        i.setContentHighlight(null);
                    }
                }
                if (d.getBodyPlain() != null) {
                    i.setBodyHighlight(documentFullSearchService.highlight(d.getBodyPlain(), form.getQ(), "bodyPlane"));
                }
            }
            i.setDocCategory1Name(getCodeName(docCategory1, i.getDocCategory1()));
            i.setDocCategory2Name(getCodeName(docCategory2, i.getDocCategory2()));
            i.setDocService1Name(getCodeName(docService1, i.getDocService1()));
            i.setDocService2Name(getCodeName(docService2, i.getDocService2()));
            i.setDocService3Name(getCodeName(docService3, i.getDocService3()));
            hits.add(i);
        }

        // 集約結果を格納
        Map<String, Long> countsByDocCategory1 = result.aggregation(AggregationKey.of("countsByDocCategory1"));
        Map<String, Long> countsByDocCategory2 = result.aggregation(AggregationKey.of("countsByDocCategory2"));

        // ソート機能を実装
        List<Variable> variableCategoryList1 = variableService.findAllByType(docCategory1);
        List<Variable> variableCategoryList2 = variableService.findAllByType(docCategory2);
        // ソート
        sortList(variableCategoryList1, variableCategoryList2);

        List<VariableBean> variableCategoryBeanList = new ArrayList<>();
        for (Variable v1 : variableCategoryList1) {
            if (countsByDocCategory1.get(v1.getCode()) != null) {
                variableCategoryBeanList.add(new VariableBean(v1.getCode(), v1.getValue1(), "1", countsByDocCategory1.get(v1.getCode())));
                for (Variable v2 : variableCategoryList2) {
                    if (Objects.equals(v2.getValue2(), v1.getCode())) {
                        if (countsByDocCategory2.get(v2.getCode()) != null) {
                            variableCategoryBeanList.add(new VariableBean(v2.getCode(), v2.getValue1(), "2", countsByDocCategory2.get(v2.getCode())));
                        }
                    }
                }
            }
        }

        Map<String, Long> countsByDocService1 = result.aggregation(AggregationKey.of("countsByDocService1"));
        Map<String, Long> countsByDocService2 = result.aggregation(AggregationKey.of("countsByDocService2"));
        Map<String, Long> countsByDocService3 = result.aggregation(AggregationKey.of("countsByDocService3"));


        List<Variable> variableServiceList1 = variableService.findAllByType(docService1);
        List<Variable> variableServiceList2 = variableService.findAllByType(docService2);
        List<Variable> variableServiceList3 = variableService.findAllByType(docService3);
        // ソート
        sortList(variableServiceList1, variableServiceList2, variableServiceList3);

        List<VariableBean> variableServiceBeanList = new ArrayList<>();
        for (Variable v1 : variableServiceList1) {
            if (countsByDocService1.get(v1.getCode()) != null) {
                variableServiceBeanList.add(new VariableBean(v1.getCode(), v1.getValue1(), "1", countsByDocService1.get(v1.getCode())));
                for (Variable v2 : variableServiceList2) {
                    if (Objects.equals(v2.getValue2(), v1.getCode())) {
                        if (countsByDocService2.get(v2.getCode()) != null) {
                            variableServiceBeanList.add(new VariableBean(v2.getCode(), v2.getValue1(), "2", countsByDocService2.get(v2.getCode())));
                        }
                        for (Variable v3 : variableServiceList3) {
                            if (Objects.equals(v3.getValue2(), v2.getCode())) {
                                if (countsByDocService3.get(v3.getCode()) != null) {
                                    variableServiceBeanList.add(new VariableBean(v3.getCode(), v3.getValue1(), "3", countsByDocService3.get(v3.getCode())));
                                }
                            }
                        }
                    }
                }
            }
        }

        // ページの表示情報を渡す？
        Page<DocumentIndexSearchRow> page = new PageImpl<>(hits, pageable, result.total().hitCount());

        // Modelに格納
        model.addAttribute("op", new DocumentOperationUtil(BASE_PATH));
        model.addAttribute("page", page);
        model.addAttribute("form", form);
        model.addAttribute("result", result);
        model.addAttribute("hits", hits);
        model.addAttribute("totalHitCount", result.total().hitCount());
        model.addAttribute("variableCategoryBeanList", variableCategoryBeanList);
        model.addAttribute("variableServiceBeanList", variableServiceBeanList);


        return BASE_PATH + "/search";
    }

    /**
     * @param model
     * @param form
     * @param bindingResult
     * @param pageable
     * @param loggedInUser
     * @return
     */
    @GetMapping(value = "search", params = {"q", "period", "sort"})
    public String searchForm(Model model, @Validated DocumentFullSearchForm form, BindingResult bindingResult,
                             @PageableDefault(size = 5) Pageable pageable, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        return searchFacets(model, form, bindingResult, pageable, loggedInUser);
    }

    /**
     * @param type
     * @param code
     * @return
     */
    private String getCodeName(String type, String code) {
        List<Variable> variableList = variableService.findAllByTypeAndCode(type, code);
        String codeName = "";
        for (Variable v : variableList) {
            codeName = v.getValue1();
        }
        return codeName;
    }

    @SafeVarargs
    private void sortList(List<Variable>... list) {
        for (List<Variable> l : list) {
            Comparator<Variable> compare = Comparator.comparing(Variable::getCode);
            l.sort(compare);
        }
    }
}
