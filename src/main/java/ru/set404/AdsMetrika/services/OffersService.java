package ru.set404.AdsMetrika.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.set404.AdsMetrika.dto.OfferDTO;
import ru.set404.AdsMetrika.dto.OfferListDTO;
import ru.set404.AdsMetrika.models.Offer;
import ru.set404.AdsMetrika.models.User;
import ru.set404.AdsMetrika.repositories.OffersRepository;

import java.util.List;

@Service
public class OffersService {
    private final OffersRepository offersRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OffersService(OffersRepository offersRepository, ModelMapper modelMapper) {
        this.offersRepository = offersRepository;
        this.modelMapper = modelMapper;
    }

    public List<Offer> getUserOffersList(User user) {
        return offersRepository.findByOwner(user);
    }

    public OfferDTO getById(User user, int id) {
        if (offersRepository.existsOfferByOwnerAndId(user, id))
            return modelMapper.map(offersRepository.findById(id).orElse(new Offer()), OfferDTO.class);
        return new OfferDTO();
    }

    public OfferListDTO getOfferListDTO(User user) {
        return new OfferListDTO(offersRepository.findByOwner(user).stream()
                .map(offer -> modelMapper.map(offer, OfferDTO.class)).toList());
    }

    public void saveOffersDTOList(OfferListDTO offerListDTO, User user) {
        for (OfferDTO offerDTO : offerListDTO.getOffers()) {
            if (offerDTO.getGroupName() != null && !offerDTO.getGroupName().isEmpty()) {
                Offer offer = modelMapper.map(offerDTO, Offer.class);
                offer.setOwner(user);
                offer.setId(offersRepository.findIdByParameters(user, offer.getAdcomboNumber(), offer.getGroupName(),
                        offer.getNetworkName()).orElse(0));
                offersRepository.save(offer);
            }
        }
    }

    public void deleteById(User user, int id) {
        if (offersRepository.existsOfferByOwnerAndId(user, id))
            offersRepository.deleteById(id);
    }
}