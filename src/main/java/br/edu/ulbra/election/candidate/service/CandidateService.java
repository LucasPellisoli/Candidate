package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.client.ElectionClientService;
import br.edu.ulbra.election.candidate.client.PartyClientService;
import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ModelMapper modelMapper;
    private final ElectionClientService electionClientService;
    private final PartyClientService partyClientService;
    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_VOTER_NOT_FOUND = "Voter not found";

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, ModelMapper modelMapper, ElectionClientService electionClientService, PartyClientService partyClientService) {
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
        this.electionClientService = electionClientService;
        this.partyClientService = partyClientService;
    }

    public List<CandidateOutput> getAll(){
        List<Candidate> candidateList = (List<Candidate>) candidateRepository.findAll();
        List<CandidateOutput> candidateOutputList = new ArrayList<>();
        for(Candidate candidate : candidateList){
            candidateOutputList.add(this.mapCanditade(candidate));
        }

        return candidateOutputList;
    }

    public CandidateOutput getById(Long candidateId){
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
        }
        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public CandidateOutput create(CandidateInput candidateInput){
        this.validateInput(candidateInput);
        Candidate candidate = modelMapper.map(candidateInput, Candidate.class);
        candidate = candidateRepository.save(candidate);

        return this.mapCanditade(candidate);
    }

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput){
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        validateInput(candidateInput);
        Candidate candidate = modelMapper.map(this.getById(candidateId), Candidate.class);

        candidate.setName(candidateInput.getName());
        candidate.setPartyId(candidateInput.getPartyId());
        candidate.setName(candidateInput.getName());
        candidate.setNumberElection(candidateInput.getNumberElection());
        candidate.setElectionId(candidateInput.getElectionId());

        return mapCanditade(candidate);
    }

    public GenericOutput delete(Long voterId) {
        if (voterId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Candidate candidate  = candidateRepository.findById(voterId).orElse(null);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
        }

        candidateRepository.delete(candidate);

        return new GenericOutput("Candidate deleted");
    }

    private CandidateOutput mapCanditade(Candidate candidate){
        CandidateOutput candidateOutput = modelMapper.map(candidate,CandidateOutput.class);

        ElectionOutput electionOutput = electionClientService.getById(candidate.getElectionId());
        candidateOutput.setElectionOutput(electionOutput);

        PartyOutput partyOutput = partyClientService.getById(candidate.getPartyId());
        candidateOutput.setPartyOutput(partyOutput);

        return candidateOutput;
    }


    private void validateInput(CandidateInput candidateInput) {

        if (StringUtils.isBlank(candidateInput.getName())) {
            throw new GenericOutputException("Name is required");
        }
        if (candidateInput.getElectionId() == null) {
            throw new GenericOutputException("Election id is required");
        }
        if (candidateInput.getNumberElection() == null) {
            throw new GenericOutputException("Number election is required");
        }
        if (candidateInput.getPartyId() == null) {
            throw new GenericOutputException("Party id is required");
        }
        if (candidateInput.getName().indexOf(" ") == -1) {
            throw new GenericOutputException("Last name is required");
        }

        if (candidateInput.getName().length() < 5) {
            throw new GenericOutputException("min length 5");
        }

        ElectionOutput electionOutput = electionClientService.getById(candidateInput.getElectionId());
        PartyOutput partyOutput = partyClientService.getById(candidateInput.getPartyId());

        if (electionOutput == null) {
            throw new GenericOutputException("Election not found");
        }

        if (partyOutput == null) {
            throw new GenericOutputException("Party not found");
        }
    }
}

