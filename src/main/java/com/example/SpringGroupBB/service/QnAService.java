package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.constant.OpenSW;
import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.QnADTO;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.entity.QnA;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.repository.QnARepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnAService {
  @Autowired
  HttpSession session;

  private final QnARepository qnaRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public void insertQnA(QnADTO dto) {
    Member fromMid = memberRepository.findByMid(dto.getFromMid()).orElse(null);
    Member dearMid = memberRepository.findByMid(dto.getDearMid()).orElse(null);
    QnA qna = qnaRepository.save(QnA.dtoToEntity(fromMid, dearMid, dto));
    qna.setParentId(qna.getId());
    qnaRepository.save(qna);
  }

  public List<QnA> selectQnAParentId(Long id) {
    return qnaRepository.findByParentId(id);
  }

  public List<QnA> selectAllQnA(Progress progress) {
    return qnaRepository.findAllByProgressNotOrderByLastDateDesc(progress);
  }

  @Transactional
  public void insertQnAAnswer(Long id, String content) throws Exception {
    QnA parentQnA = qnaRepository.findById(id).orElse(null);
    if(parentQnA.getProgress()!=Progress.RESOLVING) throw new Exception();

    Member dearMid = memberRepository.findByMid(parentQnA.getFromMid().getMid()).orElse(null);
    Member fromMid = memberRepository.findByMid(session.getAttribute("sMid").toString()).orElse(null);
    qnaRepository.save(QnA.builder()
                 .parentId(id)
                 .fromMid(fromMid)
                 .dearMid(dearMid)
                 .title(parentQnA.getTitle())
                 .content(content)
                 .progress(Progress.ANSWER)
                 .build());
    parentQnA.setLastDate(LocalDateTime.now());
    qnaRepository.save(parentQnA);
  }

  public void updateOpenSWOK(QnA qna) {
    qna.setOpenSW(OpenSW.OK);
    qnaRepository.save(qna);
  }

  public void updateOpenSWNO(QnA qna) {
    qna.setOpenSW(OpenSW.NO);
    qnaRepository.save(qna);
  }

  public void updateProgress(Long id, Progress progress) {
    QnA qna = qnaRepository.findById(id).orElse(null);
    qna.setProgress(progress);
    qnaRepository.save(qna);
  }

  public QnADTO selectQnAId(Long id) {
    return QnADTO.entityToDTO(qnaRepository.findById(id).orElse(null));
  }

  public void deleteQnA(Long id) {
    qnaRepository.deleteById(id);
  }
}
