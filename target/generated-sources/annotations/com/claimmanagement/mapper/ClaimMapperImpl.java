package com.claimmanagement.mapper;

import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.Claim;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T21:26:45+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClaimMapperImpl implements ClaimMapper {

    @Override
    public Claim toEntity(ClaimRequestDto requestDto) {
        validateRequest( requestDto );

        if ( requestDto == null ) {
            return null;
        }

        Claim claim = new Claim();

        claim.setPolicyNumber( requestDto.getPolicyNumber() );
        claim.setClaimantName( requestDto.getClaimantName() );
        claim.setClaimantEmail( requestDto.getClaimantEmail() );
        claim.setClaimantPhone( requestDto.getClaimantPhone() );
        claim.setDescription( requestDto.getDescription() );
        claim.setClaimAmount( requestDto.getClaimAmount() );
        claim.setStatus( requestDto.getStatus() );
        claim.setIncidentDate( requestDto.getIncidentDate() );

        setDefaultValues( requestDto, claim );

        return claim;
    }

    @Override
    public ClaimResponseDto toResponseDto(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimResponseDto claimResponseDto = new ClaimResponseDto();

        claimResponseDto.setId( claim.getId() );
        claimResponseDto.setClaimNumber( claim.getClaimNumber() );
        claimResponseDto.setPolicyNumber( claim.getPolicyNumber() );
        claimResponseDto.setClaimantName( claim.getClaimantName() );
        claimResponseDto.setClaimantEmail( claim.getClaimantEmail() );
        claimResponseDto.setClaimantPhone( claim.getClaimantPhone() );
        claimResponseDto.setDescription( claim.getDescription() );
        claimResponseDto.setClaimAmount( claim.getClaimAmount() );
        claimResponseDto.setStatus( claim.getStatus() );
        claimResponseDto.setIncidentDate( claim.getIncidentDate() );
        claimResponseDto.setCreatedAt( claim.getCreatedAt() );
        claimResponseDto.setUpdatedAt( claim.getUpdatedAt() );

        return claimResponseDto;
    }

    @Override
    public List<ClaimResponseDto> toResponseDtoList(List<Claim> claims) {
        if ( claims == null ) {
            return null;
        }

        List<ClaimResponseDto> list = new ArrayList<ClaimResponseDto>( claims.size() );
        for ( Claim claim : claims ) {
            list.add( toResponseDto( claim ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(ClaimRequestDto requestDto, Claim claim) {
        validateRequest( requestDto );

        if ( requestDto == null ) {
            return;
        }

        if ( requestDto.getPolicyNumber() != null ) {
            claim.setPolicyNumber( requestDto.getPolicyNumber() );
        }
        if ( requestDto.getClaimantName() != null ) {
            claim.setClaimantName( requestDto.getClaimantName() );
        }
        if ( requestDto.getClaimantEmail() != null ) {
            claim.setClaimantEmail( requestDto.getClaimantEmail() );
        }
        if ( requestDto.getClaimantPhone() != null ) {
            claim.setClaimantPhone( requestDto.getClaimantPhone() );
        }
        if ( requestDto.getDescription() != null ) {
            claim.setDescription( requestDto.getDescription() );
        }
        if ( requestDto.getClaimAmount() != null ) {
            claim.setClaimAmount( requestDto.getClaimAmount() );
        }
        if ( requestDto.getStatus() != null ) {
            claim.setStatus( requestDto.getStatus() );
        }
        if ( requestDto.getIncidentDate() != null ) {
            claim.setIncidentDate( requestDto.getIncidentDate() );
        }

        setDefaultValues( requestDto, claim );
    }

    @Override
    public ClaimResponseDto toStatusResponseDto(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimResponseDto claimResponseDto = new ClaimResponseDto();

        claimResponseDto.setId( claim.getId() );
        claimResponseDto.setClaimNumber( claim.getClaimNumber() );
        claimResponseDto.setPolicyNumber( claim.getPolicyNumber() );
        claimResponseDto.setClaimantName( claim.getClaimantName() );
        claimResponseDto.setClaimantEmail( claim.getClaimantEmail() );
        claimResponseDto.setClaimAmount( claim.getClaimAmount() );
        claimResponseDto.setStatus( claim.getStatus() );
        claimResponseDto.setIncidentDate( claim.getIncidentDate() );
        claimResponseDto.setCreatedAt( claim.getCreatedAt() );
        claimResponseDto.setUpdatedAt( claim.getUpdatedAt() );

        return claimResponseDto;
    }
}
