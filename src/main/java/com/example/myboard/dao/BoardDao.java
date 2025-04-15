package com.example.myboard.dao;

import com.example.myboard.dto.Board;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertBoard;

    public BoardDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("board")
                .usingGeneratedKeyColumns("board_id");
    }


    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        insertBoard.execute(params);
    }


    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board"; // 무조건 한건의 데이터가 나온다.
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue();
    }


    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        int offset = (page - 1) * 10;
        int limit = 10;

        String sql = "SELECT b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name " +
                "FROM board b JOIN \"user\" u ON b.user_id = u.user_id " +
                "ORDER BY b.board_id DESC " +
                "LIMIT :limit OFFSET :offset";

        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);

        Map<String, Object> params = Map.of("limit", limit, "offset", offset);
        List<Board> list = jdbcTemplate.query(sql, params, rowMapper);
        return list;
    }



    @Transactional(readOnly = true)
    public Board getBoard(int boardId) {
        String sql = "SELECT b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content " +
                "FROM board b, \"user\" u " +
                "WHERE b.user_id = u.user_id AND b.board_id = :boardId";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }



    @Transactional
    public void updateViewCnt(int boardId) {
        String sql = "update board " +
                "set view_cnt = view_cnt + 1 " +
                "where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }


    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "DELETE FROM board WHERE board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }


    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        String sql = "update board\n" +
                "set title = :title , content = :content\n" +
                "where board_id = :boardId";
        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params =  new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, params);
//        jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content));
    }
}
