package com.demo.service;

import com.demo.dao.BookdetailsDao;
import com.demo.dao.BookroomDao;
import com.demo.dao.FavoriteDao;
import com.demo.dao.UserDao;
import com.demo.domain.Bookroom;
import com.demo.domain.Favorite;
import com.demo.domain.Friend;
import com.demo.domain.User;
import com.demo.dto.BookRoomSelectDto;
import com.demo.dto.FavoriteInsertDto;
import com.demo.dto.FriendDto;
import com.demo.dto.RecommendFriendDto;
import com.demo.domain.*;
import com.demo.dto.*;
import com.demo.dto.response.Response;
import com.demo.repository.BookRoomRepo;
import com.demo.repository.FavoriteRepo;
import com.demo.repository.FriendRepo;
import com.demo.repository.UserRepo;
import org.apache.ibatis.annotations.Param;
import com.demo.repository.RoomViewRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.demo.domain.responseCode.ResponseCodeMessage.*;
import static com.demo.domain.responseCode.ResponseCodeMessage.BOOKROOMSELECTERRORMESSAGE;

@Service
public class FavoriteService {
    @Autowired
    RoomViewRepo roomViewRepo;
    @Autowired
    BookroomDao bookroomDao;

    @Autowired
    FriendRepo friendRepo;

    @Autowired
    FavoriteRepo favoriteRepo;

    @Autowired
    BookRoomRepo bookroomRepo;

    @Autowired
    FavoriteDao favoriteDao;

    @Autowired
    BookdetailsDao bookdetailsDao;

    @Autowired
    UserDao userDao;

    //책 좋아요
    public Favorite likeBooks(Long userId, Long bookId){
        Favorite favorite = favoriteRepo.Like(userId,bookId);

        return  favorite;
    }
    //책 좋아요 취소
    public  Favorite disLikeBooks(Long userId, Long bookId){
        Favorite favorite = favoriteRepo.DisLike(userId,bookId);

        return favorite;
    }

    public Favorite save(Long user_id, Long bookroom_id, Long book_id) {
        //책을 담을때 popularity올려야함-> dao에서 해결 -> 그린님과 공통되는 부분 ?? popularity는 책방에 담을 때 올리는 것 아닌가요?!
        FavoriteInsertDto favoriteInsertDto = new FavoriteInsertDto(user_id,bookroom_id,book_id,0);
        Favorite favorite = favoriteRepo.save(favoriteInsertDto.FavoriteDtoToFavorite());

        return favorite;
    }

    //동화책 등록시 추천 동화책
    public Set<String> bookRecommendSelect(Long user_id) {
        //읽어본 동화책, 좋아요를 눌러둔 동화책의 같은 장르의 동화책을 추천
        List<String> getBookGenre = bookdetailsDao.myFavoriteGenreByExperience(user_id);

        //해당 장르 중 가장 많이 나온 장르를 뽑기
        Map<String, Integer> genreCount = new HashMap<>();
        for(String genreStr:getBookGenre){
            if(!genreCount.containsKey(genreStr))
                genreCount.put(genreStr,0);
            genreCount.put(genreStr,genreCount.get(genreStr)+1);
        }
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(genreCount.entrySet());

        // 비교함수 Comparator를 사용하여 내림차순 정렬
        Collections.sort(list_entries, new Comparator<Map.Entry<String, Integer>>() {
            // compare로 값을 비교
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                // 오름 차순 정렬
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        List<String> resultList = new ArrayList<>();
        resultList = bookdetailsDao.getBookTitleByBookGere(list_entries.get(0).getKey(),user_id);
        resultList.addAll(bookdetailsDao.getBookTitleByBookGere(list_entries.get(1).getKey(),user_id));
        Set<String> resultSet = new HashSet<>();
        for(String s:resultList){
            resultSet.add(s);
        }
        return resultSet;

    }

    public Response getResult(Long userId) {
        Map<String, Object> result = new HashMap<>();
        Response response = new Response();
        Set<BookRoomSelectDto> recommendBookFavoriteDtoList = getRecommendBooks(userId);
        Set<RecommendFriendDto> recommendUserList = bookRecommendFriend(userId);
        Optional<Bookroom> bookRoomFavoriteList = getFavoriteBookRooms(userId);

        if (recommendUserList == null && recommendUserList == null && bookRoomFavoriteList == null) {
            response.setCode(NULLCODE);
            response.setMessage(NULLMESSAGE);
            return response;
        } else {

            result.put("recommendBookFavoriteDtoList", recommendBookFavoriteDtoList);
            result.put("recommendUserList", recommendUserList);
            result.put("bookRoomFavoriteList", bookRoomFavoriteList);

            return new Response(result, SUCCESSMESSAGE, SUCCESSCODE);
        }
    }
    private Set<BookRoomSelectDto> getRecommendBooks(Long id){
        //유저가 좋아요를 눌러논 동화책방의 주인이 등록한 다른 동화책방을 추천으로 주기 -> null일 경우 고려
        List<BookRoomSelectDto> bookRoomSelectDtoList = bookroomDao.getBookroomByFavorite(id);
        if(bookRoomSelectDtoList == null){
            bookRoomSelectDtoList = bookroomDao.getBookroomByExperience(id);
        }
        else{
            bookRoomSelectDtoList.addAll(bookroomDao.getBookroomByExperience(id));
        }
        System.out.println(bookRoomSelectDtoList);

        Set<BookRoomSelectDto> bookRoomSelectDtos = new HashSet<>();
        if(bookRoomSelectDtoList == null){
            //null일경우 아무것도 보내주지 않게 하기
            return bookRoomSelectDtos;
        }
        else {
            for (BookRoomSelectDto brs : bookRoomSelectDtoList) {
                bookRoomSelectDtos.add(brs);
            }
            return bookRoomSelectDtos;
        }
    }
        /**
         * 이 부분에서 null인 경우에 add를 하려하면 NullPointException이 발생하길래 우선 조건문을 써서 막아뒀어요
         */

        //유저에게 추천 친구
        private Set<RecommendFriendDto> bookRecommendFriend(Long user_id) {
            List<RecommendFriendDto> recommendUserList  = new ArrayList<>();

            if (userDao.recommendFriendByFavoriteExperience(user_id) != null)
                recommendUserList = userDao.recommendFriendByFavoriteExperience(user_id);
            if (userDao.recommendFriendBySameAge(user_id) != null)
                recommendUserList.addAll(userDao.recommendFriendBySameAge(user_id));
            if (userDao.recommendFriendBySameBook(user_id) != null)
                recommendUserList.addAll(userDao.recommendFriendBySameBook(user_id));
            if (userDao.recommendFriendBySameFavoriteBook(user_id) != null)
                recommendUserList.addAll(userDao.recommendFriendBySameFavoriteBook(user_id));

            Set<RecommendFriendDto> resultSet = new HashSet<>();
            for(RecommendFriendDto rfd:recommendUserList){
                Friend friendDto = new FriendDto(user_id,rfd.getUserId()).FriendDto();
                if(rfd.getUserId() == user_id && friendRepo.exists(friendDto));
                    continue;
                resultSet.add(rfd);
            }
            return resultSet;
        }

        /**
         * 여기도 위와 같이 null일 경우 오류가 발생할 것 같기도 한데, null이 안나와서..
         * 나중에 필요하면 처리!
         */

        private List<Bookroom> getFavoriteBookRooms(Long user_id) {
//        Optional<Bookroom> getFavoriteBookRoomList;
//        getFavoriteBookRoomList = bookroomRepo.findById(user_id);
            List<Bookroom> getFavoriteBookRoomList = new ArrayList<>();
            /**
             * 이건 그냥 본인의 책방 리스트를 가져오는 중입니다 -> 좋아요 누른 책방이 아님
             * 그리고 findById는 해당 pk값으로 가져오는 것입니다.
             * userId를 사용하기 위해서는 findByUserId라는 메소드를 만들고 사용하면 됩니다.
             *
             * 우선 제 나름대로 수정했습니다.
             * favorite에서 bookroomId를 가져와서 그 bookroomId를 통해 bookroom조회하여 리스트로 만들어 반환합니다.
             */
            List<Long> bookroomIds = favoriteRepo.findBookroomIdByUserId(user_id);
            for (Long bookroomId : bookroomIds) {
                Optional<Bookroom> byId = bookroomRepo.findById(bookroomId);
                if (byId.isPresent())
                    getFavoriteBookRoomList.add(byId.get());
            }

            return getFavoriteBookRoomList;
        }

        public Response checkFavorite(Long userId, Long friendUserId, Long bookId) {
            Roomview roomview = roomViewRepo.findByBookIdAndUserId(bookId, friendUserId);

            Boolean isFavorite = false;
            if (favoriteRepo.findByUserIdAndBookroomId(userId, roomview.getBookroomId()).isEmpty())
                isFavorite = false;

            BookRoomResponse result = new BookRoomResponse(roomview.getBookroomId(), roomview.getUserId(), roomview.getBookId(), roomview.getCharacterId(),
                    roomview.getThemeColor(), roomview.getThemeMusicUrl(), roomview.getBookTitle(), isFavorite, roomview.getGender(), roomview.getNickname(),
                    roomview.getHeadStyle(), roomview.getHeadColor(), roomview.getTopStyle(), roomview.getTopColor(), roomview.getPantsStyle(), roomview.getPantsColor(),
                    roomview.getShoesStyle(), roomview.getShoesColor(), roomview.getFaceColor(), roomview.getFaceStyle());

            return new Response(result, SUCCESSMESSAGE, SUCCESSCODE);
        }
    }

