package jp.co.stnet.cms.sales.presentation.controller.document;

import com.github.dozermapper.core.Mapper;
import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.sales.application.service.document.DocumentFullSearchService;
import jp.co.stnet.cms.sales.domain.model.document.DocumentFullSearchForm;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndexSearchRow;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.terasoluna.gfw.common.message.ResultMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.co.stnet.cms.sales.presentation.controller.document.DocumentConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH)
public class DocumentFullSearchController {

    @Autowired
    DocumentFullSearchService documentFullSearchService;

    @Autowired
    Mapper beanMapper;

    @GetMapping("search")
    public String search(Model model, @AuthenticationPrincipal LoggedInUser loggedInUser) {
        return BASE_PATH + "search";
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
    public String search(Model model, @Validated DocumentFullSearchForm form, BindingResult bindingResult,
                         @PageableDefault(size = 5) Pageable pageable, @AuthenticationPrincipal LoggedInUser loggedInUser) {

        // 検索キーワードが入力されていないときにエラー表示 ここはなにかを検索させたほうがいい？
        if (form.getQ() == null) {
            model.addAttribute(ResultMessages.info().add("e.sl.fw.5001"));
            return search(model, loggedInUser);
        }

        SearchResult<DocumentIndex> result = documentFullSearchService.search(form.getQ(), pageable);

        // 検索にヒットしたものを加工したデータを格納するもの
        List<DocumentIndexSearchRow> hits = new ArrayList<>();
        for (DocumentIndex d : result.hits()) {
            DocumentIndexSearchRow i = beanMapper.map(d, DocumentIndexSearchRow.class);
            i.setContentHighlight(documentFullSearchService.highlight(d.getContent(), form.getQ()));
            hits.add(i);
        }

        // 集約結果を格納
        Map<String, Long> countsByGenre = result.aggregation(AggregationKey.of("countsByGenre"));

        // ページの表示情報を渡す？
        Page<DocumentIndexSearchRow> page = new PageImpl<>(hits, pageable, result.total().hitCount());

        Map<String, String> query = new HashMap<>();
        query.put("q", form.getQ());

        // Modelに格納
        model.addAttribute("query", query);
        model.addAttribute("page", page);
        model.addAttribute("q", form.getQ());
        model.addAttribute("result", result);
        model.addAttribute("hits", hits);
        model.addAttribute("totalHitCount", result.total().hitCount());
        model.addAttribute("countsByGenre", countsByGenre);

        return BASE_PATH + "/search";
    }
}
