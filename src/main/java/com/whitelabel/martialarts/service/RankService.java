package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.Rank;
import java.util.List;

public interface RankService {
    List<Rank> getAllRanks();
    Rank getRankById(Long id);
    Rank createRank(Rank rank);
    Rank updateRank(Long id, Rank rank);
    void deleteRank(Long id);
}
