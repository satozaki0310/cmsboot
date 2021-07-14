package jp.co.stnet.cms.sales.application.repository.variable;

import jp.co.stnet.cms.base.domain.model.variable.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    List<Variable> findByCode(List<String> code);
}
