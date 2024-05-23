package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Rank;
import com.whitelabel.martialarts.repository.RankRepository;
import com.whitelabel.martialarts.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private RankRepository rankRepository;

    @Override
    public List<Rank> getAllRanks() {
        return rankRepository.findAll();
    }

    @Override
    public Rank getRankById(Long id) {
        return rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found with id: " + id));
    }

    @Override
    public Rank createRank(Rank rank) {
        return rankRepository.save(rank);
    }

    @Override
    public Rank updateRank(Long id, Rank rank) {
        Rank existingRank = rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found with id: " + id));
        existingRank.setName(rank.getName());
        existingRank.setSubRanks(rank.getSubRanks());
        return rankRepository.save(existingRank);
    }

    @Override
    public void deleteRank(Long id) {
        rankRepository.deleteById(id);
    }
}
