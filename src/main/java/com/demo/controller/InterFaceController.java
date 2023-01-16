package com.demo.controller;

import com.demo.domain.Bookroom;
import com.demo.domain.Favorite;
import com.demo.domain.Roomview;
import com.demo.dto.BookRoomSelectDto;
import com.demo.domain.Friend;
import com.demo.domain.User;
import com.demo.dto.FavoriteInsertDto;
import com.demo.dto.RecommendBookFavoriteDto;
import com.demo.dto.RecommendFriendDto;
import com.demo.service.BookService;
import com.demo.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.FileVisitOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class InterFaceController {

    @Autowired
    FavoriteService favoriteService;

    @Autowired
    BookService bookService;


    @GetMapping("/book/recommend")
    public void bookRecommend(@RequestParam("user_id") Long user_id) {
        //response dto 필요할까요?
        //추천 동화책 방
        List<BookRoomSelectDto> bookRecommendList = bookService.getRecommendBooks(user_id);
        //model.setAttribute("recommendBookFavoriteDtoList",recommendBookFavoriteDtoList);
    @PostMapping("/book/favorite/{user_id}/{book_id}")
    //책 좋아요 추가
    public Favorite addFavorite(@PathVariable Long user_id, @PathVariable Long book_id){
        Favorite favorite = favoriteService.LikeBooks(user_id,0l,book_id);
        return favorite;
    }
    @PostMapping("book/deletefavorite/{user_id}/{book_id}")
    // 책 좋아요 삭제
    public Favorite deleteFavorite(@PathVariable Long user_id, @PathVariable Long book_id){
        Favorite favorite = favoriteService.DisLikeBooks(user_id,0l,book_id);
        return favorite;
    }
//    @PostMapping("user/addfriend/{user_id}")
//    public Friend addFriend(@PathVariable Long user_id){
//        Friend friend = favoriteService.
//    }
//
//
//    @PostMapping("user/deletefriend/{user_id}")

    @GetMapping("/book/recommend/{user_id}")
    //추천 동화책 방
    public List<RecommendBookFavoriteDto> bookRecommend(@PathVariable Long user_id) {
        List<RecommendBookFavoriteDto> recommendBookFavoriteDtoList = bookService.getRecommendBooks(user_id);

        //user의 추천 친구
        List<RecommendFriendDto> recommendUserList = favoriteService.bookRecommendFriend(user_id);
        //model.setAttribute("recommendUserList",recommendUserList);

        //별표 누른 동화책방 리스트 가져오기
        Optional<Bookroom> bookRoomFavoriteList = favoriteService.getFavoriteBookRooms(user_id);
        //model.setAttribute("bookRoomFavoriteList",bookRoomFavoriteList);

    }

    @GetMapping("/book/readbook")
    //읽어본 동화책 클릭시 favorite table에 담기
    //초기에 읽어본 동화책 클릭시 isfavorite에는 0값 들어감
    //해당 book_id favorite의 popularity 올려야함
    public List<Favorite> bookReadbook(@RequestParam("user_id") Long user_id, @RequestParam("book_id1") Long book_id1, @RequestParam("book_id2") Long book_id2, @RequestParam("book_id3") Long book_id3, @RequestParam("book_id4")Long book_id4, @RequestParam("book_id5") Long book_id5) {
        List<Favorite> favoriteList = new ArrayList<>();
        favoriteList.add(favoriteService.save(user_id,0L,book_id1));
        favoriteList.add(favoriteService.save(user_id,0L,book_id1));
        favoriteList.add(favoriteService.save(user_id,0L,book_id2));
        favoriteList.add(favoriteService.save(user_id,0L,book_id3));
        favoriteList.add(favoriteService.save(user_id,0L,book_id4));
        favoriteList.add(favoriteService.save(user_id,0L,book_id5));

        return favoriteList;
    }

    //동화책방에 들어와서 제목 클릭시 '이런 동화책은 어떤가요?'문구와 함께 보여지는 동화책
    //favorite에 담겨진 동화책 중 가장 선호하는 genre 2가지를 가진 book title 리스트 가져오기
    //book titles를 api검색어로 넣어 book image가져오기
    @GetMapping("/book/recommend/select")
    public List<String> bookRecommendSelect(@RequestParam("user_id") Long user_id) {
        return favoriteService.bookRecommendSelect(user_id);
    }

    //추천 친구 클릭시 로드 되는 메인페이지
//    @GetMapping("/main/friend")
//    public List bookRecommendSelect(@RequestParam("user_id") Long user_id, @RequestParam("friend_id")) {
//        return favoriteService.bookRecommendSelect(user_id);
//    }


    //이미 follow한 친구 클릭시 로드 되는 메인페이지


    //친구가 등록한 동화책방 클릭시
    @GetMapping("/book/friend/bookroom")
    public Roomview bookFriendBookroom(Long notFriendUserId, Long bookId) {
        //별표 확인
        if(favoriteService.checkFavorite(notFriendUserId, bookId)){
            //안드쪽 별표 있게
        }else{
            //안드쪽 별표 없이
        }
        return bookService.selectBookRoom(bookId,notFriendUserId);
    }

    //추천 동화책방 클릭시 로드되는 동화책방
    @GetMapping("/book/recommend/bookroom")
    public Roomview bookRecommendBookroom(Long notFriendUserId, Long bookId) {
        //안드로이드 측 별표 없이
        return bookService.selectBookRoom(bookId,notFriendUserId);
    }

    //즐겨찾기 클릭시 로드 되는 동화책방
    @GetMapping("/book/favorite/bookroom")
    public Roomview bookFavoriteBookroom(Long notFriendUserId, Long bookId) {
        //안드로이드 측 별표 있게
        return bookService.selectBookRoom(bookId,notFriendUserId);
    }

}
