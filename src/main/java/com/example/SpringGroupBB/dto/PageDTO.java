package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.QnA;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO {
  private int pag;
  private int pageSize;
  private int totRecCnt;
  private int totPage;
  private int startIndexNo;
  private int curScrStartNo;
  private int blockSize;
  private int curBlock;
  private int lastBlock;

  private String section;		// 방명록, 게시판, 자료실 등 어떤 곳에서 쓰는지.
  private String part;			// 학습, 여행, 음식 등 말머리.
  private String search;		// title, nickName, content 등.
  private String searchStr;	// 검색어.
  private String searchKr;  // 검색 결과창에 search를 한국어로 주기 위한 변수.
  private String flag;			// flag는 totRecCnt에서 조건을 줄 때 사용한다(7일 이내 새글 등).

  private List<QnA> qnaList;

  private boolean isOwner;  // 본인 인증여부를 확인하기 위한 변수.

  private String progress;  // 선택한 문의현황만 보여주기 위한 변수.
}
