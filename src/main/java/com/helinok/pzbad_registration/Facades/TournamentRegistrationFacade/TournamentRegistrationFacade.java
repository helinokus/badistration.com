package com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade;

import com.helinok.pzbad_registration.Dtos.AcceptPartnershipDto;
import com.helinok.pzbad_registration.Dtos.CreatePartnershipDto;
import com.helinok.pzbad_registration.Dtos.RecentRegistrationDto;
import com.helinok.pzbad_registration.Dtos.TournamentRegistrationDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Exceptions.PartnerAlreadyPlayCategoryException;
import com.helinok.pzbad_registration.Models.*;
import com.helinok.pzbad_registration.Repositories.PartnershipRepository;
import com.helinok.pzbad_registration.Repositories.TournamentCategoryPriceRepository;
import com.helinok.pzbad_registration.Repositories.TournamentRepository;
import com.helinok.pzbad_registration.Repositories.UserTournamentRepository;
import com.helinok.pzbad_registration.Services.TournamentDataExport.PlayerTournamentRegistrationDto;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentRegistrationFacade implements ITournamentRegistrationFacade {
    private final IUserService userService;
    private final ITournamentService tournamentService;
    private final IUserTournamentService userTournamentService;
    private final TournamentRepository tournamentRepository;
    private final UserTournamentRepository userTournamentRepository;
    private final PartnershipRepository partnershipRepository;
    private final TournamentCategoryPriceRepository categoryPriceRepository;

    @Transactional
    @Override
    public void createRegistrationWithPartnerships(User user, Tournament tournament,
                                                   TournamentRegistrationDto request
    ) {

        Set<GameCategories> singleCategories = request.getCategories().stream()
                .filter(cat -> cat.name().charAt(1) == 'S')
                .collect(Collectors.toSet());


        UserTournament registration = userTournamentService.registerUserForTournament(
                user,
                tournament,
                singleCategories
        );
        log.info("User registered successfully, userTournamentId={}", registration.getId());

        log.info("=== REGISTRATION STEP 5: Processing doubles ===");
        if (!request.getDoubles().isEmpty()) {
            log.info("Found {} doubles to process", request.getDoubles().size());
            request.getDoubles()
                    .forEach(d -> {
                        log.info("Processing double: category={}, partnerId={}", d.getGameCategory(), d.getPartnerId());
                        CreatePartnershipDto createPartnershipDto = CreatePartnershipDto.builder()
                                .category(d.getGameCategory())
                                .partnerId(d.getPartnerId())
                                .tournamentId(request.getTournamentId())
                                .build();
                        createPartnership(user.getId(), createPartnershipDto);
                        log.info("Partnership created successfully for category={}", d.getGameCategory());
                    });
        } else {
            log.info("No doubles to process");
        }

        log.info("=== REGISTRATION STEP 6: Registration completed successfully ===");
    }

    @Transactional
    @Override
    public List<String> addNewListCategories(List<GameCategories> categories,
                                             Map<GameCategories, List<Long>> mapDoubledCategoriesToUsers,
                                             Long initiatorUserTournamentId,
                                             String username){
        UserTournament initiatorUserTournament = findById(initiatorUserTournamentId);
        if (!tournamentService.isRegistrationOpen(initiatorUserTournament.getTournament())){
            throw new ConflictException("Tournament is not open");
        }
        isPlayerAuth(username, initiatorUserTournament);
        List<String> listAcceptedResults = new ArrayList<>();
        categories.forEach(category -> {
            if (category.name().length() > 1 && category.name().charAt(1) == 'D') {
                List<Long> partnerIds = mapDoubledCategoriesToUsers.get(category);
                if (partnerIds == null || partnerIds.isEmpty()) {
                    throw new ConflictException("Dla kategorii " + category.name() + " przeba podać co najmniej jednego partnera");
                }
                partnerIds.forEach(partnerId -> {
                    log.info("category=  {}, initUserTournamentId= {}, partnerId = {}", category, initiatorUserTournament.getId(), partnerId);
                    sendInviteForDoubledCategory(category, initiatorUserTournament, partnerId);

                    listAcceptedResults.add("Sent invite to " + partnerId + " for " + category.name());
                });
            }else {
                addSingleCategory(initiatorUserTournamentId, category);
                listAcceptedResults.add("Added single category for " + category.name());
            }
        });

        return listAcceptedResults;
    }


    private void sendInviteForDoubledCategory(GameCategories category, UserTournament initiatorUserTournament, Long partnerId) {
        if (!tournamentRepository.existsById(initiatorUserTournament.getTournament().getId())) {
            throw new NotFoundException("Nie znaleziono turnieju");
        }
        if (!areAvailableFreeCategoriesForUser(initiatorUserTournament))
        {
            throw new ConflictException("Maksymalna liczba kategorii, w której można grać w tym turnieju to: " + initiatorUserTournament.getTournament().getMaxCategoriesToPlay());
        }
        if (!tournamentService.isRegistrationOpen(initiatorUserTournament.getTournament())) {
            throw new ConflictException("Tournament is not open");
        }

        CreatePartnershipDto createPartnershipDto = CreatePartnershipDto.builder()
                .partnerId(partnerId)
                .category(category)
                .tournamentId(initiatorUserTournament.getTournament().getId())
                .build();
        createPartnership(initiatorUserTournament.getUser().getId(), createPartnershipDto);
        log.info("отправил заявку {}", partnerId);
    }

    private void addSingleCategory(Long userTournamentId, GameCategories category) {
        UserTournament userTournament = userTournamentRepository.findById(userTournamentId).orElse(null);
        if (userTournament == null) {
            throw new NotFoundException("Nie jesteś zarejestrowany na ten turniej");
        }
        if (!areAvailableFreeCategoriesForUser(userTournament))
        {
            throw new ConflictException("Maksymalna liczba kategorii, w której można grać w tym turnieju to: " + userTournament.getTournament().getMaxCategoriesToPlay());
        }
        if (!tournamentService.isRegistrationOpen(userTournament.getTournament())) {
            throw new ConflictException("Tournament is not open");
        }
        userTournament.addCategory(category);
        userTournament.setTotalFee(calculateTotalFee(userTournament.getTournament(), userTournament.getGameCategories()));
        userTournamentRepository.save(userTournament);
    }


    @Override
    public void acceptNewAssignToTournament(Long accepterId, AcceptPartnershipDto dto, Set<GameCategories> categories) {
        User partner = userService.findUserEntityById(accepterId);
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());
        userTournamentService.registerUserForTournament(partner, tournament, categories);
    }


    @Override
    public void acceptNewCategoryForPartner(Long accepterId, AcceptPartnershipDto dto) {
        User partner = userService.findUserEntityById(accepterId);
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Tournament is not open");
        }
        addNewCategoryAfterPartnership(partner, tournament, dto.getCategory());
    }

    @Override
    public void acceptNewCategoryForInitiator(AcceptPartnershipDto dto) {
        User initiator = userService.findUserEntityById(dto.getInitiatorId());
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Tournament is not open");
        }
        addNewCategoryAfterPartnership(initiator, tournament, dto.getCategory());
    }

    @Override
    public void addNewCategoryAfterPartnership(User user, Tournament tournament, GameCategories category) {
        UserTournament userTournament = userTournamentRepository.findByUserAndTournament(user, tournament).orElse(null);
        if (userTournament == null) {
            throw new NotFoundException("Nie znalazłem gracza na tym turnieju");
        }
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Tournament is not open");
        }
        if (!userTournament.canAddCategory(category)) {
            throw new ConflictException("Nie mogę dodać tej kategorii do grania");
        }

        addSingleCategory(userTournament.getId(), category);
    }

    @Override
    public void createPartnership(Long initiatorId, CreatePartnershipDto dto) {
        User partner = userService.findUserEntityById(dto.getPartnerId());
        if (partner == null) {
            throw new NotFoundException("User with id " + dto.getPartnerId() + " not found");
        }
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());
        if (tournament == null) {
            throw new NotFoundException("Tournament with id " + dto.getTournamentId() + " not found");
        }
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Rejestracja na ten turniej została zamknięta");
        }
        UserTournament partnerUT = userTournamentService.findUserTournamentByUserIdAndTournamentIdIfExists(partner.getId(), dto.getTournamentId());
        if (partnerUT != null) {
            if (!userTournamentService.areAvailableFreeCategoriesForUser(partnerUT)) {
                throw new ConflictException("Partner uczestniczy w maksymalnej ilości kategorii");
            }
        }

        User initiator = userService.findUserEntityById(initiatorId);
        if (initiator == null) {
            throw new NotFoundException("User with id " + initiatorId + " not found");
        }
        UserTournament initiatorUT = userTournamentService.findUserTournamentByUserIdAndTournamentId(initiatorId, dto.getTournamentId());
        if (!userTournamentService.areAvailableFreeCategoriesForUser(initiatorUT)){
            throw new ConflictException("Initiator uczestniczy w maksymalnej ilości kategorii");
        }

        Partnership partnershipByCategoryAndTournament =
                partnershipRepository.findPartnershipByCategoryAndTournament(
                        dto.getTournamentId(), dto.getCategory(), initiatorId, dto.getPartnerId()
                );

        if (partnershipByCategoryAndTournament != null && partnershipByCategoryAndTournament.getStatus() == PartnershipStatus.PENDING) {
            throw new ConflictException("Zaproszenie zostało już wysłane do partnera");
        }

        if (userTournamentService.isUserAvailableForCategory(partner, tournament, dto.getCategory())) {
            Partnership partnership = new Partnership();
            partnership.setTournament(tournament);
            partnership.setCategory(dto.getCategory());
            partnership.setPlayer1(initiator);
            partnership.setPlayer2(partner);
            partnershipRepository.save(partnership);
        } else {
            throw new PartnerAlreadyPlayCategoryException(partner.getFullName() + " już gra tą kategorię");
        }
    }

    @Override
    @Transactional
    public void createOnlyPartnership(Long initiatorId, CreatePartnershipDto dto) {
        User partner = userService.findUserEntityById(dto.getPartnerId());
        if (partner == null) {
            throw new NotFoundException("User with id " + dto.getPartnerId() + " not found");
        }
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());
        if (tournament == null) {
            throw new NotFoundException("Tournament with id " + dto.getTournamentId() + " not found");
        }
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Tournament if not open");
        }
        UserTournament partnerUT = userTournamentService.findUserTournamentByUserIdAndTournamentIdIfExists(partner.getId(), dto.getTournamentId());
        if (partnerUT != null) {
            if (!userTournamentService.areAvailableFreeCategoriesForUser(partnerUT)) {
                throw new ConflictException("Partner uczestniczy w maksymalnej ilości kategorii");
            }
        }

        User initiator = userService.findUserEntityById(initiatorId);
        if (initiator == null) {
            throw new NotFoundException("User with id " + initiatorId + " not found");
        }
        UserTournament initiatorUT = userTournamentService.findUserTournamentByUserIdAndTournamentId(initiatorId, dto.getTournamentId());
        if (!userTournamentService.areAvailableFreeCategoriesForUser(initiatorUT)){
            throw new ConflictException("Initiator uczestniczy w maksymalnej ilości kategorii");
        }
        log.info("найди тут!!! {}" );

        Partnership partnershipByCategoryAndTournament =
                partnershipRepository.findPartnershipByCategoryAndTournament(
                        dto.getTournamentId(), dto.getCategory(), initiatorId, dto.getPartnerId()
                );
        // Проверка на то, выслано ли партнерство
        if (partnershipByCategoryAndTournament != null && partnershipByCategoryAndTournament.getStatus() == PartnershipStatus.PENDING) {
            throw new ConflictException("Zaproszenie zostało już wysłane do partnera");
        }

        //Проверка на то, играет ли человек уже эту категорю (зарегистрирован ли)?
        if (userTournamentService.isUserAvailableForCategory(partner, tournament, dto.getCategory())) {
            Partnership partnership = new Partnership();
            partnership.setTournament(tournament);
            partnership.setCategory(dto.getCategory());
            partnership.setPlayer1(initiator);
            partnership.setPlayer2(partner);
            partnershipRepository.save(partnership);
        } else {
            throw new PartnerAlreadyPlayCategoryException(partner.getFullName() + " już gra tą kategorię");
        }
    }

    @Override
    @Transactional
    public void acceptNewPartnership(Long accepterId, AcceptPartnershipDto dto, Set<GameCategories> categories){
        User partner = userService.findUserEntityById(accepterId);
        Tournament tournament = tournamentService.findEntityById(dto.getTournamentId());

        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Rejestracja do tego turnieju została zakończona.");
        }
        long activeRegistrations = tournament.getUserTournaments().stream()
                .filter(ut -> !ut.getGameCategories().isEmpty())
                .count();
        if (activeRegistrations >= tournament.getMaxPlayers()){
            throw new ConflictException("Na ten turniej już zarejestrowana maksymalna liczba osób");
        }
        Partnership partnership = partnershipRepository.findPartnershipByCategoryAndTournament(
                dto.getTournamentId(), dto.getCategory(), dto.getInitiatorId(), accepterId
        );
        if (partnership == null) {
            throw new NotFoundException("Nie znalazłem tego partnerstwa");
        }

        // Security check: ensure accepterId matches player2 from partnership
        if (!Objects.equals(partnership.getPlayer2().getId(), accepterId)) {
            throw new ConflictException("You are not allowed to accept this partnership");
        }

        UserTournament partnerUT = userTournamentService.findUserTournamentByUserIdAndTournamentIdIfExists(partner.getId(), dto.getTournamentId());
        if (partnerUT != null) {

            if (!userTournamentService.areAvailableFreeCategoriesForUser(partnerUT)) {
                throw new ConflictException("Partner uczestniczy w maksymalnej ilości kategorii");
            }
        }

        UserTournament initiatorUT = userTournamentService.findUserTournamentByUserIdAndTournamentId(dto.getInitiatorId(), dto.getTournamentId());
        if (!userTournamentService.areAvailableFreeCategoriesForUser(initiatorUT)){
            throw new ConflictException("Initiator uczestniczy w maksymalnej ilości kategorii");
        }

        if (!partnership.isPending()){
            throw new ConflictException("Partnerstwo nie posiada statusu PENDING, tylko ma " +
                    partnership.getStatus());
        }
        // Note: Redundant check removed - already verified above
        if (!Objects.equals(partnership.getPlayer2().getId(), partner.getId())) {
            throw new ConflictException("Id partnera nie zgadzają się");
        }
        //Accept for users
        if (userTournamentService.isUserRegisteredForTournament(partner, tournament)) {
            acceptNewCategoryForPartner(accepterId, dto);
            acceptNewCategoryForInitiator(dto);
        }else {
            acceptNewAssignToTournament(accepterId, dto, categories);
            acceptNewCategoryForInitiator(dto);
        }

        partnership.setStatus(PartnershipStatus.CONFIRMED);
        partnership.setRespondedAt(LocalDateTime.now());
        partnershipRepository.save(partnership);
        User initiator = userService.findUserEntityById(dto.getInitiatorId());
        expireConflictingPartnerships(partner, tournament, dto.getCategory(), partnership);
        expireConflictingPartnerships(initiator, tournament, dto.getCategory(), partnership);
    }

    @Override
    public List<RecentRegistrationDto> getAllRegistrations(Long tournamentId) {
        log.info("getAllRegistrations: Step 1 - Starting for tournament {}", tournamentId);
        List<RecentRegistrationDto> dtos = new ArrayList<>();

        log.info("getAllRegistrations: Step 2 - Finding tournament by id");
        Tournament byId = tournamentService.findEntityById(tournamentId);
        log.info("getAllRegistrations: Step 3 - Found tournament: {}", byId.getNameTournament());

        log.info("getAllRegistrations: Step 4 - Getting tournament registrations");
        List<UserTournament> tournamentRegistrations = userTournamentService.getTournamentRegistrations(byId);
        log.info("getAllRegistrations: Step 5 - Found {} registrations", tournamentRegistrations.size());

        tournamentRegistrations.stream()
                .filter(userTournament -> !userTournament.getGameCategories().isEmpty())
                .forEach(userTournament -> {
            log.info("getAllRegistrations: Processing user {}, id {} ", userTournament.getUser().getFullName(), userTournament.getUser().getId());
           userTournament.getGameCategories().forEach(gameCategory -> {
               log.info("gameCategory {}", gameCategory);
               if (gameCategory.toString().charAt(1) == 'S'){
                   log.info("создаю одиночное ");
                    dtos.add(
                            RecentRegistrationDto.builder()
                            .id(userTournament.getId()+ "_" + gameCategory)
                            .userName(userTournament.getUser().getFirstName() + " " + userTournament.getUser().getLastName())
                            .category(gameCategory)
                            .userId(userTournament.getUser().getId())
                            .registrationTime(userTournament.getRegistrationTime())
                            .userTournamentId(userTournament.getId())
                            .build()
                    );
               }else{
                   log.info("создаю парное");
                   Optional<Partnership> first = userTournament.getTournament().getPartnerships().stream()
                           .filter(e -> e.getStatus().equals(PartnershipStatus.CONFIRMED))
                           .filter(e -> e.getCategory().equals(gameCategory))
                           .filter(e -> Objects.equals(e.getPlayer1().getEmail(), userTournament.getUser().getEmail()))
                           .findFirst();
                   if (first.isPresent()) {
                       Partnership partnership = first.get();
                       log.info("нашел такое партнерство {}, {}, {}", partnership.getCategory(), partnership.getPlayer1().getFullName(), partnership.getPlayer2().getFullName());
                       if (Objects.equals(userTournament.getUser().getId(), partnership.getPlayer2().getId())){
                           return;
                       }
                       dtos.add(
                               RecentRegistrationDto.builder()
                                       .id(userTournament.getId()+ "-" + gameCategory)
                                       .userName(userTournament.getUser().getFirstName() + " " + userTournament.getUser().getLastName())
                                       .partnerName(partnership.getPlayer2().getFirstName() + " " + partnership.getPlayer2().getLastName())
                                       .category(gameCategory)
                                       .userId(userTournament.getUser().getId())
                                       .registrationTime(userTournament.getRegistrationTime())
                                       .userTournamentId(userTournament.getId())
                                       .build());
                   }

               }
               log.info("сейчас у нас такое dto {}", dtos);
           }) ;
        });

        return dtos;
    }

    @Override
    public List<PlayerTournamentRegistrationDto> getAllPlayersInfo(Long tournamentId) {
        List<PlayerTournamentRegistrationDto> dtos = new ArrayList<>();

        Tournament tournament = tournamentService.findEntityById(tournamentId);

        List<UserTournament> tournamentRegistrations = userTournamentService.getTournamentRegistrations(tournament);

        tournamentRegistrations.stream()
                .filter(userTournament -> !userTournament.getGameCategories().isEmpty())
                .forEach(userTournament -> {
                    BigDecimal amountTotal = BigDecimal.valueOf(
                            calculateTotalFee(tournament, userTournament.getGameCategories())
                    );

                    dtos.add(
                            PlayerTournamentRegistrationDto.builder()
                                    .playerId(userTournament.getUser().getId())
                                    .fullName(userTournament.getUser().getFullName())
                                    .email(userTournament.getUser().getEmail())
                                    .categories(userTournament.getGameCategories().stream().map(Enum::toString).collect(Collectors.toList()))
                                    .amount(amountTotal)
                                    .build()
                    );
                });


        return dtos;
    }


    private void expireConflictingPartnerships(User user, Tournament tournament, GameCategories category, Partnership partnership) {
        partnershipRepository.findPartnershipsByTournamentAndPlayerAndCategory(tournament, user, category)
                .stream()
                .filter(p -> p != partnership)
                .filter(p -> p.getStatus() == PartnershipStatus.PENDING)
                .forEach(p -> {
                    p.setStatus(PartnershipStatus.EXPIRED);
                    p.setRespondedAt(LocalDateTime.now());
                    partnershipRepository.save(p);
                });
    }

    // ========== HELPERS ==========

    private UserTournament findById(Long id) {
        return userTournamentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Registration not found with id: " + id));
    }

    private void isPlayerAuth(String username, UserTournament userTournament) {
        User user = userService.findUserEntityByEmail(username);
        if (!Objects.equals(userTournament.getUser(), user)){
            throw new ConflictException("You are not allowed to do this operation");
        }
    }

    private boolean areAvailableFreeCategoriesForUser(UserTournament userTournament) {
        int currentCategoriesCount = userTournament.getGameCategories().size();
        int maxCategories = userTournament.getTournament().getMaxCategoriesToPlay();
        return currentCategoriesCount < maxCategories;
    }

    private Double calculateTotalFee(Tournament tournament, Set<GameCategories> categories) {
        return categories.stream()
                .mapToDouble(category -> categoryPriceRepository
                        .findByTournamentAndCategory(tournament, category)
                        .map(tcp -> tcp.getPrice() != null ? tcp.getPrice().doubleValue() : 0.0)
                        .orElse(0.0))
                .sum();
    }





}
