package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.InputTag;
import org.project.simproject.repository.entityRepo.InputTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InputTagService {
    private final InputTagRepository inputTagRepository;

    @Transactional
    public void save(List<String> inputTags){
        if(inputTags.size() != 0){
            for(String tag : inputTags){
                if(!inputTagRepository.existsInputTagByName(tag)){
                    inputTagRepository.save(
                            InputTag.builder()
                                    .name(tag)
                                    .build()
                    );
                }
            }
        }
    }
}
