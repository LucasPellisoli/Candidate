package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
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

    public CandidateOutput create(CandidateInput candidateInput){
        validateInput(candidateInput, false);
        Candidate candidate = modelMapper.map(candidateInput, Candidate.class);
        candidate = candidateRepository.save(candidate);

        return modelMapper.map(candidate, CandidateOutput.class);
    }

    private void validateInput(CandidateInput candidateInput, boolean isUpdate){

    }
}

