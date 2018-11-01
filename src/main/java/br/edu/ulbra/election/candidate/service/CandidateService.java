package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ModelMapper modelMapper;


    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_VOTER_NOT_FOUND = "Voter not found";

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, ModelMapper modelMapper) {
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
    }

    public List<CandidateOutput> getAll(){
        Type candidateOutputListType = new TypeToken<List<CandidateOutput>>(){}.getType();
        return modelMapper.map(candidateRepository.findAll(), candidateOutputListType);
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
        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput){
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        validateInput(candidateInput);
        Candidate candidate = modelMapper.map(this.getById(candidateId), Candidate.class);

        candidate.setPartyId(candidateInput.getPartyId());
        candidate.setName(candidateInput.getName());
        candidate.setNumberElection(candidateInput.getNumberElection());
        candidate.setElectionId(candidateInput.getElectionId());

        candidate = candidateRepository.save(candidate);
        return modelMapper.map(candidate, CandidateOutput.class);
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
    private void validateInput(CandidateInput candidateInput){

        if(StringUtils.isBlank(candidateInput.getName())){
            throw new GenericOutputException("Name is required");
        }
        if(candidateInput.getElectionId() == null){
            throw new GenericOutputException("Election id is required");
        }
        if(candidateInput.getNumberElection() == null){
            throw new GenericOutputException("Number election is required");
        }
        if(candidateInput.getPartyId() == null){
            throw new GenericOutputException("Party id is required");
        }

    }
}

