package com.example.SpringGroupBB.common;


import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.entity.LoginHistory;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.entity.Product;
import com.example.SpringGroupBB.repository.LoginHistoryRepository;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Pagination {

  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final LoginHistoryRepository loginHistoryRepository;



  public PageDTO pagination(PageDTO dto) {	// 각각의 변수로 받으면 초기값처리를 spring이 자동할수 있으나, 객체로 받으면 개별 문자/객체 자료에는 null이 들어오기에 따로 초기화 작업처리해야함.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    int pag = dto.getPag();
    int pageSize = dto.getPageSize() == 0 ? 10 : dto.getPageSize();
    String part = dto.getPart() == null ? "" : dto.getPart();


    int totRecCnt = 0, totPage = 0;

    PageRequest pageable = PageRequest.of(pag, pageSize, Sort.by("id").descending());

    if(dto.getSection().equals("member")) {
      Page<Member> page;
      if(dto.getSearch() != null && !dto.getSearch().isEmpty()) {
        if(dto.getSearch().equals("email")) page = memberRepository.findByEmailContaining(dto.getSearchString(), pageable);
        else page = memberRepository.findByNameContaining(dto.getSearchString(), pageable);
      }
      else page = memberRepository.findAll(pageable);

      dto.setMemberList(page.getContent());
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();

    }
    else if(dto.getSection().equals("product")) {
      Page<Product> page;

      if(dto.getSearch() != null && !dto.getSearch().isEmpty()) {
        if(dto.getSearch().equals("sensorName")) page = productRepository.findBySensorNameContaining(dto.getSearchString(), pageable);
        else page = productRepository.findByManufacturerContaining(dto.getSearchString(), pageable);
      }
      else page = productRepository.findAll(pageable);
      dto.setProductList(page.getContent());

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }
    else if(dto.getSection().equals("History")) {
      LocalDateTime startDateTime = dto.getStartDate() != null ? dto.getStartDate().atStartOfDay() : null;
      LocalDateTime endDateTime = dto.getEndDate() != null ? dto.getEndDate().atTime(23, 59, 59) : null;
      Page<LoginHistory> page;
      switch (dto.getSearchStr() != null ? dto.getSearchStr() : "") {
        case "name":
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByMember_NameContainingAndCreateDateBetween(dto.getSearchString(), startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findByMember_NameContaining(dto.getSearchString(), pageable);
          break;

        case "email":
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByMember_EmailContainingAndCreateDateBetween(dto.getSearchString(), startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findByMember_EmailContaining(dto.getSearchString(), pageable);
          break;

        default:
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByCreateDateBetween(startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findAll(pageable);
          break;
      }
      dto.setLoginHistoryList(page.map(LoginHistoryDTO::entityToDto).getContent());
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }

    int startIndexNo = pag * pageSize;
		int curScrStartNo = totRecCnt - startIndexNo;

		int blockSize = 3;
    int curBlock = ((pag + 1) - 1) / blockSize;
    int lastBlock = (totPage - 1) / blockSize;
    dto.setPag(pag+1);
    dto.setPageSize(pageSize);
    dto.setTotRecCnt(totRecCnt);
    dto.setTotPage(totPage);
    dto.setStartIndexNo(startIndexNo);
    dto.setCurScrStartNo(curScrStartNo);
    dto.setBlockSize(blockSize);
    dto.setCurBlock(curBlock);
    dto.setLastBlock(lastBlock);

		if(dto.getSearch() != null) {
			if(dto.getSearch().equals("title")) dto.setSearch("글제목");
			else if(dto.getSearch().equals("name")) dto.setSearch("글쓴이");
			else if(dto.getSearch().equals("content")) dto.setSearch("글내용");
		}
		dto.setSearch(dto.getSearch());
		dto.setSearchStr(dto.getSearchStr());

		dto.setPart(part);
		dto.setBoardFlag(dto.getBoardFlag());

		return dto;
	}
}
