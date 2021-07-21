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
import jp.co.stnet.cms.sales.domain.model.variable.VariableCategoryBean;
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
import org.terasoluna.gfw.common.message.ResultMessages;

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
    public String search(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser) {
        return BASE_PATH + "/search";
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

        // 検索キーワードが入力されていないときにエラー表示 ここはなにかを検索させたほうがいい？
        if (form.getQ() == null) {
            model.addAttribute(ResultMessages.info().add("e.sl.fw.5001"));
            return search(model, loggedInUser);
        }

        SearchResult<DocumentIndex> result = documentFullSearchService.search(form.getQ(), form.getPeriod(), form.getSort(), form.getFacets(), pageable);

        // 検索にヒットしたものを加工したデータを格納するもの
        List<DocumentIndexSearchRow> hits = new ArrayList<>();
        for (DocumentIndex d : result.hits()) {
            DocumentIndexSearchRow i = beanMapper.map(d, DocumentIndexSearchRow.class);
            i.setContentHighlight(documentFullSearchService.highlight(d.getContent(), form.getQ()));
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

        List<VariableCategoryBean> variableCategoryBeanList = new ArrayList<>();
        for (Variable v1 : variableCategoryList1) {
            if (countsByDocCategory1.get(v1.getCode()) != null) {
                variableCategoryBeanList.add(new VariableCategoryBean(v1.getCode(), v1.getValue1(), "1", countsByDocCategory1.get(v1.getCode())));
                for (Variable v2 : variableCategoryList2) {
                    if (Objects.equals(v2.getValue2(), v1.getCode())) {
                        System.out.println("____" + v2.getCode() + ": " + v2.getValue1() + " " + countsByDocCategory2.get(v2.getCode()));
                        variableCategoryBeanList.add(new VariableCategoryBean(v2.getCode(), v2.getValue1(), "2", countsByDocCategory2.get(v2.getCode())));
                    }
                }
            }
        }

        Map<String, Long> countsByDocService1 = getFacets(docService1, result.aggregation(AggregationKey.of("countsByDocService1")));
        Map<String, Long> countsByDocService2 = getFacets(docService2, result.aggregation(AggregationKey.of("countsByDocService2")));
        Map<String, Long> countsByDocService3 = getFacets(docService3, result.aggregation(AggregationKey.of("countsByDocService3")));

        List<Variable> variableServiceList1 = variableService.findAllByType(docService1);
        List<Variable> variableServiceList2 = variableService.findAllByType(docService2);
        List<Variable> variableServiceList3 = variableService.findAllByType(docService3);

        List<VariableCategoryBean> variableCategoryBeanList2 = new ArrayList<>();
        for (Variable v1 : variableServiceList1) {
            if (countsByDocCategory1.get(v1.getCode()) != null) {
                variableCategoryBeanList2.add(new VariableCategoryBean(v1.getCode(), v1.getValue1(), "1", countsByDocService1.get(v1.getCode())));
                for (Variable v2 : variableServiceList2) {
                    if (Objects.equals(v2.getValue2(), v1.getCode())) {
                        variableCategoryBeanList2.add(new VariableCategoryBean(v2.getCode(), v2.getValue1(), "2", countsByDocService2.get(v2.getCode())));
                        for (Variable v3 : variableServiceList3) {
                            if (Objects.equals(v3.getValue2(), v2.getCode())) {
                                variableCategoryBeanList2.add(new VariableCategoryBean(v3.getCode(), v3.getValue1(), "3", countsByDocService3.get(v3.getCode())));
                            }
                        }
                    }
                }
            }
        }

        // ページの表示情報を渡す？
        Page<DocumentIndexSearchRow> page = new PageImpl<>(hits, pageable, result.total().hitCount());


//        Map<String, String> query = new HashMap<>();
//        query.put("q", form.getQ());

        // Modelに格納
//        model.addAttribute("query", query);
        model.addAttribute("page", page);
//        model.addAttribute("q", form.getQ());
        model.addAttribute("result", result);
        model.addAttribute("hits", hits);
        model.addAttribute("totalHitCount", result.total().hitCount());
        model.addAttribute("variableCategoryBeanList", variableCategoryBeanList);
        model.addAttribute("variableCategoryBeanList2", variableCategoryBeanList2);


        return BASE_PATH + "/search";
    }

    @GetMapping(value = "search", params = {"q", "period", "sort"})
    public String searchForm(Model model, @Validated DocumentFullSearchForm form, BindingResult bindingResult,
                             @PageableDefault(size = 5) Pageable pageable, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        return searchFacets(model, form, bindingResult, pageable, loggedInUser);
    }

    /**
     * @param type
     * @param map
     * @return
     */
    private Map<String, Long> getFacets(String type, Map<String, Long> map) {
        Map<String, Long> facets = new HashMap<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            List<Variable> codeName = variableService.findAllByTypeAndCode(type, entry.getKey());
            for (Variable v : codeName) {
                facets.put(v.getValue1(), entry.getValue());
            }
        }
        return facets;
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
}
