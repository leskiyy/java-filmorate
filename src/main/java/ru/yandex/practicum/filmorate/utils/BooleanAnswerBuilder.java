package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BooleanAnswerBuilder {

    private final String fail = "FAIL";
    private final String success = "SUCCESS";
    private final String addLikeSuccess = "Like to film id=%d was added by user id=%s ";
    private final String addLikeFail = "Like to film id=%d is already added by user id=%s";
    private final String deleteLikeSuccess = "Like to film id=%d was deleted by user id=%s";
    private final String deleteLikeFail = "There is no like to remove for film id=%d added by user id=%d";
    private final String addFriendSuccess = "Users id=%d and id=%d have become friends";
    private final String addFriendFail = "Users id=%d and id=%d are friends already";
    private final String deleteFriendSuccess = "Users id=%d and id=%d are not friends anymore";
    private final String deleteFriendFail = "Users id=%d and id=%d are not friends";

    public Map<String, String> addLikeSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(addLikeSuccess, id, userId));
    }

    public Map<String, String> addLikeFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(addLikeFail, id, userId));
    }

    public Map<String, String> deleteLikeSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(deleteLikeSuccess, id, userId));
    }

    public Map<String, String> deleteLikeFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(deleteLikeFail, id, userId));
    }

    public Map<String, String> addFriendSuccessAnswer(long firstUserId, long secondUserId) {
        return Map.of(success, String.format(addFriendSuccess, firstUserId, secondUserId));
    }

    public Map<String, String> addFriendFailAnswer(long firstUserId, long secondUserId) {
        return Map.of(fail, String.format(addFriendFail, firstUserId, secondUserId));
    }

    public Map<String, String> deleteFriendSuccessAnswer(long firstUserId, long secondUserId) {
        return Map.of(success, String.format(deleteFriendSuccess, firstUserId, secondUserId));
    }

    public Map<String, String> deleteFriendFailAnswer(long firstUserId, long secondUserId) {
        return Map.of(fail, String.format(deleteFriendFail, firstUserId, secondUserId));
    }
}
