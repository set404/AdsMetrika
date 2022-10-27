package ru.set404.AdsMetrika.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.set404.AdsMetrika.dto.StatDTO;
import ru.set404.AdsMetrika.models.Stat;
import ru.set404.AdsMetrika.models.User;
import ru.set404.AdsMetrika.repositories.StatsRepository;
import ru.set404.AdsMetrika.services.network.Network;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public StatsService(StatsRepository statsRepository, ModelMapper modelMapper) {
        this.statsRepository = statsRepository;
        this.modelMapper = modelMapper;
    }

    public List<Stat> getStatsList(User user, LocalDate dateStart) {
        return statsRepository.findAllByOwnerAndCreatedDateAfter(user, dateStart);
    }

    public void saveStatDTOList(List<StatDTO> statDTOList, User user, Network network, LocalDate date) {
        List<Stat> statList = new ArrayList<>();
        for (StatDTO statDTO : statDTOList) {
            Stat stat = modelMapper.map(statDTO, Stat.class);
            stat.setNetworkName(network);
            stat.setOwner(user);
            stat.setCreatedDate(date);
            stat.setId(statsRepository.findSimilar(user, stat.getCampaignName(), network, date).orElse(0));
            statList.add(stat);
        }
        statsRepository.saveAll(statList);
    }
}