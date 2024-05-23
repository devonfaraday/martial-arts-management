package com.whitelabel.martialarts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.whitelabel.martialarts.model.Rank;

public interface RankRepository extends JpaRepository<Rank, Long>  {
}
