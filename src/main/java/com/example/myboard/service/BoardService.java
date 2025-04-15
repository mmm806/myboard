package com.example.myboard.service;

import com.example.myboard.dao.BoardDao;
import com.example.myboard.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDao boardDao;

    @Transactional
    public void addBoard(int userId, String title, String content) {
        boardDao.addBoard(userId, title, content);
    }


    @Transactional(readOnly = true) // select할때 성능을 높이기 위해서 넣어줌
    public int getTotalCount() {
        return boardDao.getTotalCount();
    }


    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        return boardDao.getBoards(page);
    }


    @Transactional
    public Board getBoard(int boardId) {
        Board board = boardDao.getBoard(boardId);
        boardDao.updateViewCnt(boardId);
        return board;
    }


    @Transactional
    public Board getBoard(int boardId, boolean updateViewCnt) {
        Board board = boardDao.getBoard(boardId);
        if (updateViewCnt) {
            boardDao.updateViewCnt(boardId);
        }
        return board;
    }



    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardDao.getBoard(boardId);
        if (board.getUserId() == userId) {
            boardDao.deleteBoard(boardId);
        }
    }


    @Transactional
    public void deleteBoard(int boardId) {
        boardDao.deleteBoard(boardId);
    }


    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }

}
