package com.aviro.android.common

import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.DefaultTrackingOptions
import com.amplitude.android.events.Identify
import com.aviro.android.BuildConfig
import com.aviro.android.data.datasource.datastore.DataStoreDataSource
import com.aviro.android.data.datasource.member.MemberDataSource
import com.aviro.android.data.model.menu.MenuDTO
import com.aviro.android.domain.entity.auth.User
import com.aviro.android.domain.entity.menu.Menu
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


enum class UserType(
    val typeName : String
) {
    signUpClick("signup_click"),
    signUpComplete("signup_complete"),
    userLogin("login_complete"),
    userLogout("logout_complete"),
    withdrawal("withdrawal_complete")
}

enum class SearchType(
    val typeName : String
) {
    searchInput("search_enter_term"),  // 검색어를 입력해 검색 요청
    searchClick("search_click_result") // 검색어 클릭

}

enum class BookmarkType(
    val typeName : String
) {
    bookmarkOfPlaceClick("bookmark_click_in place"), // 가게를 북마크 설정
    bookmarkOfMapClick("bookmark_click_in map"),   // 맵에서 북마크 보기 버튼 클릭
    bookmarkListClick("bookmark_click_list"),     // 마이페이지-북마크 리스트 보기 클릭

}

enum class ChallengeType(
    val typeName : String
) {
    levelupDidMove("challenge_click_checking_levelup"),
    challPresent("challenge_view"), // 챌린지 화면 보여짐
}

enum class View(val type : String) {
    HOME("scroll in home tab"),
    MENU("click menu tab"),
    REVIEW("click review tab")
}

enum class Category(val type : String) {
    DISH("식당"),
    CAFE("카페"),
    BREAD("빵집"),
    BAR("술집")
}

enum class PlaceType(
    val typeName : String
) {

    placeSheetShow("place_view_sheet"),  // 바텀시트 보여짐
    placeSheetHalf("place_view_half"),   // 바텀시트 2단계
    placeMenuShow("place_view_menu"),    // 메뉴 보여짐 (홈 탭에서 스크롤 or 메뉴 탭에서)
    placeReviewShow("place_view_review"), // 리뷰 보여짐 (홈 탭에서 스크롤 or 리뷰 탭에서)

    reviewUploadClick("review_view_upload"),  // 리뷰 업로드 클릭
    reviewUploadComplete("review_complete_upload"),  // 리뷰 업로드 완료

    placeUploadClick("place_view_upload"),  // 가게 업로드 클릭
    placeUploadComplete("place_complete_upload"),  // 가게 업로드 완료

    placeEditClick("place_click_edit_place"),       // 가게 수정 클릭
    placeEditComplete("place_complete_edit_place"), // 가게 수정 완료

    menuEditClick("place_click_edit_menu"), // 메뉴 수정 클릭
    menuEditComplete("place_complete_edit_menu"), // 메뉴 수정 완료

    placeRemoveClick("place_click_remove"),  // 가게 신고 클릭
    placeRemoveComplete("place_complete_remove")  // 가게 신고 완료

}




object AmplitudeUtils  {

    // Application 에서 한번만 하고, amplitude를 넘겨줌
    // 만약 amplitude 변수가 유효하지 않으면 다시 초기화
    var amplitude : Amplitude? = null
    fun init(amplitude : Amplitude) {
        this.amplitude = amplitude
    }

    fun getDate(): String {
        val dateFormat = "YYYY-MM-dd"
        val date = Date(System.currentTimeMillis())
        val DateFormat = SimpleDateFormat(dateFormat)
        val formattedDate = DateFormat.format(date)

        return formattedDate

    }

    fun getDateTime() : String  {
        val dateFormat = "YYYY-MM-dd HH:mm"
        val date = Date(System.currentTimeMillis())
        val DateFormat = SimpleDateFormat(dateFormat)
        val formattedDate = DateFormat.format(date)

        return formattedDate
    }


    fun login(userId : String , name : String?,  nickname : String, birthday : String?, gender :String?, email : String?, type: String) {

        val identify = Identify()

        amplitude?.setUserId(userId) // userID 정보 변경

        identify.set("name", name ?: "")
        identify.set("email",email ?: "")
        identify.set("nickname", nickname)
        identify.set("birthday", birthday ?: "")
        identify.set("gender", gender ?: "")
        identify.set("version", BuildConfig.VERSION_NAME)
        identify.set("auth_type", type) // sns 값 추가


        amplitude?.identify(identify)
        val date = getDate()
        amplitude?.track(UserType.userLogin.typeName,
            mutableMapOf("auth_type" to type, "login_date" to date))
    }


    // 닉네임 수정시
    fun updateNickname(nickname : String) {
        amplitude?.identify(mapOf("nickname" to nickname))
    }

    fun updateTotalReviews(total_review : Int) {
        amplitude?.identify(mapOf("total_review" to total_review))
    }

    fun updateTotalPlaces(total_place : Int) {
        amplitude?.identify(mapOf("total_place" to total_place))
    }

    fun updateTotalBookmarks(total_bookmark : Int) {
        amplitude?.identify(mapOf("total_bookmark" to total_bookmark))
    }

    fun updateReviewDateFirst() {
        val review_date_first = getDate()
        amplitude?.identify(mapOf("review_date_first" to review_date_first))
    }

    fun updateReviewDateLast() {
        val review_date_last = getDate()
        amplitude?.identify(mapOf("review_date_last" to review_date_last))
    }

    fun updatePlaceDateFirst() {
        val place_date_first = getDate()
        amplitude?.identify(mapOf("place_date_first" to place_date_first))
    }

    fun updatePlaceDateLast() {
        val place_date_last = getDate()
        amplitude?.identify(mapOf("place_date_last" to place_date_last))
    }


    fun signUpClick(type : String) { // userId: String,
        amplitude?.track(UserType.signUpClick.typeName,
            mutableMapOf("auth_type" to type)
        )
    }

    fun signUpComplete(type : String) { //userId: String,
       val date = getDate()
        amplitude?.track(UserType.signUpComplete.typeName,
            mutableMapOf("signup_date" to date, "auth_type" to type)
        )

        amplitude?.identify(mapOf("signup_date" to date))
    }

    fun withdrawal() {
        val date = getDate()
        amplitude?.track(UserType.withdrawal.typeName,
            mutableMapOf( "login_date" to date))
    }

    fun logout( type : String) {
        val date = getDateTime()
        amplitude?.track(UserType.userLogout.typeName,
            mutableMapOf("auth_type" to type, "logout_date" to date))
    }

    fun enterKeyword(search_path : String, number: Int, keywords : String) {
        amplitude?.track(SearchType.searchInput.typeName,
            mutableMapOf("search_path" to search_path, "number" to number, "keywords" to keywords))
    }

    fun searchKeyword(placeId : String, place_name : String, keywords : String, category: String, rank : Int) {
        amplitude?.track(SearchType.searchClick.typeName,
            mutableMapOf("placeId" to placeId, "place_name" to place_name, "category" to category,
                "keywords" to keywords, "rank" to rank))
    }

    fun clickPlaceBookmark(placeId : String, place_name : String, address : String, category: String) {
        amplitude?.track(BookmarkType.bookmarkOfPlaceClick.typeName,
            mutableMapOf("placeId" to placeId, "place_name" to place_name,
                "address" to address, "category" to category))
    }

    fun clickMapBookmark() {
        amplitude?.track(BookmarkType.bookmarkOfMapClick.typeName)
    }

    fun bookmarkListPresent(number : Int) {
        amplitude?.track(BookmarkType.bookmarkListClick.typeName,
            mutableMapOf("number" to number))
    }

    fun placePresentFirst(place_id: String, place_name : String, category: String, view_path_browse_place: String,
                         toggle_restaurant_activated : Boolean, toggle_cafe_activated : Boolean,
                          toggle_bakery_activated : Boolean, toggle_bar_activated : Boolean) { // 가게명

        amplitude?.track(
            PlaceType.placeSheetShow.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "view_path_browse_place" to view_path_browse_place,
                "toggle_restaurant_activated" to toggle_restaurant_activated, "toggle_cafe_activated" to toggle_cafe_activated,
                "toggle_bar_activated" to toggle_bar_activated, "toggle_bakery_activated" to toggle_bakery_activated)
        )
    }


    fun placePresentHalf(place_id: String, place_name : String, category: String) { // 가게명
        amplitude?.track(
            PlaceType.placeSheetHalf.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category)
            )
    }



    fun placePresentMenu(place_id: String, place_name : String, category: String, view_path_browse_menu : String) { // 가게명
        amplitude?.track(
            PlaceType.placeMenuShow.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "view_path_browse_menu" to view_path_browse_menu)
        )
    }

    fun placePresentReview(place_id: String, place_name : String, category: String, view_path_browse_review : String) { // 가게명
        amplitude?.track(
            PlaceType.placeReviewShow.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "view_path_browse_review" to view_path_browse_review)
        )
    }


    fun placeUploadClick() {
        amplitude?.track(PlaceType.placeUploadClick.typeName)
    }

    fun placeUploadComplete(place_id: String, place_name : String, category: String,
                            menu_number : Int) {
        amplitude?.track(PlaceType.placeUploadComplete.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "menu_number" to menu_number)
        )
    }

    fun reviewUploadClick(place_id: String, place_name : String, category: String, view_path_upload_review : String) {
        amplitude?.track(PlaceType.reviewUploadClick.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "view_path_upload_review" to view_path_upload_review)
        )
    }

    fun reviewUploadComplete(place_id: String, place_name : String, category: String,
                             review_id : String,  review : String) {
        amplitude?.track(PlaceType.reviewUploadComplete.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category,
                "review_id" to review_id, "review" to review)
        )
    }

    // 레벨업 확인하러 가기
    fun levelupMove(select : Boolean) {
        amplitude?.track(ChallengeType.levelupDidMove.typeName,
            mutableMapOf("select" to select))
    }

    // 챌린지 화면 가기
    fun challengePresent() {
        amplitude?.track( ChallengeType.challPresent.typeName)
    }


    fun placeEditClick(place_id: String, place_name : String, category: String) {
        amplitude?.track(
           PlaceType.placeEditClick.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category)
        )
    }

    fun placeEditComplete(edit_category: String, edit_before : String, edit_detail : String,
                          place_id: String, place_name : String, category: String) {

        val edit_date = getDateTime()
        amplitude?.track(
            PlaceType.placeEditComplete.typeName,
            mutableMapOf("edit_category" to edit_category, "edit_before" to edit_before, "edit_detail" to edit_detail,
                "edit_date" to edit_date, "place_id" to place_id, "place_name" to place_name, "category" to category)
        )
    }

    fun menuEditClick(place_id: String, place_name : String, category: String) {
        amplitude?.track(
            PlaceType.menuEditClick.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category)
        )
    }

    fun menuEditComplete(number_before : Int, number_detail : Int,
                          place_id: String, place_name : String, category: String) {

        val edit_date = getDateTime()
        amplitude?.track(
            PlaceType.menuEditComplete.typeName,
            mutableMapOf("number_before" to number_before, "number_detail" to number_detail,
                "edit_date" to edit_date, "place_id" to place_id, "place_name" to place_name, "category" to category)
        )
    }

    fun placeRemoveClick(place_id: String, place_name : String, category: String) {
        amplitude?.track(
            PlaceType.placeRemoveClick.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category)
        )
    }

    fun placeRemoveComplete(place_id: String, place_name : String, category: String) {
        val edit_date = getDateTime()
        amplitude?.track(
            PlaceType.placeRemoveComplete.typeName,
            mutableMapOf("place_id" to place_id, "place_name" to place_name, "category" to category, "edit_date" to edit_date)
        )
    }

}
