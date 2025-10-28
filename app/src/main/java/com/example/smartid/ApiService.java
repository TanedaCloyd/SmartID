package com.example.smartid;

// *** DELETE ALL THE HELPER CLASS DEFINITIONS FROM THIS FILE ***

import okhttp3.MultipartBody; // Keep necessary imports
import retrofit2.Call;
import retrofit2.http.*;

// Keep only the interface definition
public interface ApiService {

    // --- AUTH ENDPOINTS ---
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest body); // Uses AuthResponse, AuthRequest

    @POST("/api/auth/register")
    Call<LoggedInUser> register(@Body RegisterRequest body); // Uses LoggedInUser, RegisterRequest
    // --- END NEW ---

    @POST("/api/students/{rfid}/add-balance")
    Call<AddBalanceResponse> addBalance( // Uses AddBalanceResponse, AddBalanceRequest
                                         @Path("rfid") String rfid,
                                         @Body AddBalanceRequest body
    );

    @Multipart
    @POST("/api/upload-image")
    Call<UploadResponse> uploadImage( // Uses UploadResponse
                                      @Part MultipartBody.Part imageFile
    );

    @PUT("/api/students/{rfid}")
    Call<User> updateStudent( // Uses User, UpdateUserRequest
                              @Path("rfid") String rfid,
                              @Body UpdateUserRequest body
    );

    @GET("/api/students/{identifier}")
    Call<StudentProfile> getStudentProfile(@Path("identifier") String rfidOrStudentId); // Uses StudentProfile
}