package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.BoardDTO;
import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import com.example.SpringGroupBB.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardReplyRepository boardReplyRepository;

  public Board setBoardInput(Board board) {
    return boardRepository.save(board);
  }

  public Board getBoardContent(Long id) {
    //return boardRepository.findById(id).get();
    return boardRepository.findById(id).orElse(null);
  }

  public void setBoardReadNumPlus(Long id) {
    boardRepository.setBoardReadNumPlus(id);
  }

  public Board getPreNextSearch(Long id, String preNext) {
    if(preNext.equals("pre")) {
      return boardRepository.findPrevious(id);
    }
    else {
      return boardRepository.findNext(id);
    }
  }

  public List<BoardReply> getBoardReply(Long boardId) {
    return boardReplyRepository.findByBoardIdOrderById(boardId);
  }

  public void setBoardReplyInput(BoardReply boardReply) {
    boardReplyRepository.save(boardReply);
  }

  public boolean setBoardImageDelete(String content, String realPath) {
    //             0         1         2         3         4         5
    //             012345678901234567890123456789012345678901234567890
    // <img alt="" src="/ckeditorUpload/250916121142_4.jpg" style="height:402px; width:600px" />

    int position = 21;

    // 1. 본문에 src="/로 시작하는 이미지 경로가 없으면(이미지 없는 글이면) 바로 true 리턴, 아래 코드는 더 이상 실행 안 됨
    int srcIndex = content.indexOf("src=\"/");
    if (srcIndex == -1) {
      return true;
    }

    // 2. 첫 번째 이미지의 src="/ 위치에서 이미지 경로 추출을 시작할 nextImg 문자열을 만듦
    String nextImg = content.substring(srcIndex + position);
    boolean sw = true, flag = false;

    while(sw) {
      // 3.1 현재 nextImg에서 src="경로 끝(")의 위치
      int quoteIdx = nextImg.indexOf("\"");
      if (quoteIdx == -1) break; // 다음 큰따옴표가 없으면 종료

      // 3.2 파일명 추출: src=~" 부분을 잘라 이미지 파일 경로로 만듦
      String imgFile = nextImg.substring(0,quoteIdx);
      String origFilePath = realPath + imgFile; // 실제 서버 저장소의 전체 경로

      // 3.3 파일이 있으면 서버에서 삭제
      File delFile = new File(origFilePath);
      if(delFile.exists()) delFile.delete();

      // 3.4 다음 src="/이 더 있는지 확인해서 루프 반복하거나 종료
      int nextSrcIdx = nextImg.indexOf("src=\"/");
      if(nextSrcIdx == -1) sw = false;
      else nextImg = nextImg.substring(nextSrcIdx + position); // 다음 이미지를 위해 nextImg 갱신

      flag = true; // 적어도 한 번은 삭제 시도했음을 표시(실제 사용처에 따라 의미 다를 수 있음)
    }
    return true;
  }

  public void imgBackup(String content) {
    // 이미지 src 찾기
    String searchStr = "src=\"/ckeditorUpload/";
    int pos = 0, fileIdx;
    while ((fileIdx = content.indexOf(searchStr, pos)) != -1) {
      int startIdx = fileIdx + searchStr.length();
      int endIdx = content.indexOf("\"", startIdx);
      if (endIdx == -1) break;

      String imgFileName = content.substring(startIdx, endIdx);

      // 파일 원본 경로와 백업 폴더 지정
      String originFolder = "/var/www/ckeditorUpload/"; // 실제 운영 환경 경로로 수정!
      String backupFolder = "/var/www/ckeditorBackup/"; // 백업 폴더

      File originFile = new File(originFolder + imgFileName);
      File backupFile = new File(backupFolder + imgFileName);

      if (originFile.exists()) {
        // 디렉토리 없으면 생성
        backupFile.getParentFile().mkdirs();

        try {
          // 파일 복사 (overwrite)
          Files.copy(originFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
          // 로그 및 예외 처리
          System.err.println("이미지 백업 실패: " + imgFileName);
          e.printStackTrace();
        }
      }
      pos = endIdx; // 다음 위치로 이동
    }
  }

  // 공지사항 고정
  public List<Board> getBoardListWithNoticeFirst() {
    return boardRepository.findAllWithNoticeFirst();
  }

  public List<BoardDTO> selectSearchStr(String searchStr) {
    return BoardDTO.entityListToDTOList(boardRepository.selectAllSearchStr(searchStr));
  }

  public BoardDTO selectSearchID(Long id) {
    return BoardDTO.entityToDto(boardRepository.findById(id));
  }

  public int searchEmailBoardCnt(String email) {
    return boardRepository.countByMemberEmail(email);
  }

  public int searchEmailBoardReplyCnt(String email) {
    return boardReplyRepository.countByMemberEmail(email);
  }

  @Transactional
  public void ReplyDeleteByBoardId(Long id) {
    boardReplyRepository.deleteByBoardId(id);
  }

  // 공지사항 만료 여부를 Map으로 반환
  public Map<Long, Boolean> getNoticeExpiredMap(List<Board> boardList) {
    Map<Long, Boolean> expiredMap = new HashMap<>();

    if (boardList == null || boardList.isEmpty()) {
      return expiredMap;
    }
    LocalDateTime now = LocalDateTime.now();
    for(Board board : boardList) {
      if("OK".equals(board.getNoticeSw())) {
        long daysDiff = ChronoUnit.DAYS.between(board.getWDate(),now);
        expiredMap.put(board.getId(), daysDiff >= 7);
      } else {
        expiredMap.put(board.getId(), false);
      }
    }
    return expiredMap;
  }
}
