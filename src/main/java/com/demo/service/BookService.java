package com.demo.service;

import com.demo.domain.*;
import com.demo.dto.*;
import com.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.demo.dto.DefaultFavoriteInsert.dtoToEntity;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookService {
    private final BookRoomRepo bookRoomRepo;
    private final BookDetailsRepo bookDetailsRepo;
    private final FavoriteRepo favoriteRepo;
    private final RoomViewRepo roomViewRepo;
    private final PictureRepo pictureRepo;
    private final WordRepo wordRepo;
    private final MindMapRepo mindMapRepo;
    private final UserCharacterRepo userCharacterRepo;

//    public BookRoomPlusBookDetails getBookRoom(Long userId) {
//        log.info("userId 입력 = {}", userId);
//        BookRoomPlusBookDetails bookRoomPlusBookDetails = bookRoomRepo.findBookTitle(userId);
//        return bookRoomPlusBookDetails;
//    }
    /**
     * 결과값이 안나옴 null로 나옴
     * 나중에 이유를 알게 되면 고치도록 하자.
     * 우선 작동하는 방식으로 하자.
     */

//    public Map<String, Object> getBookRoom(Long bookId, Long userId) {
//        Map<String, Object> returnMap = new HashMap<>();
//
//        Optional<Bookroom> optionalBookroom = bookRoomRepo.findByUserIdAndBookId(bookId, userId);
//        Optional<Bookdetails> optionalBookdetails = bookDetailsRepo.findById(bookId);
//        if (optionalBookdetails.isEmpty() || optionalBookroom.isEmpty())
//            return null;
//        Bookroom bookroom = optionalBookroom.get();
//        Bookdetails bookdetails = optionalBookdetails.get();
//        returnMap.put("bookroom", bookroom);
//        returnMap.put("bookdetails", bookdetails);
//
//        return returnMap;
//    }

    /**
     * View를 이용하는 방식으로 작성함
     * join에서 문제 발생 -> 하나하나 조회는 비효율 -> view 사용
     */


    public Roomview getBookRoom(Long bookId, Long userId) {
        Roomview roomView = roomViewRepo.findByBookIdAndUserId(bookId, userId);
        return roomView;
    }

    public Bookroom saveBookRoom(BookRoomInsertDto bookRoomInsertDto) {
        insertDefaultCharacter(bookRoomInsertDto);
        //기본 캐릭터를 바탕으로 bookroom기본 캐릭터 생성
        updateBookPopularity(bookRoomInsertDto);
        //bookroom을 생성하면서 bookdetails에서 popularity를 +1함

        Bookroom bookroom = bookRoomInsertDto.dtoToBookRoom(bookRoomInsertDto);
        Bookroom save = bookRoomRepo.save(bookroom);
        log.info("bookroom save = {}", save);
        //생성

        insertDefaultFavorite(bookroom);
        //좋아요 표시 안한 상태로 favorite 생성
        return save;
    }

    private void updateBookPopularity(BookRoomInsertDto bookRoomInsertDto) {
        Optional<Bookdetails> optionalBookdetails = bookDetailsRepo.findById(bookRoomInsertDto.getBookId());
        Bookdetails bookdetails = optionalBookdetails.get();
        bookdetails.updatePopularity();
        log.info("bookdetails update 실행");
    }

    private void insertDefaultFavorite(Bookroom bookroom) {
        Favorite favorite = dtoToEntity(bookroom.getUserId(), bookroom.getBookId(), bookroom.getBookroomId());
        Favorite favoriteSave = favoriteRepo.save(favorite);
        log.info("default favorite save = {}", favoriteSave);
    }

    private void insertDefaultCharacter(BookRoomInsertDto bookRoomInsertDto) {
        Usercharacter defaultCharacter = userCharacterRepo.findByUserIdAndBookId(bookRoomInsertDto.getUserId(), 0L);
        Usercharacter usercharacter = DefaultCharacterDto.dtoToEntity(defaultCharacter, bookRoomInsertDto.getBookId());

        Usercharacter characterSave = userCharacterRepo.save(usercharacter);
        log.info("default character save = {}", characterSave);
    }

    public List<Picturetable> getPictureByBookroomId(Long bookroomId) {
        List<Picturetable> picturetables = pictureRepo.findAllByBookroomId(bookroomId);
        return picturetables;
    }
    public Picturetable savePicture(PictureInsertDto pictureInsertDto) {
        Picturetable picturetable = pictureInsertDto.insertDtoToPicturetable(pictureInsertDto);
        Picturetable save = pictureRepo.save(picturetable);
        log.info("picture save = {}", save);
        return save;
    }

    public List<Wordtable> getWordByroomId(Long bookroomId) {
        List<Wordtable> wordtables = wordRepo.findAllByBookroomId(bookroomId);
        return wordtables;
    }
    public Wordtable saveWord(WordInsertDto wordInsertDto) {
        Wordtable wordtable = wordInsertDto.insertDtoToWordtable(wordInsertDto);
        Wordtable save = wordRepo.save(wordtable);
        log.info("word save = {}", save);
        return save;
    }

    public List<Mindmap> getMindmapByBookroomId(Long bookroomId) {
        List<Mindmap> mindmaps = mindMapRepo.findAllByBookroomId(bookroomId);
        return mindmaps;
    }
    public Mindmap saveMind(MindInsertDto mindInsertDto) {
        Mindmap mindmap = mindInsertDto.insertDtoToMindmap(mindInsertDto);
        Mindmap save = mindMapRepo.save(mindmap);
        log.info("mind save = {}", save);
        return save;
    }
}
