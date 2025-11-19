package com.example.SpringGroupBB.common;

import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.entity.MemberEntity;
import com.example.SpringGroupBB.entity.QnA;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.repository.QnARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Pagination {
  private final QnARepository qnaRepository;
  private final MemberRepository memberRepository;

  public PageDTO pagination(PageDTO dto) {	// 각각의 변수로 받으면 초기값처리를 spring이 자동할수 있으나, 객체로 받으면 개별 문자/객체 자료에는 null이 들어오기에 따로 초기화 작업처리해야함.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    int pag = dto.getPag();
    int pageSize = dto.getPageSize() == 0 ? 10 : dto.getPageSize();
    String part = dto.getPart() == null ? "" : dto.getPart();

    int totRecCnt = 0, totPage = 0;

    PageRequest pageable = PageRequest.of(pag, pageSize, Sort.by("id").descending());

    if(dto.getSection().equals("qna")) {
      // 일반회원의 경우 처리중인 것을 처음에 보여준다.
      if(dto.getProgress() == null) dto.setProgress("RESOLVING");

      // 마지막 갱신일 기준으로 내림차순.
      pageable = PageRequest.of(pag, pageSize, Sort.by("lastDate").descending());

      // 문의현황.
      Progress progress;
      if(dto.getProgress().equals("RESOLVING")) progress = Progress.RESOLVING;
      else if(dto.getProgress().equals("RESOLVED")) progress = Progress.RESOLVED;
      else if(dto.getProgress().equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
      else progress = null;

      Page<QnA> page;
      MemberEntity fromEmail;
      if(progress != null) {
        fromEmail = memberRepository.findByEmail(authentication.getName()).orElse(null);
        page = qnaRepository.findByFromEmailAndProgress(fromEmail, progress, pageable);
      }
      else {
        fromEmail = memberRepository.findByEmail(authentication.getName()).orElse(null);
        page = qnaRepository.findByFromEmailAndProgressNot(fromEmail, Progress.ANSWER, pageable);
      }

      dto.setQnaList(page.getContent());

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }
    else if(dto.getSection().equals("qnaAdmin")) {
      // 관리자의 경우 전체 문의를 처음에 보여준다.
      if(dto.getProgress() == null) dto.setProgress("");

      // 마지막 갱신일 기준으로 내림차순.
      pageable = PageRequest.of(pag, pageSize, Sort.by("lastDate").descending());

      // 문의현황.
      Progress progress;
      if(dto.getProgress().equals("RESOLVING")) progress = Progress.RESOLVING;
      else if(dto.getProgress().equals("RESOLVED")) progress = Progress.RESOLVED;
      else if(dto.getProgress().equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
      else progress = null;

      Page<QnA> page;
      if(progress != null) page = qnaRepository.findByProgress(progress, pageable);
      else page = qnaRepository.findAllByProgressNot(Progress.ANSWER, pageable);

      dto.setQnaList(page.getContent());

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }

    int startIndexNo = pag * pageSize;
		int curScrStartNo = totRecCnt - startIndexNo;
		
		int blockSize = 3;
    int curBlock = pag / blockSize;
    int lastBlock = (totPage - 1) / blockSize;
		dto.setPag(pag);
		dto.setPageSize(pageSize);
		dto.setTotRecCnt(totRecCnt);
		dto.setTotPage(totPage);
		dto.setStartIndexNo(startIndexNo);
		dto.setCurScrStartNo(curScrStartNo);
		dto.setBlockSize(blockSize);
		dto.setCurBlock(curBlock);
		dto.setLastBlock(lastBlock);

		if(dto.getSearch() != null) {
			if(dto.getSearch().equals("title")) dto.setSearchKr("글제목");
			else if(dto.getSearch().equals("name")) dto.setSearchKr("글쓴이");
			else if(dto.getSearch().equals("content")) dto.setSearchKr("글내용");
		}
		dto.setSearch(dto.getSearch());
		dto.setSearchStr(dto.getSearchStr());
		
		dto.setPart(part);
		dto.setFlag(dto.getFlag());
		
		return dto;
	}
}
