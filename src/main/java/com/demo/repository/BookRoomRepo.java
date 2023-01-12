package com.demo.repository;

import com.demo.domain.BookRoomPlusBookDetails;
import com.demo.domain.Bookroom;
import com.demo.dto.RecommendBookFavoriteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRoomRepo extends JpaRepository<Bookroom, Long> {
    @Query(value = "select br.bookroom_id, br.book_id, br.theme_color, br.theme_music_url, br.user_id, bd.book_title as Bookdetails "
            + "from Bookdetails bd JOIN Bookroom br ON br.book_id = bd.book_id "
            + "where br.user_id = :userId", nativeQuery = true)
    List<BookRoomPlusBookDetails> findBookTitle(@Param("userId") Long userId);
    /**
     * 결과값이 안나옴 null로 나옴
     * 나중에 이유를 알게 되면 고치도록 하자.
     * 우선 작동하는 방식으로 하자.
     */
}
