package com.demo.dto;

import com.demo.domain.Bookroom;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookRoomInsertDto {
    Long userId;
    Long bookId;
    String themeColor;
    String themeMusicUrl;

    public Bookroom dtoToBookRoom(BookRoomInsertDto bookRoomInsertDto) {
        return Bookroom.builder()
                .userId(bookRoomInsertDto.getUserId())
                .bookId(bookRoomInsertDto.getBookId())
                .themeColor(bookRoomInsertDto.getThemeColor())
                .themeMusicUrl(bookRoomInsertDto.getThemeMusicUrl())
                .build();
    }
}
