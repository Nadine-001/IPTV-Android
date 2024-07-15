package com.example.tvapp.retrofit


import com.example.tvapp.AboutModel
import com.example.tvapp.AddFoodToCartRequest
import com.example.tvapp.AddToCartRequest
import com.example.tvapp.AdsModel
import com.example.tvapp.CartModelItem
import com.example.tvapp.CartServiceModelItem
import com.example.tvapp.ChannelModelItem
import com.example.tvapp.DaftarMenuModelItem
import com.example.tvapp.DaftarMenuPerCategoryModelItem
import com.example.tvapp.DescriptionModel
import com.example.tvapp.FacilityModelItem
import retrofit2.Call
import com.example.tvapp.GreetingModel
import com.example.tvapp.HomeModel
import com.example.tvapp.MenuModelApiItem
import com.example.tvapp.MenuType
import com.example.tvapp.ModelPaymentMethod
import com.example.tvapp.ModelShowQRAgain
import com.example.tvapp.OrderX
import com.example.tvapp.PendingTransaksiModel
import com.example.tvapp.PostOrderFoodModel
import com.example.tvapp.PostOrderResponse
import com.example.tvapp.PostPaymentStatusModel
import com.example.tvapp.PostPaymentStatusResponse
import com.example.tvapp.PostReqServiceModel
import com.example.tvapp.PutPaymentMethodModel
import com.example.tvapp.QRISModel
import com.example.tvapp.RoomHeaderModel
//import com.example.tvapp.SaveQRCodeRequest
import com.example.tvapp.SaveQRCodeResponse
import com.example.tvapp.ServiceCartModel
import com.example.tvapp.ServicesData
import com.example.tvapp.statusmodel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiEndpoint {
    @GET("greeting")
    fun getGreeting(@Query("mac_address") macAddress: String): Call<GreetingModel>

    @GET("about")
    fun getAbout(@Query("mac_address") macAddress: String): Call<AboutModel>

    @GET("room_about")
    fun getDesc(@Query("mac_address") macAddress: String): Call<DescriptionModel>

    @GET("room_header")
    fun getHeader(@Query("mac_address") macAddress: String): Call<RoomHeaderModel>

    @GET("home")
    fun getHome(@Query("mac_address") macAddress: String): Call<HomeModel>

    @GET("facilites")
    fun getfacilities(@Query("mac_address") macAddress: String): Call<List<FacilityModelItem>>

    @GET("room_service")
    fun getService(@Query("mac_address") macAddress: String): Call<List<ServicesData>>

    @GET("menu_cart")
    fun getCartService(@Query("mac_address") macAddress: String): Call<List<CartModelItem>>


    @GET("ads_lips")
    fun getHeaderFood(@Query("mac_address") macAddress: String): Call<AdsModel>

    @GET("menu_list")
    fun getMenuList(@Query("mac_address") macAddress: String, @Query("menu_type_id") categoryId: Int): Call<List<DaftarMenuPerCategoryModelItem>>

    @GET("service_cart")
    fun getCartServiceList(@Query("mac_address") macAddress: String): Call<List<CartServiceModelItem>>

    @GET("menu_cart")
    fun getCartMenuList(@Query("mac_address") macAddress: String): Call<List<OrderX>>

    @GET("menu_type")
    fun getCategoryList(@Query("mac_address") macAddress: String): Call<List<MenuType>>

    @GET("channel")
    fun getChannel(@Query("mac_address") macAddress: String): Call<List<ChannelModelItem>>

    @GET("show_qr_code")
    fun getShowQRAgain(@Query("food_request_id") databaseId: Int): Call<ModelShowQRAgain>

    @GET("pending_transaction")
    fun getPending(@Query("mac_address") macAddress: String): Call<PendingTransaksiModel>

    @GET("payment_method")
    fun getPaymentMethod(@Query("food_request_id") databaseId: Int): Call<ModelPaymentMethod>


    @Headers("Content-Type: application/json")
    @PUT("payment_method")
    fun putPayment(@Body newPaymentPut: PutPaymentMethodModel): Call<PutPaymentMethodModel>

//    @POST("add_service_to_cart")
//    fun addToCartService(@Field("mac_address") mac_address: String,
//                         @Field("service_id") service_id: Int,)

//    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @POST("add_service_to_cart")
    fun addToCartService(@Body newCartService: AddToCartRequest): Call<AddToCartRequest>

    @Headers("Content-Type: application/json")
    @POST("request_service")
    fun postReqService(@Body newReqService: PostReqServiceModel): Call<PostReqServiceModel>

    @Headers("Content-Type: application/json")
    @POST("food_order")
    fun postReqFood(@Body newReqFood: PostOrderFoodModel): Call<PostOrderResponse>

//    @Headers("Content-Type: application/json")
//    @POST("payment_status")
//    fun postPaymentStatus(@Body newPaymentStatusList: List<PostPaymentStatusModel>): Call<List<PostPaymentStatusModel>>
@Headers("Content-Type: application/json")
@POST("payment_status")
fun postPaymentStatus(@Body newPaymentStatus: PostPaymentStatusModel): Call<PostPaymentStatusModel>

    @Headers("Content-Type: application/json")
    @POST("add_menu_to_cart")
    fun addToCartFood(@Body newCartFood: AddFoodToCartRequest): Call<AddFoodToCartRequest>


//    @Multipart
//    @POST("save_qr_code")
//    fun saveQRCode(
//        @Part("food_request_id") foodRequestId: RequestBody,
//        @Part qrCode: MultipartBody.Part
//    ): Call<SaveQRCodeResponse>

    @DELETE("delete_request/{item_id}")
    fun deleteRequest(@Path("item_id") item_id: Int, @Query("mac_address") mac_address: String): Call<String>

    @DELETE("delete_order/{item_id}")
    fun deleteRequestMenu(@Path("item_id") item_id: Int, @Query("mac_address") mac_address: String): Call<String>

    @GET("qr_code")
    fun getQRIS(@Query("mac_address") macAddress: String): Call<QRISModel>

    @Headers(
        "Accept: application/json",
        "Authorization: Basic U0ItTWlkLXNlcnZlci1RS1F1dHZoaUFtUW1CeTktTjlKb0ZRaEM6",
        "Content-Type: application/json"
    )
    @GET("{order_id}/status")
    fun getStatusMID(@Path("order_id") orderId: String): Call<statusmodel>




//    @POST("add_service_to_cart")
//    fun addServiceToCart(@Body request: AddToCartRequest): Call<String>

//    @POST("add_service_to_cart/{service_id}")
//    fun addServiceToCart(@Path("service_id") serviceId: Int, @Body request: AddToCartRequest): Call<String>
}
