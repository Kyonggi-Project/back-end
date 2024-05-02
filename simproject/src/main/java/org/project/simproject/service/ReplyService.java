package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.OTTReview;
import org.project.simproject.domain.Reply;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddReplyRequest;
import org.project.simproject.dto.request.ModifyReplyRequest;
import org.project.simproject.dto.response.ReplyResponse;
import org.project.simproject.repository.entityRepo.ReplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;

    @Transactional
    public Reply save(AddReplyRequest request, OTTReview ottReview, User user){
        return replyRepository.save(request.toEntity(ottReview, user));
    }

    public Reply findById(Long id){
        return replyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reply Not Found"));
    }

    public List<ReplyResponse> findByOTTReviewId(OTTReview ottReview){
        return replyRepository.findReplyByOttReviewId(ottReview)
                .stream()
                .map(ReplyResponse::new)
                .toList();
    }

    @Transactional
    public Reply modify(ModifyReplyRequest request, Long replyId, User user){
        Reply reply = replyRepository.findById(replyId)
                        .orElseThrow(() -> new IllegalArgumentException("Reply Not Found"));
        authorizeReplyAuthor(reply, user);

        reply.modify(request);

        return reply;
    }

    @Transactional
    public void delete(Long replyId, User user){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Reply Not Found"));
        authorizeReplyAuthor(reply, user);

        replyRepository.delete(reply);
    }

    // 현재 사용자와 리뷰 작성자가 일치하는지 검사
    private static void authorizeReplyAuthor(Reply reply, User user){
        if(!reply.getUserId().getNickname().equals(user.getNickname())){
            throw new IllegalArgumentException("Not Authorization User");
        }
    }
}
