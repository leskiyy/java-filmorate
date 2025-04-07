package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BooleanAnswerBuilder {

    private final String fail = "FAIL";
    private final String success = "SUCCESS";
    private final String addLikeSuccess = "Like to film id=%d was added by user id=%d ";
    private final String addLikeFail = "Like to film id=%d is already added by user id=%d";
    private final String deleteLikeSuccess = "Like to film id=%d was deleted by user id=%d";
    private final String deleteLikeFail = "There is no like to remove for film id=%d added by user id=%d";
    private final String addFriendSuccess = "Users id=%d and id=%d have become friends";
    private final String addFriendFail = "Users id=%d and id=%d are friends already";
    private final String deleteFriendSuccess = "Users id=%d and id=%d are not friends anymore";
    private final String deleteFriendFail = "Users id=%d and id=%d are not friends";
    private final String deleteReviewSuccess = "Review id=%d was deleted";
    private final String deleteReviewFail = "Review id=%d can't be deleted";
    private final String addLikeToReviewSuccess = "Like to review id=%d was added by user id=%d";
    private final String addLikeToReviewFail = "Like to review id=%d can't be added by user id=%d";
    private final String addDislikeToReviewSuccess = "Dislike to review id=%d was added by user id=%d";
    private final String addDislikeToReviewFail = "Dislike to review id=%d can't be added by user id=%d";
    private final String deleteLikeOrDislikeToReviewSuccess =
            "Like or dislike to review id=%d was deleted by user id=%d";
    private final String deleteLikeOrDislikeToReviewFail =
            "Like or dislike to review id=%d can't be deleted by user id=%d";
    private final String deleteDislikeToReviewSuccess = "Dislike to review id=%d was deleted by user id=%d";
    private final String deleteDislikeToReviewFail = "Dislike to review id=%d can't be deleted by user id=%d";


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

    public Map<String, String> deleteReviewSuccessAnswer(long id) {
        return Map.of(success, String.format(deleteReviewSuccess, id));
    }

    public Map<String, String> deleteReviewFailAnswer(long id) {
        return Map.of(fail, String.format(deleteReviewFail, id));
    }

    public Map<String, String> addLikeToReviewSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(addLikeToReviewSuccess, id, userId));
    }

    public Map<String, String> addLikeToReviewFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(addLikeToReviewFail, id, userId));
    }

    public Map<String, String> addDislikeToReviewSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(addDislikeToReviewSuccess, id, userId));
    }

    public Map<String, String> addDislikeToReviewFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(addDislikeToReviewFail, id, userId));
    }

    public Map<String, String> deleteReviewLikeOrDislikeSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(deleteLikeOrDislikeToReviewSuccess, id, userId));
    }

    public Map<String, String> deleteReviewLikeOrDislikeFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(deleteLikeOrDislikeToReviewFail, id, userId));
    }

    public Map<String, String> deleteReviewDislikeSuccessAnswer(long id, long userId) {
        return Map.of(success, String.format(deleteDislikeToReviewSuccess, id, userId));
    }

    public Map<String, String> deleteReviewDislikeFailAnswer(long id, long userId) {
        return Map.of(fail, String.format(deleteDislikeToReviewFail, id, userId));
    }
}
