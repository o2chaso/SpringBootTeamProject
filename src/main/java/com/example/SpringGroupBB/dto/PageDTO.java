package com.example.SpringGroupBB.dto;


import com.example.SpringGroupBB.entity.Member;

import com.example.SpringGroupBB.entity.Product;
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

  private String section;	// 'guest/board/pds/member'....
  private String part;		// '학습/여행/음식/기타'...
  private String search;  // '글제목/글쓴이/글내용'
  private String searchString;  // '검색어...'
  private String searchStr; // '글제목/글쓴이/글내용'
  private String boardFlag;	// 검색기에서 글내용보기 호출시 사용하는 변수

  private int level;	// 회원 등급(초기값:99 - 비회원)

  //private List<Board> boardList;  // 게시판의 글 리스트를 저장하기위한 변수

  private List<Member> memberList;
  private List<Product> productList;

  // QnA
  //private List<QnA> qnaList;
  private boolean isOwner;  // 본인 인증여부를 확인하기 위한 변수.
  private String progress;  // 선택한 문의현황만 보여주기 위한 변수.
}
