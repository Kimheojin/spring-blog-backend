package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Optional<Category> findByCategoryName(String categoryName);

    List<Category> findAllByOrderByPriorityAsc();

}
