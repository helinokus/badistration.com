package com.helinok.pzbad_registration.Controllers.User;

import com.helinok.pzbad_registration.Dtos.AcceptPartnershipDto;
import com.helinok.pzbad_registration.Dtos.CreatePartnershipDto;
import com.helinok.pzbad_registration.Dtos.PartnershipInitiatorDto;
import com.helinok.pzbad_registration.Dtos.PartnershipReceiverDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Exceptions.PartnerAlreadyPlayCategoryException;
import com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade.ITournamentRegistrationFacade;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Repositories.PartnershipRepository;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.PartnershipService.IPartnershipService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/partnerships")
@RequiredArgsConstructor
@Slf4j
public class PartnershipController {
    private final IPartnershipService partnershipService;
    private final PartnershipRepository partnershipRepository;
    private final IUserService userService;
    private final ITournamentRegistrationFacade tournamentRegistrationFacade;

    @PostMapping()
    @Operation(summary = "create a new partnership with player")
    public ResponseEntity<ApiResponse> createPartnership(
            @RequestBody CreatePartnershipDto dto,
            Authentication auth) {
        try {
            User currentUser = userService.findUserEntityByEmail(auth.getName());
            tournamentRegistrationFacade.createOnlyPartnership(currentUser.getId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Partnerstwo zostało utworzone"));
        }catch (ConflictException | PartnerAlreadyPlayCategoryException e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/{partnershipId}/accept")
    @Operation(summary = "accept requested partnership")
    public ResponseEntity<ApiResponse> acceptNewPartnership(
            @PathVariable Long partnershipId,
            @RequestBody Set<GameCategories> additionalCategories,
            Authentication auth) {

        try {
            User currentUser = userService.findUserEntityByEmail(auth.getName());
            Partnership partnership = partnershipService.getPartnershipById(partnershipId, currentUser);
            AcceptPartnershipDto acceptPartnershipDto = AcceptPartnershipDto
                    .builder()
                    .partnershipId(partnershipId)
                    .tournamentId(partnership.getTournament().getId())
                    .initiatorId(partnership.getInitiator().getId())
                    .category(partnership.getCategory())
                    .build();
            Set<GameCategories> categories = new HashSet<>(additionalCategories);
            categories.add(partnership.getCategory());

            tournamentRegistrationFacade.acceptNewPartnership(currentUser.getId(), acceptPartnershipDto, categories);
            return ResponseEntity.ok(new ApiResponse("Partnerstwo zostało zaakceptowane"));
        }catch (ConflictException | PartnerAlreadyPlayCategoryException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }

    }

    @PutMapping("/{partnershipId}/decline")
    @Operation(summary = "decline partnership by person who get the invite")
    public ResponseEntity<ApiResponse> declinePartnership(
            @PathVariable Long partnershipId,
            Authentication auth
    ){
        try {

            User currentUser = userService.findUserEntityByEmail(auth.getName());
            Partnership partnership = partnershipService.getPartnershipById(partnershipId, currentUser);
            partnershipService.declinePartnership(partnershipId, auth.getName());
            return ResponseEntity.ok(new ApiResponse("Zaproszenie na partnerstwo zostało odrzucone"));
        }catch (ConflictException | PartnerAlreadyPlayCategoryException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }


    @PutMapping("/{partnershipId}/cancel")
    @Operation(summary = "cancel partnership which i sent")
    public ResponseEntity<ApiResponse> cancelPartnership(
            @PathVariable Long partnershipId,
            Authentication auth
    ){
        try {
            User currentUser = userService.findUserEntityByEmail(auth.getName());
            Partnership partnership = partnershipService.getPartnershipById(partnershipId, currentUser);
            partnershipService.cancelPartnership(partnershipId, auth.getName());
            return ResponseEntity.ok(new ApiResponse("Zaproszenie na partnerstwo zostało usunięte"));
        }catch (ConflictException | PartnerAlreadyPlayCategoryException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/my/sent")
    @Operation(summary = "get all partnerships i sent")
    public ResponseEntity<ApiResponse> getSentPartnerships(Authentication auth) {
        try {
            User currentUser = userService.findUserEntityByEmail(auth.getName());
            List<PartnershipInitiatorDto> allSentPartnerships = partnershipService.getAllSentPartnerships(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(allSentPartnerships));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/my/received")
    @Operation(summary = "get all partnerships sent to me")
    public ResponseEntity<ApiResponse> getReceivedPartnerships(Authentication auth) {
        try {
            User currentUser = userService.findUserEntityByEmail(auth.getName());
            List<PartnershipReceiverDto> allSentPartnerships = partnershipService.getAllReceivedPartnerships(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(allSentPartnerships));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }





}
